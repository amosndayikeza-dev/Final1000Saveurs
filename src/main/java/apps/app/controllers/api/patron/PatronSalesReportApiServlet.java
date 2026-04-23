package apps.app.controllers.api.patron;

import apps.app.dao.SaleDAO;
import apps.app.dao.DepartementDAO;
import apps.app.models.Sale;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/patron/reports/sales")
public class PatronSalesReportApiServlet extends HttpServlet {

    private SaleDAO saleDAO = new SaleDAO();
    private DepartementDAO departementDAO = new DepartementDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String deptIdStr = req.getParameter("departement_id");
        String startDateStr = req.getParameter("start_date");
        String endDateStr = req.getParameter("end_date");

        resp.setContentType("application/json");
        try {
            List<Sale> sales;
            if (deptIdStr != null && !deptIdStr.isEmpty()) {
                int deptId = Integer.parseInt(deptIdStr);
                if (startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()) {
                    Date start = Date.valueOf(startDateStr);
                    Date end = Date.valueOf(endDateStr);
                    sales = saleDAO.findByDepartementAndDate(deptId, start, end);
                } else {
                    sales = saleDAO.findByDepartement(deptId);
                }
            } else {
                if (startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()) {
                    Date start = Date.valueOf(startDateStr);
                    Date end = Date.valueOf(endDateStr);
                    sales = saleDAO.findByDateRange(start, end);
                } else {
                    sales = saleDAO.findAll();
                }
            }
            resp.getWriter().write(gson.toJson(sales));
        } catch (SQLException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}