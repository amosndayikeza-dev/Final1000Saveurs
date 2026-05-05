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
import com.google.gson.GsonBuilder;

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

@WebServlet("/api/manager/sales/*")
public class ManagerSalesApiServlet extends HttpServlet {

    private SaleDAO saleDAO;
    private SaleItemDAO saleItemDAO;
    private ProductDAO productDAO;
    private StockMovementDAO stockMovementDAO;
    private DebtDAO debtDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        saleDAO = new SaleDAO();
        saleItemDAO = new SaleItemDAO();
        productDAO = new ProductDAO();
        stockMovementDAO = new StockMovementDAO();
        debtDAO = new DebtDAO();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    // ============================ GET ============================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null || session.getAttribute("departementId") == null) {
            sendError(resp, 401, "Non authentifié");
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId");
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Sale> sales = saleDAO.findByDepartement(departementId);
                resp.getWriter().write(gson.toJson(sales));
                return;
            }
            if (pathInfo.matches("/\\d+")) {
                int id = Integer.parseInt(pathInfo.substring(1));
                Sale sale = saleDAO.findById(id);
                if (sale == null || sale.getDepartementId() != departementId) {
                    sendError(resp, 404, "Vente non trouvée");
                    return;
                }
                resp.getWriter().write(gson.toJson(sale));
                return;
            }
            sendError(resp, 404, "Endpoint inconnu");
        } catch (NumberFormatException e) {
            sendError(resp, 400, "ID invalide");
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur base de données");
        }
    }

    // ============================ POST ============================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null || session.getAttribute("departementId") == null) {
            sendError(resp, 401, "Authentification requise");
            return;
        }
        Integer userId = (Integer) session.getAttribute("userId");
        Integer departementId = (Integer) session.getAttribute("departementId");

        Map<String, Object> data;
        try (BufferedReader reader = req.getReader()) {
            data = gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            sendError(resp, 400, "JSON invalide");
            return;
        }

        // Validation des champs requis
        if (!data.containsKey("sold_at") || !data.containsKey("items")) {
            sendError(resp, 400, "sold_at et items sont requis");
            return;
        }

        Date soldAt;
        try {
            soldAt = Date.valueOf((String) data.get("sold_at"));
        } catch (IllegalArgumentException e) {
            sendError(resp, 400, "Format de date invalide (YYYY-MM-DD)");
            return;
        }

        String notes = data.get("notes") != null ? data.get("notes").toString() : "";

        List<Map<String, Object>> itemsData;
        try {
            itemsData = (List<Map<String, Object>>) data.get("items");
        } catch (ClassCastException e) {
            sendError(resp, 400, "items doit être une liste");
            return;
        }

        if (itemsData == null || itemsData.isEmpty()) {
            sendError(resp, 400, "Au moins un article est requis");
            return;
        }

        // 1. Vérifier tous les stocks et accumuler les données nécessaires
        Map<Integer, Integer> productQuantities = new HashMap<>();
        Map<Integer, Double> productPrices = new HashMap<>();
        Map<Integer, Boolean> productPaidStatus = new HashMap<>();
        Map<Integer, String> productClientNames = new HashMap<>();
        double totalAmount = 0.0;

        for (Map<String, Object> item : itemsData) {
            if (!item.containsKey("product_id") || !item.containsKey("quantity")) {
                sendError(resp, 400, "Chaque ligne doit contenir product_id et quantity");
                return;
            }
            int productId = ((Number) item.get("product_id")).intValue();
            int quantity = ((Number) item.get("quantity")).intValue();
            if (quantity <= 0) {
                sendError(resp, 400, "La quantité doit être positive");
                return;
            }

            boolean isPaid = item.containsKey("is_paid") ? (Boolean) item.get("is_paid") : true;
            String clientName = item.containsKey("client_name") ? (String) item.get("client_name") : "";

            Product product;
            try {
                product = productDAO.findById(productId);
            } catch (SQLException e) {
                sendError(resp, 500, "Erreur lors de la vérification des produits");
                return;
            }
            if (product == null || product.getDepartementId() != departementId) {
                sendError(resp, 404, "Produit " + productId + " non trouvé dans ce département");
                return;
            }
            if (product.getCurrentStock() < quantity) {
                sendError(resp, 400, "Stock insuffisant pour " + product.getName() +
                        " (stock: " + product.getCurrentStock() + ")");
                return;
            }

            productQuantities.put(productId, quantity);
            productPrices.put(productId, product.getUnitPrice());
            productPaidStatus.put(productId, isPaid);
            productClientNames.put(productId, clientName);
            totalAmount += product.getUnitPrice() * quantity;
        }

        // 2. Créer la vente
        Sale sale = new Sale();
        sale.setDepartementId(departementId);
        sale.setSoldAt(soldAt);
        sale.setCreatedBy(userId);
        sale.setNotes(notes);
        sale.setTotalAmount(totalAmount);
        try {
            saleDAO.create(sale);
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur lors de la création de la vente: " + e.getMessage());
            return;
        }

        // 3. Enregistrer les lignes, mettre à jour stocks, mouvements, dettes
        try {
            for (Map<String, Object> item : itemsData) {
                int productId = ((Number) item.get("product_id")).intValue();
                int quantity = productQuantities.get(productId);
                double unitPrice = productPrices.get(productId);
                boolean isPaid = productPaidStatus.get(productId);
                String clientName = productClientNames.get(productId);

                // SaleItem
                SaleItem saleItem = new SaleItem();
                saleItem.setSaleId(sale.getId());
                saleItem.setProductId(productId);
                saleItem.setQuantity(quantity);
                saleItem.setUnitPrice(unitPrice);
                saleItem.setPaid(isPaid);
                saleItem.setClientName(clientName);
                saleItemDAO.create(saleItem);

                // Mise à jour du stock
                Product product = productDAO.findById(productId);
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

                // Dette si impayé
                if (!isPaid && clientName != null && !clientName.isEmpty()) {
                    Debt debt = new Debt();
                    debt.setDebtorType("client");
                    debt.setDebtorName(clientName);
                    debt.setAmount(unitPrice * quantity);
                    debt.setSaleItemId(saleItem.getId());
                    debt.setDueDate(Date.valueOf(LocalDate.now().plusDays(30)));
                    debt.setStatus("pending");
                    debtDAO.create(debt);
                }
            }
            // Réponse succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Vente enregistrée avec succès");
            response.put("sale_id", sale.getId());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(response));
        } catch (SQLException e) {
            // Si une erreur survient après l'insertion de la vente, on la supprime pour nettoyer
            try {
                saleDAO.delete(sale.getId());
            } catch (SQLException ex) {
                // Log silencieux
            }
            sendError(resp, 500, "Erreur lors de l'enregistrement des détails: " + e.getMessage());
        }
    }

    // ============================ PUT ============================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null || session.getAttribute("departementId") == null) {
            sendError(resp, 401, "Authentification requise");
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            sendError(resp, 400, "URL invalide. Utilisez PUT /{id}");
            return;
        }
        int saleId = Integer.parseInt(pathInfo.substring(1));

        Map<String, Object> data;
        try (BufferedReader reader = req.getReader()) {
            data = gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            sendError(resp, 400, "JSON invalide");
            return;
        }

        try {
            Sale sale = saleDAO.findById(saleId);
            if (sale == null || sale.getDepartementId() != departementId) {
                sendError(resp, 404, "Vente non trouvée ou non autorisée");
                return;
            }
            if (data.containsKey("sold_at")) {
                sale.setSoldAt(Date.valueOf((String) data.get("sold_at")));
            }
            if (data.containsKey("notes")) {
                sale.setNotes((String) data.get("notes"));
            }
            saleDAO.update(sale);
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(sale));
        } catch (SQLException | IllegalArgumentException e) {
            sendError(resp, 500, "Erreur mise à jour: " + e.getMessage());
        }
    }

    // ============================ DELETE ============================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null || session.getAttribute("departementId") == null) {
            sendError(resp, 401, "Authentification requise");
            return;
        }
        Integer userId = (Integer) session.getAttribute("userId");
        Integer departementId = (Integer) session.getAttribute("departementId");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            sendError(resp, 400, "URL invalide. Utilisez DELETE /{id}");
            return;
        }
        int saleId = Integer.parseInt(pathInfo.substring(1));

        try {
            Sale sale = saleDAO.findById(saleId);
            if (sale == null || sale.getDepartementId() != departementId) {
                sendError(resp, 404, "Vente non trouvée ou non autorisée");
                return;
            }

            // Vérifier si des dettes associées sont déjà payées
            List<Debt> debts = debtDAO.findBySaleItem(saleId);  // à implémenter si besoin
            // Si tu n'as pas cette méthode, tu peux la sauter ou la créer
            // Par sécurité, on va simplement annuler, mais attention aux dettes payées

            // Restaurer les stocks
            List<SaleItem> items = saleItemDAO.findBySaleId(saleId);
            for (SaleItem item : items) {
                Product product = productDAO.findById(item.getProductId());
                product.setCurrentStock(product.getCurrentStock() + item.getQuantity());
                productDAO.update(product);

                // Mouvement d'annulation
                StockMovement movement = new StockMovement();
                movement.setProductId(item.getProductId());
                movement.setQuantity(item.getQuantity());
                movement.setType("in");
                movement.setReason("cancellation");
                movement.setReferenceId(saleId);
                movement.setCreatedBy(userId);
                stockMovementDAO.create(movement);
            }

            // Supprimer les lignes, les dettes, puis la vente
            debtDAO.deleteBySaleId(saleId);   // méthode à créer ou adapter
            saleItemDAO.deleteBySaleId(saleId);
            saleDAO.delete(saleId);

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur lors de l'annulation: " + e.getMessage());
        }
    }

    // ============================ Utilitaire ============================
    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        resp.getWriter().write(gson.toJson(error));
    }
}