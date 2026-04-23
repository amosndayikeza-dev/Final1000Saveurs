package apps.app.controllers.api.manager;

import apps.app.dao.SaleDAO;
import apps.app.dao.SaleItemDAO;
import apps.app.dao.ProductDAO;
import apps.app.dao.StockMovementDAO;
import apps.app.dao.DebtDAO;
import apps.app.models.Sale;
import apps.app.models.SaleItem;
import apps.app.models.Product;
import apps.app.models.StockMovement;
import apps.app.models.Debt;
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

@WebServlet("/api/manager/sales/*")
public class ManagerSalesApiServlet extends HttpServlet {

    private SaleDAO saleDAO = new SaleDAO();
    private SaleItemDAO saleItemDAO = new SaleItemDAO();
    private ProductDAO productDAO = new ProductDAO();
    private StockMovementDAO stockMovementDAO = new StockMovementDAO();
    private DebtDAO debtDAO = new DebtDAO();
    private Gson gson = new Gson();

    // GET: liste des ventes du département du manager
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId"); // à stocker lors du login
        if (departementId == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Sale> sales = saleDAO.findByDepartement(departementId);
                resp.getWriter().write(gson.toJson(sales));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Sale sale = saleDAO.findById(id);
                if (sale == null || sale.getDepartementId() != departementId) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    resp.getWriter().write(gson.toJson(sale));
                }
            }
        } catch (SQLException | NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST: créer une vente (avec ses lignes)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId");
        Integer userId = (Integer) session.getAttribute("userId");
        if (departementId == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        BufferedReader reader = req.getReader();
        Map<String, Object> data = gson.fromJson(reader, Map.class);
        Sale sale = new Sale();
        sale.setDepartementId(departementId);
        sale.setSoldAt(Date.valueOf((String) data.get("sold_at")));
        sale.setCreatedBy(userId);
        sale.setNotes((String) data.get("notes"));
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) data.get("items");

        try {
            // Démarrer transaction (à implémenter avec setAutoCommit false)
            saleDAO.create(sale);
            double total = 0;
            for (Map<String, Object> itemData : itemsData) {
                int productId = ((Double) itemData.get("product_id")).intValue();
                int quantity = ((Double) itemData.get("quantity")).intValue();
                boolean isPaid = (Boolean) itemData.getOrDefault("is_paid", true);
                String clientName = (String) itemData.get("client_name");

                Product product = productDAO.findById(productId);
                if (product.getCurrentStock() < quantity) {
                    throw new SQLException("Stock insuffisant pour " + product.getName());
                }
                double lineTotal = product.getUnitPrice() * quantity;
                SaleItem item = new SaleItem();
                item.setSaleId(sale.getId());
                item.setProductId(productId);
                item.setQuantity(quantity);
                item.setUnitPrice(product.getUnitPrice());
                item.setPaid(isPaid);
                item.setClientName(clientName);
                saleItemDAO.create(item);

                // Mise à jour stock
                product.setCurrentStock(product.getCurrentStock() - quantity);
                productDAO.update(product);

                // Mouvement de stock
                StockMovement movement = new StockMovement();
                movement.setProductId(productId);
                movement.setQuantity(quantity);
                movement.setType("out");
                movement.setReason("sale");
                movement.setReferenceId(sale.getId());
                movement.setCreatedBy(userId);
                stockMovementDAO.create(movement);

                // Dette si non payé
                if (!isPaid) {
                    Debt debt = new Debt();
                    debt.setDebtorType("client");
                    debt.setDebtorName(clientName);
                    debt.setAmount(lineTotal);
                    debt.setSaleItemId(item.getId());
                    debt.setDueDate(Date.valueOf(LocalDate.now().plusDays(30)));
                    debt.setStatus("pending");
                    debtDAO.create(debt);
                }
                total += lineTotal;
            }
            sale.setTotalAmount(total);
            saleDAO.update(sale);
            // Commit transaction
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(sale));
        } catch (SQLException e) {
            // Rollback transaction
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // PUT: modifier une vente (ex: date, notes)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int id = Integer.parseInt(pathInfo.substring(1));
        BufferedReader reader = req.getReader();
        Sale saleData = gson.fromJson(reader, Sale.class);
        try {
            Sale sale = saleDAO.findById(id);
            if (sale == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (saleData.getSoldAt() != null) sale.setSoldAt(saleData.getSoldAt());
            if (saleData.getNotes() != null) sale.setNotes(saleData.getNotes());
            saleDAO.update(sale);
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(sale));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // DELETE: annuler une vente (restaure stock)
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int id = Integer.parseInt(pathInfo.substring(1));
        try {
            Sale sale = saleDAO.findById(id);
            if (sale == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            // Restaurer les stocks
            List<SaleItem> items = saleItemDAO.findBySaleId(id);
            for (SaleItem item : items) {
                Product product = productDAO.findById(item.getProductId());
                product.setCurrentStock(product.getCurrentStock() + item.getQuantity());
                productDAO.update(product);
                // Mouvement de stock annulation
                StockMovement movement = new StockMovement();
                movement.setProductId(item.getProductId());
                movement.setQuantity(item.getQuantity());
                movement.setType("in");
                movement.setReason("cancellation");
                movement.setReferenceId(id);
                movement.setCreatedBy(sale.getCreatedBy());
                stockMovementDAO.create(movement);
            }
            // Supprimer les lignes puis la vente
            saleItemDAO.deleteBySaleId(id);
            saleDAO.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}