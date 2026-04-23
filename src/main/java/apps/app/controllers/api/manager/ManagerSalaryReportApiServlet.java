package apps.app.controllers.api.manager;

import apps.app.dao.EmployeeDAO;
import apps.app.dao.SalaryReportDAO;
import apps.app.models.SalaryReport;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

@WebServlet("/api/manager/salary-report")
public class ManagerSalaryReportApiServlet extends HttpServlet {

    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private SalaryReportDAO salaryReportDAO = new SalaryReportDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("departementId") == null || session.getAttribute("userId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"Session invalide (departementId manquant)\"}");
            return;
        }

        Integer departmentId = (Integer) session.getAttribute("departementId");
        Integer managerId = (Integer) session.getAttribute("userId");
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        try {
            // Vérifier si déjà envoyé
            if (salaryReportDAO.existsByDepartementAndMonthYear(departmentId, month, year)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"Rapport déjà envoyé pour ce mois\"}");
                return;
            }
            double totalSalary = employeeDAO.getTotalSalaryByDepartement(departmentId);
            SalaryReport report = new SalaryReport();
            report.setDepartmentId(departmentId);
            report.setManagerId(managerId);
            report.setMonth(month);
            report.setYear(year);
            report.setTotalSalary(totalSalary);
            report.setStatus("pending");
            report.setSubmittedAt(new Timestamp(System.currentTimeMillis()));
            salaryReportDAO.create(report);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(report));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}