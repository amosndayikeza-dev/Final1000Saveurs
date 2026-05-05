package apps.app.controllers.api.manager;

import apps.app.dao.DebtDAO;
import apps.app.dao.SaleItemDAO;
import apps.app.dao.SaleDAO;
import apps.app.models.Debt;
import apps.app.models.SaleItem;
import apps.app.models.Sale;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/manager/debts/*")
public class ManagerDebtsApiServlet extends HttpServlet {

    private DebtDAO debtDAO = new DebtDAO();
    private SaleItemDAO saleItemDAO = new SaleItemDAO();
    private SaleDAO saleDAO = new SaleDAO(); // Ajouté pour vérifier l'appartenance
    private Gson gson = new Gson();

    // ============================ GET ============================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("departementId") == null) {
            sendError(resp, 401, "Non authentifié");
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId");
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Debt> debts = debtDAO.findByDepartement(departementId);
                resp.getWriter().write(gson.toJson(debts));
                return;
            }
            if (pathInfo.matches("/\\d+")) {
                int id = Integer.parseInt(pathInfo.substring(1));
                Debt debt = debtDAO.findById(id);
                if (debt == null) {
                    sendError(resp, 404, "Dette non trouvée");
                    return;
                }
                // Vérifier que la dette appartient au département (via la vente)
                if (!belongsToDepartement(debt, departementId)) {
                    sendError(resp, 403, "Accès non autorisé");
                    return;
                }
                resp.getWriter().write(gson.toJson(debt));
                return;
            }
            sendError(resp, 404, "Endpoint inconnu");
        } catch (NumberFormatException e) {
            sendError(resp, 400, "ID invalide");
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur base de données");
        }
    }

    // ============================ POST (paiement) ============================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("departementId") == null) {
            sendError(resp, 401, "Authentification requise");
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+/pay")) {
            sendError(resp, 400, "URL invalide. Utilisez POST /{id}/pay");
            return;
        }
        int debtId;
        try {
            debtId = Integer.parseInt(pathInfo.split("/")[1]);
        } catch (NumberFormatException e) {
            sendError(resp, 400, "ID dette invalide");
            return;
        }

        // Lecture JSON
        Map<String, Object> data;
        try (BufferedReader reader = req.getReader()) {
            data = gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            sendError(resp, 400, "JSON invalide");
            return;
        }

        // Validation du montant
        if (!data.containsKey("paid_amount")) {
            sendError(resp, 400, "paid_amount requis");
            return;
        }
        double paidAmount;
        try {
            paidAmount = ((Number) data.get("paid_amount")).doubleValue();
        } catch (Exception e) {
            sendError(resp, 400, "Le montant  doit être un nombre");
            return;
        }
        if (paidAmount <= 0) {
            sendError(resp, 400, "Le montant doit être positif");
            return;
        }

        try {
            Debt debt = debtDAO.findById(debtId);
            if (debt == null) {
                sendError(resp, 404, "Dette non trouvée");
                return;
            }

            // Vérification d'appartenance au département
            if (!belongsToDepartement(debt, departementId)) {
                sendError(resp, 403, "Cette dette n'appartient pas à votre département");
                return;
            }

            if ("paid".equals(debt.getStatus())) {
                sendError(resp, 400, "Dette déjà payée");
                return;
            }

            // Calcul des montants (structure correcte : amount = montant initial, paid_amount = cumul payé)
            double initialAmount = debt.getAmount();
            double alreadyPaid = debt.getPaidAmount() != null ? debt.getPaidAmount() : 0.0;
            double newPaid = alreadyPaid + paidAmount;

            if (newPaid > initialAmount + 0.001) { // petite tolérance
                sendError(resp, 400, "Le montant total payé dépasse la dette");
                return;
            }

            boolean isFullyPaid = Math.abs(newPaid - initialAmount) < 0.001;
            debt.setPaidAmount(newPaid);
            if (isFullyPaid) {
                debt.setStatus("paid");
                debt.setPaidAt(Date.valueOf(LocalDate.now()));
            } else {
                debt.setStatus("partial");
            }

            // Mise à jour en base (sans transaction simple – risque minime)
            debtDAO.update(debt);

            // Si la dette est totalement payée, marquer le SaleItem correspondant comme payé
            if (isFullyPaid) {
                SaleItem item = saleItemDAO.findById(debt.getSaleItemId());
                if (item != null && !item.isPaid()) {
                    item.setPaid(true);
                    saleItemDAO.update(item);
                }
            }

            // Réponse avec la dette mise à jour
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(debt));

        } catch (SQLException e) {
            sendError(resp, 500, "Erreur lors du paiement : " + e.getMessage());
        }
    }

    // ============================ Utilitaire ============================
    private boolean belongsToDepartement(Debt debt, int departementId) throws SQLException {
        // Récupérer le saleItem, puis la sale, puis vérifier le departement_id
        SaleItem item = saleItemDAO.findById(debt.getSaleItemId());
        if (item == null) return false;
        Sale sale = saleDAO.findById(item.getSaleId());
        return sale != null && sale.getDepartementId() == departementId;
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        resp.getWriter().write(gson.toJson(error));
    }


}