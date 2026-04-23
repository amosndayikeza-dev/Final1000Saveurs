package apps.app.controllers.api.patron;

import apps.app.dao.ProductDAO;
import apps.app.models.Product;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/patron/reports/stocks")
public class PatronStocksReportApiServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String deptIdStr = req.getParameter("department_id");
        resp.setContentType("application/json");
        List<Product> products;
        if (deptIdStr != null && !deptIdStr.isEmpty()) {
            int deptId = Integer.parseInt(deptIdStr);
            products = productDAO.findByDepartement(deptId);
        } else {
            products = productDAO.findAll();
        }
        resp.getWriter().write(gson.toJson(products));
    }
}