//package apps.app.services;
//
//import apps.app.dao.ProductDAO;
//import apps.app.dao.StockMovementDAO;
//import apps.app.models.Product;
//import apps.app.models.StockMovement;
//import org.*;
//import org.apache.logging.log4j.LogManager;
//
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.logging.Logger;
//import javax.sql.DataSource;
//import javax.sqlaSource;
//
//public class StockService {
//    private static LogManager LoggerFactory;
//    private static final Logger log = LoggerFactory.getLogger(MonServlet.class);
//    private ProductDAO productDAO;
//    private StockMovementDAO stockMovementDAO;
//    private DataSource dataSource;
//
//    public StockService(DataSource dataSource) {
//        this.dataSource = dataSource;
//        this.productDAO = new ProductDAO();
//        this.stockMovementDAO = new StockMovementDAO();
//    }
//
//    /**
//     * Ajuste le stock d'un produit et enregistre le mouvement dans une transaction.
//     * @return le produit mis à jour
//     * @throws SQLException si erreur (rollback automatique)
//     */
//    public Product adjustStock(int productId, int adjustment, String reason, int userId) throws SQLException {
//        Connection conn = null;
//        try {
//            conn = dataSource.getConnection();
//            conn.setAutoCommit(false);  // début transaction
//
//            // 1. Récupérer le produit
//            Product product = productDAO.findById(productId);
//            if (product == null) {
//                throw new SQLException("Produit non trouvé");
//            }
//
//            // 2. Vérifier stock suffisant
//            int newStock = product.getCurrentStock() + adjustment;
//            if (newStock < 0) {
//                throw new SQLException("Stock insuffisant. Actuel: " + product.getCurrentStock() + ", demande: " + adjustment);
//            }
//
//            // 3. Mettre à jour le stock
//            product.setCurrentStock(newStock);
//            productDAO.update(product);  // devra utiliser la même connexion
//
//            // 4. Enregistrer le mouvement
//            StockMovement movement = new StockMovement();
//            movement.setProductId(productId);
//            movement.setQuantity(Math.abs(adjustment));
//            movement.setType(adjustment >= 0 ? "in" : "out");
//            movement.setReason(reason);
//            movement.setCreatedBy(userId);
//            stockMovementDAO.create(movement); // doit aussi utiliser la même connexion
//
//            conn.commit();
//            logger.info("Ajustement stock produit {} : {} unités, motif '{}' par user {}", productId, adjustment, reason, userId);
//            return product;
//        } catch (SQLException e) {
//            if (conn != null) {
//                try { conn.rollback(); } catch (SQLException ex) { logger.error("Rollback échoué", ex); }
//            }
//            logger.error("Erreur lors de l'ajustement du stock : {}", e.getMessage());
//            throw e;
//        } finally {
//            if (conn != null) {
//                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { logger.error("Fermeture connexion", e); }
//            }
//        }
//    }
//
//    // On peut ajouter updateProduct, softDeleteProduct, etc. avec transactions similaires
//}