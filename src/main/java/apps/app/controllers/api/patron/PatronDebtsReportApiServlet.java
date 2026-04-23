package apps.app.controllers.api.patron;

import apps.app.dao.DebtDAO;
import apps.app.models.Debt;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/patron/reports/debts")
public class PatronDebtsReportApiServlet extends HttpServlet {
    private DebtDAO debtDAO = new DebtDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String status = req.getParameter("status"); // "pending" ou "paid"
        String deptIdStr = req.getParameter("department_id");
        resp.setContentType("application/json");
        try {
            List<Debt> debts = List.of();
            if (deptIdStr != null && !deptIdStr.isEmpty()) {
                int deptId = Integer.parseInt(deptIdStr);
                debts = debtDAO.findByDepartmentAndStatus(deptId, status);
            } else {
                if (status != null && !status.isEmpty()) {
                    debtDAO.findByStatus(status);
                } else {
                    debts = debtDAO.findAll();
                }
            }
            resp.getWriter().write(gson.toJson(debts));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}