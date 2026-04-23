package apps.app.services;

import apps.app.dao.ProductDAO;
import apps.app.dao.StockMovementDAO;
import apps.app.models.Product;
import apps.app.models.StockMovement;
import apps.app.utils.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StockService {

    private ProductDAO productDAO = new ProductDAO();
    private StockMovementDAO stockMovementDAO = new StockMovementDAO();

    /**
     * Ajuste le stock d'un produit (entrée ou sortie) et enregistre le mouvement.
     *
     * @param productId identifiant du produit
     * @param quantity  quantité à ajouter (positive pour entrée, négative pour sortie)
     * @param reason    motif (ex: "purchase", "adjustment", "loss", "return")
     * @param userId    identifiant de l'utilisateur qui effectue l'opération
     * @throws SQLException              si erreur base de données
     * @throws IllegalArgumentException si le stock devient négatif
     */
    public void adjustStock(int productId, int quantity, String reason, int userId) throws SQLException {
        if (quantity == 0) {
            throw new IllegalArgumentException("La quantité doit être différente de zéro.");
        }

        // Utiliser une transaction pour garantir la cohérence
        try {
            Connection conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                Product product = productDAO.findById(productId);
                if (product == null) {
                    throw new IllegalArgumentException("Produit non trouvé avec l'ID " + productId);
                }

                int newStock = product.getCurrentStock() + quantity;
                if (newStock < 0) {
                    throw new IllegalArgumentException("Stock négatif impossible. Stock actuel : "
                            + product.getCurrentStock() + ", ajustement : " + quantity);
                }

                // Mettre à jour le stock du produit
                product.setCurrentStock(newStock);
                productDAO.update(product);

                // Enregistrer le mouvement de stock
                StockMovement movement = new StockMovement();
                movement.setProductId(productId);
                movement.setQuantity(Math.abs(quantity));
                movement.setType(quantity > 0 ? "in" : "out");
                movement.setReason(reason);
                movement.setCreatedBy(userId);
                stockMovementDAO.create(movement);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retourne la liste des produits dont le stock est inférieur ou égal au seuil d'alerte.
     */
    public List<Product> getLowStockProducts() throws SQLException {
        return productDAO.findLowStock();
    }

    /**
     * Retourne l'historique des mouvements pour un produit donné.
     */
    public List<StockMovement> getStockMovementsForProduct(int productId) throws SQLException {
        return stockMovementDAO.findByProduct(productId);
    }

    /**
     * Calcule la valeur totale du stock pour un département (ou pour tous).
     * @param departmentId optionnel, si null calcule pour tous les départements
     * @return valeur totale (somme de current_stock * unit_price)
     */
    public double getTotalStockValue(Integer departmentId) throws SQLException {
        List<Product> products;
        if (departmentId != null) {
            products = productDAO.findByDepartement(departmentId);
        } else {
            products = productDAO.findAll();
        }
        double total = 0.0;
        for (Product p : products) {
            total += p.getCurrentStock() * p.getUnitPrice();
        }
        return total;
    }

    /**
     * Récupère le stock actuel d'un produit (sans charger tout l'objet, juste la quantité).
     * Cette méthode est utile pour des vérifications rapides.
     */
    public int getCurrentStock(int productId) throws SQLException {
        Product product = productDAO.findById(productId);
        return product == null ? 0 : product.getCurrentStock();
    }
}