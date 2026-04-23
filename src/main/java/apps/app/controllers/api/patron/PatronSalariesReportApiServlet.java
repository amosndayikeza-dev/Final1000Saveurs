package apps.app.controllers.api.patron;

import apps.app.dao.SalaryReportDAO;
import apps.app.models.SalaryReport;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/patron/reports/salaries")
public class PatronSalariesReportApiServlet extends HttpServlet {

    private SalaryReportDAO salaryReportDAO = new SalaryReportDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String deptIdStr = req.getParameter("departement_id");
        resp.setContentType("application/json");
        List<SalaryReport> reports;
        if (deptIdStr != null && !deptIdStr.isEmpty()) {
            int deptId = Integer.parseInt(deptIdStr);
            reports = salaryReportDAO.findByDepartement(deptId);
        } else {
            reports = salaryReportDAO.findAll();
        }
        resp.getWriter().write(gson.toJson(reports));
    }
}