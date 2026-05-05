package apps.app.controllers.api.patron;

import apps.app.dao.DebtDAO;
import apps.app.dao.DepartementDAO;
import apps.app.dao.EmployeeDAO;
import apps.app.dao.UserDAO;
import apps.app.models.Debt;
import apps.app.models.Departement;
import apps.app.models.Employee;
import apps.app.models.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/patron/reports/debts")
public class PatronDebtsReportApiServlet extends HttpServlet {
    private DebtDAO debtDAO = new DebtDAO();
    private DepartementDAO departementDAO = new DepartementDAO();
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private UserDAO userDAO = new UserDAO();
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1. Récupérer les paramètres
        String status = req.getParameter("status");        // "pending" ou "paid"
        String deptIdStr = req.getParameter("departement_id");

        resp.setContentType("application/json");
        try {
            // 2. Charger les dettes selon les filtres
            List<Debt> debts = List.of();
            if (deptIdStr != null && !deptIdStr.isEmpty()) {
                int deptId = Integer.parseInt(deptIdStr);
                debts = debtDAO.findByDepartementAndStatus(deptId, status);
            } else {
                if (status != null && !status.isEmpty()) {
                    debtDAO.findByStatus(status);
                } else {
                    debts = debtDAO.findAll();
                }
            }

            // 3. Enrichir chaque dette et calculer les totaux
            List<Map<String, Object>> enrichedDebts = new ArrayList<>();
            double totalPending = 0.0;
            double totalPaid = 0.0;

            for (Debt debt : debts) {
                Map<String, Object> enriched = new HashMap<>();
                enriched.put("id", debt.getId());
                enriched.put("debtor_type", debt.getDebtorType());
                enriched.put("debtor_name", debt.getDebtorName() != null ? debt.getDebtorName() : "");
                enriched.put("amount", debt.getAmount());
                enriched.put("due_date", debt.getDueDate());
                enriched.put("status", debt.getStatus());
                enriched.put("paid_at", debt.getPaidAt());
                enriched.put("paid_amount", debt.getPaidAmount());

                // Nom du département (via une méthode du DAO qui fait la jointure)
                String deptName = debtDAO.findDepartementNameByDebtId(debt.getId());
                enriched.put("departement_name", deptName != null ? deptName : "Inconnu");

                // Nom de l'employé si dette employé
                String employeeName = "";
                if ("employee".equals(debt.getDebtorType()) && debt.getEmployeeId() != null) {
                    Employee emp = employeeDAO.findById(debt.getEmployeeId());
                    if (emp != null && emp.getUserId() != 0) {
                        Users user = userDAO.findById(emp.getUserId());
                        if (user != null) {
                            employeeName = user.getFirstName() + " " + user.getLastName();
                        }
                    }
                }
                enriched.put("employee_name", employeeName);

                enrichedDebts.add(enriched);

                // Cumul des totaux
                if ("pending".equals(debt.getStatus())) {
                    totalPending += debt.getAmount();
                } else if ("paid".equals(debt.getStatus())) {
                    double paid = (debt.getPaidAmount() != null) ? debt.getPaidAmount() : debt.getAmount();
                    totalPaid += paid;
                }
            }

            // 4. Construire la réponse finale
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("debts", enrichedDebts);
            responseData.put("total_pending", totalPending);
            responseData.put("total_paid", totalPaid);
            responseData.put("count", debts.size());

            resp.getWriter().write(gson.toJson(responseData));

        } catch (SQLException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}