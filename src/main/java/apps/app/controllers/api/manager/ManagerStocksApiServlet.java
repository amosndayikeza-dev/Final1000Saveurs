package apps.app.controllers.api.manager;

import apps.app.dao.ProductDAO;
import apps.app.dao.StockMovementDAO;
import apps.app.models.Product;
import apps.app.models.StockMovement;
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
import java.util.List;
import java.util.Map;

@WebServlet("/api/manager/stocks/*")
public class ManagerStocksApiServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private StockMovementDAO stockMovementDAO = new StockMovementDAO();
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
                List<Product> products = productDAO.findByDepartement(departementId);
                resp.getWriter().write(gson.toJson(products));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Product product = productDAO.findById(id);
                if (product == null || product.getDepartementId() != departementId) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    resp.getWriter().write(gson.toJson(product));
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId");
        Integer userId = (Integer) session.getAttribute("userId");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int productId = Integer.parseInt(pathInfo.substring(1));
        BufferedReader reader = req.getReader();
        Map<String, Object> data = gson.fromJson(reader, Map.class);
        int adjustment = ((Double) data.get("adjustment")).intValue();
        String reason = (String) data.get("reason");

        Product product = productDAO.findById(productId);
        if (product == null || product.getDepartementId() != departementId) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int newStock = product.getCurrentStock() + adjustment;
        if (newStock < 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Stock négatif impossible\"}");
            return;
        }
        product.setCurrentStock(newStock);
        productDAO.update(product);

        StockMovement movement = new StockMovement();
        movement.setProductId(productId);
        movement.setQuantity(Math.abs(adjustment));
        movement.setType(adjustment >= 0 ? "in" : "out");
        movement.setReason(reason);
        movement.setCreatedBy(userId);
        stockMovementDAO.create(movement);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(product));
    }
}