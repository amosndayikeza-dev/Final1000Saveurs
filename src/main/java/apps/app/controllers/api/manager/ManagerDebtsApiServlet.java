package apps.app.controllers.api.manager;

import apps.app.dao.DebtDAO;
import apps.app.dao.SaleItemDAO;
import apps.app.models.Debt;
import apps.app.models.SaleItem;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet("/api/manager/debts/*")
public class ManagerDebtsApiServlet extends HttpServlet {

    private DebtDAO debtDAO = new DebtDAO();
    private SaleItemDAO saleItemDAO = new SaleItemDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("departementId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId");
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Liste des dettes du département
                List<Debt> debts = debtDAO.findByDepartement(departementId);
                resp.getWriter().write(gson.toJson(debts));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Debt debt = debtDAO.findById(id);
                if (debt == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    resp.getWriter().write(gson.toJson(debt));
                }
            }
        } catch (SQLException | NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST: enregistrer un paiement partiel ou total
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int debtId = Integer.parseInt(pathInfo.substring(1));
        BufferedReader reader = req.getReader();
        Map<String, Object> data = gson.fromJson(reader, Map.class);
        double paidAmount = ((Number) data.get("paid_amount")).doubleValue();
        try {
            Debt debt = debtDAO.findById(debtId);
            if (debt == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (debt.getStatus().equals("paid")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Dette déjà payée\"}");
                return;
            }
            double remaining = debt.getAmount() - paidAmount;
            double newPaidAmount = (debt.getPaidAmount() == null ? 0 : debt.getPaidAmount()) + paidAmount;
            if (remaining < 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Montant trop élevé\"}");
                return;
            }
            if (remaining == 0) {
                debt.setStatus("paid");
                debt.setPaidAt(Date.valueOf(LocalDate.now()));
                debt.setPaidAmount(newPaidAmount);
                debtDAO.update(debt);
                // Marquer le saleItem comme payé
                SaleItem item = saleItemDAO.findById(debt.getSaleItemId());
                item.setPaid(true);
                saleItemDAO.update(item);
            } else {
                debt.setAmount(remaining);
                debt.setPaidAmount(newPaidAmount);
                debtDAO.update(debt);
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(debt));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}