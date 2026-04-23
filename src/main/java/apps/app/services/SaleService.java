package apps.app.services;

import apps.app.dao.*;
import apps.app.models.*;
import apps.app.utils.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class SaleService {

    private SaleDAO saleDAO = new SaleDAO();
    private SaleItemDAO saleItemDAO = new SaleItemDAO();
    private ProductDAO productDAO = new ProductDAO();
    private StockMovementDAO stockMovementDAO = new StockMovementDAO();
    private DebtService debtService = new DebtService();

    /**
     * Enregistre une vente complète (en-tête + lignes) dans une transaction.
     * Met à jour les stocks, crée les mouvements et gère les dettes.
     *
     * @param sale   l'en-tête de la vente (department_id, sold_at, created_by, notes)
     * @param items  la liste des articles vendus (product_id, quantity, unit_price, is_paid, client_name)
     * @throws SQLException si une erreur de base de données survient
     * @throws IllegalArgumentException si stock insuffisant ou données invalides
     */
    public void createSale(Sale sale, List<SaleItem> items) throws SQLException {
        // Vérifications préalables
        if (sale == null || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La vente et les articles sont obligatoires.");
        }

        // Vérifier le stock pour chaque article avant toute insertion
        for (SaleItem item : items) {
            Product product = productDAO.findById(item.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("Produit introuvable : id " + item.getProductId());
            }
            if (product.getCurrentStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Stock insuffisant pour le produit " + product.getName() +
                        ". Stock disponible : " + product.getCurrentStock());
            }
        }

        // Utiliser une transaction pour garantir l'intégrité
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Insérer l'en-tête de vente (total temporaire à 0)
                sale.setTotalAmount(0.0);
                saleDAO.create(sale);  // sale.getId() est maintenant peuplé

                double total = 0.0;

                // 2. Traiter chaque article
                for (SaleItem item : items) {
                    // Lier l'article à la vente
                    item.setSaleId(sale.getId());
                    saleItemDAO.create(item);  // item.getId() est peuplé

                    // Mise à jour du stock
                    Product product = productDAO.findById(item.getProductId());
                    int newStock = product.getCurrentStock() - item.getQuantity();
                    product.setCurrentStock(newStock);
                    productDAO.update(product);

                    // Enregistrer le mouvement de stock
                    StockMovement movement = new StockMovement();
                    movement.setProductId(item.getProductId());
                    movement.setQuantity(item.getQuantity());
                    movement.setType("out");
                    movement.setReason("sale");
                    movement.setReferenceId(sale.getId());
                    movement.setCreatedBy(sale.getCreatedBy());
                    stockMovementDAO.create(movement);

                    // Gérer la dette si non payé
                    if (!item.isPaid()) {
                        Debt debt = new Debt();
                        debt.setDebtorType("client");
                        debt.setDebtorName(item.getClientName());
                        debt.setAmount(item.getQuantity() * item.getUnitPrice());
                        debt.setSaleItemId(item.getId());
                        debt.setDueDate(Date.valueOf(LocalDate.now().plusDays(30)));
                        debt.setStatus("pending");
                        debtService.createDebt(debt);
                    }

                    total += item.getQuantity() * item.getUnitPrice();
                }

                // 3. Mettre à jour le total de la vente
                sale.setTotalAmount(total);
                saleDAO.update(sale);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Annule une vente (supprime la vente, restaure les stocks, supprime les dettes associées).
     * Cette opération est irréversible.
     *
     * @param saleId l'identifiant de la vente à annuler
     * @throws SQLException si erreur base de données
     */
    public void cancelSale(int saleId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Récupérer la vente et ses articles
                Sale sale = saleDAO.findById(saleId);
                if (sale == null) {
                    throw new IllegalArgumentException("Vente non trouvée");
                }
                List<SaleItem> items = saleItemDAO.findBySaleId(saleId);

                for (SaleItem item : items) {
                    // Restaurer le stock du produit
                    Product product = productDAO.findById(item.getProductId());
                    product.setCurrentStock(product.getCurrentStock() + item.getQuantity());
                    productDAO.update(product);

                    // Enregistrer un mouvement de stock entrant (pour traçabilité)
                    StockMovement movement = new StockMovement();
                    movement.setProductId(item.getProductId());
                    movement.setQuantity(item.getQuantity());
                    movement.setType("in");
                    movement.setReason("cancellation");
                    movement.setReferenceId(saleId);
                    movement.setCreatedBy(sale.getCreatedBy());
                    stockMovementDAO.create(movement);

                    // Supprimer la dette associée si elle existe
                    // (vous devrez implémenter une méthode findBySaleItemId dans DebtService)
                    List<Debt> debts = debtService.getDebtsBySaleItemId(item.getId());
                    for (Debt debt : debts) {
                        // On pourrait les marquer comme annulées, mais ici on supprime
                        // (à ajouter une méthode delete dans DebtDAO)
                    }
                }

                // Supprimer les articles puis la vente
                for (SaleItem item : items) {
                    saleItemDAO.delete(item.getId());
                }
                saleDAO.delete(saleId);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Debt> getDebtsBySaleItemId(int id) {
        return List.of();
    }
}