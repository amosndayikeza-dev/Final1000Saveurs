package apps.app.controllers.api.manager;

import apps.app.dao.ProductDAO;
import apps.app.dao.StockMovementDAO;
import apps.app.models.Product;
import apps.app.models.StockMovement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/manager/stocks/*")
public class ManagerStocksApiServlet extends HttpServlet {

    private ProductDAO productDAO;
    private StockMovementDAO stockMovementDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        stockMovementDAO = new StockMovementDAO();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    // ============================ GET ============================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("departementId") == null) {
            sendError(resp, 401, "Session invalide ou manager non authentifié");
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId");
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");

        try {
            // Liste des produits
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Product> products = productDAO.findByDepartement(departementId);
                resp.getWriter().write(gson.toJson(products));
                return;
            }
            // Détail d'un produit
            if (pathInfo.matches("/\\d+")) {
                int productId = Integer.parseInt(pathInfo.substring(1));
                Product product = productDAO.findById(productId);
                if (product == null || product.getDepartementId() != departementId) {
                    sendError(resp, 404, "Produit non trouvé ou non autorisé");
                    return;
                }
                resp.getWriter().write(gson.toJson(product));
                return;
            }
            // Historique des mouvements
            if (pathInfo.matches("/\\d+/movements")) {
                String[] parts = pathInfo.split("/");
                int productId = Integer.parseInt(parts[1]);
                Product product = productDAO.findById(productId);
                if (product == null || product.getDepartementId() != departementId) {
                    sendError(resp, 404, "Produit non trouvé");
                    return;
                }
                List<StockMovement> movements = stockMovementDAO.findByProductId(productId);
                resp.getWriter().write(gson.toJson(movements));
                return;
            }
            sendError(resp, 404, "Endpoint inconnu");

        } catch (NumberFormatException e) {
            sendError(resp, 400, "ID de produit invalide");
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur base de données : " + e.getMessage());
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
        Integer departementId = (Integer) session.getAttribute("departementId");
        Integer userId = (Integer) session.getAttribute("userId");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+/adjust")) {
            sendError(resp, 400, "URL invalide. Utilisez POST /{id}/adjust");
            return;
        }
        int productId;
        try {
            productId = Integer.parseInt(pathInfo.split("/")[1]);
        } catch (NumberFormatException e) {
            sendError(resp, 400, "ID produit invalide");
            return;
        }

        Map<String, Object> data;
        try (BufferedReader reader = req.getReader()) {
            data = gson.fromJson(reader, Map.class);
        } catch (JsonSyntaxException e) {
            sendError(resp, 400, "JSON mal formé");
            return;
        }

        if (!data.containsKey("adjustment") || !data.containsKey("reason")) {
            sendError(resp, 400, "Les champs 'adjustment' et 'reason' sont requis");
            return;
        }

        int adjustment;
        try {
            Object adjObj = data.get("adjustment");
            if (adjObj instanceof Number) {
                adjustment = ((Number) adjObj).intValue();
            } else {
                adjustment = Integer.parseInt(adjObj.toString());
            }
        } catch (NumberFormatException | ClassCastException e) {
            sendError(resp, 400, "L'ajustement doit être un nombre entier");
            return;
        }

        String reason = data.get("reason").toString().trim();
        if (reason.isEmpty() || reason.length() > 255) {
            sendError(resp, 400, "Le motif doit comporter entre 1 et 255 caractères");
            return;
        }

        Product product;
        try {
            product = productDAO.findById(productId);
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur lors de la recherche du produit");
            return;
        }
        if (product == null || product.getDepartementId() != departementId) {
            sendError(resp, 404, "Produit non trouvé ou non autorisé");
            return;
        }

        int newStock = product.getCurrentStock() + adjustment;
        if (newStock < 0) {
            sendError(resp, 400, "Stock insuffisant. Stock actuel : " + product.getCurrentStock() +
                    ", ajustement demandé : " + adjustment);
            return;
        }

        // Sauvegarder l'ancien stock en cas d'échec de l'historique
        int oldStock = product.getCurrentStock();
        product.setCurrentStock(newStock);

        StockMovement movement = new StockMovement();
        movement.setProductId(productId);
        movement.setQuantity(Math.abs(adjustment));
        movement.setType(adjustment >= 0 ? "in" : "out");
        movement.setReason(reason);
        movement.setCreatedBy(userId);

        try {
            // Mettre à jour le produit
            productDAO.update(product);
            // Créer le mouvement
            stockMovementDAO.create(movement);
            // Succès
            resp.setStatus(200);
            resp.getWriter().write(gson.toJson(product));
        } catch (SQLException e) {
            // Si échec, on tente de restaurer l'ancien stock (rollback simple)
            product.setCurrentStock(oldStock);
            try {
                productDAO.update(product);
            } catch (SQLException ex) {
                // Impossible de restaurer, on logge (mais on n'a pas de logger, on ignore)
            }
            sendError(resp, 500, "Erreur lors de l'ajustement du stock : " + e.getMessage());
        }
    }

    // ============================ PUT ============================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("departementId") == null) {
            sendError(resp, 401, "Non authentifié");
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            sendError(resp, 400, "URL invalide. Utilisez PUT /{id}");
            return;
        }
        int productId = Integer.parseInt(pathInfo.substring(1));

        Product updatedProduct;
        try (BufferedReader reader = req.getReader()) {
            updatedProduct = gson.fromJson(reader, Product.class);
        } catch (JsonSyntaxException e) {
            sendError(resp, 400, "JSON invalide");
            return;
        }

        Product existingProduct;
        try {
            existingProduct = productDAO.findById(productId);
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur base de données");
            return;
        }
        if (existingProduct == null || existingProduct.getDepartementId() != departementId) {
            sendError(resp, 403, "Produit non trouvé ou droits insuffisants");
            return;
        }

        // Mise à jour des champs autorisés
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setUnitPrice(updatedProduct.getUnitPrice());
        existingProduct.setLowStockThreshold(updatedProduct.getLowStockThreshold());
        // currentStock et departementId ne sont pas modifiés

        try {
            productDAO.update(existingProduct);
            resp.setStatus(200);
            resp.getWriter().write(gson.toJson(existingProduct));
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur lors de la mise à jour");
        }
    }

    // ============================ DELETE ============================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("departementId") == null) {
            sendError(resp, 401, "Authentification requise");
            return;
        }
        Integer departementId = (Integer) session.getAttribute("departementId");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            sendError(resp, 400, "URL invalide. Utilisez DELETE /{id}");
            return;
        }
        int productId = Integer.parseInt(pathInfo.substring(1));

        Product product;
        try {
            product = productDAO.findById(productId);
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur base de données");
            return;
        }
        if (product == null || product.getDepartementId() != departementId) {
            sendError(resp, 403, "Produit non trouvé ou accès interdit");
            return;
        }

        try {
            boolean deleted = productDAO.delete(productId);
            if (deleted) {
                resp.setStatus(204); // No Content
            } else {
                sendError(resp, 404, "Produit non trouvé");
            }
        } catch (SQLException e) {
            // Si contrainte de clé étrangère (mouvements existants), on préfère 409
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
                sendError(resp, 409, "Impossible de supprimer : des mouvements de stock sont associés");
            } else {
                sendError(resp, 500, "Erreur lors de la suppression : " + e.getMessage());
            }
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