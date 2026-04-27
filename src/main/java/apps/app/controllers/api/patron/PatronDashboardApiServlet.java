package apps.app.controllers.api.patron;

import apps.app.dao.*;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/api/patron/dashboard")
public class PatronDashboardApiServlet extends HttpServlet {
    private DepartementDAO departementDAO = new DepartementDAO();
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private SaleDAO saleDAO = new SaleDAO();
    private SalaryReportDAO salaryReportDAO = new SalaryReportDAO();
    private DebtDAO debtDAO = new DebtDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int deptCount = departementDAO.findAll().size();
            int empCount = employeeDAO.findAll().size();

            // Ventes du jour
            java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());
            double todaySales = saleDAO.getTotalSalesByPeriod(today, today);

            // Dettes en cours (pending)
            double pendingDebts = debtDAO.getTotalPendingDebts();

            // Nombre de rapports (ex: salary_reports) - optionnel, vous pouvez retourner 0 par défaut
            int reportsCount = 0;

            Map<String, Object> data = new HashMap<>();
            data.put("departementsCount", deptCount);
            data.put("employeesCount", empCount);
            data.put("todaySales", todaySales);
            data.put("pendingDebts", pendingDebts);
            data.put("reportsCount", reportsCount);

            // Ajouter des activités récentes si vous avez des endpoints dédiés

            Map<String,String> lastEmployee = employeeDAO.getLastEmployee();
            if(lastEmployee != null) data.put("recentEmployee",lastEmployee);

            Map<String,String> lastDepartement = departementDAO.getLastDepartement();
            if (lastDepartement != null) data.put("recentDepartement",lastDepartement);

            Map<String, String> lastReport = salaryReportDAO.getLastSalaryReport();
            if (lastReport != null) data.put("recentReport", lastReport);

            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(data));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}