package apps.app.services;

import apps.app.dao.DebtDAO;
import apps.app.dao.SaleItemDAO;
import apps.app.models.Debt;
import apps.app.models.SaleItem;
import apps.app.utils.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DebtService {

    private DebtDAO debtDAO = new DebtDAO();
    private SaleItemDAO saleItemDAO = new SaleItemDAO();

    /**
     * Enregistre un paiement partiel ou total sur une dette.
     *      * @param debtId l'identifiant de la dette
     *      * @param paidAmount le montant payé (doit être > 0 et ≤ montant restant)
     *      * @throws SQLException si erreur base de données
     *      * @throws IllegalArgumentException si paramètres invalides
     *      * @throws IllegalStateException si la dette est déjà soldée
     */

    public void registerPayment(int debtId,double paidAmount) throws SQLException {

        //verification preliminaire
        if (paidAmount <= 0){
            throw new IllegalArgumentException("Le montant doit etre supperier a 0");
        }

        //requiperer la dette
        Debt debt = debtDAO.findById(debtId);
        if (debt == null){
            throw new IllegalArgumentException("Dette no trouve");
        }
        if ("paid".equals(debt.getStatus())){
            throw  new  IllegalArgumentException("Cette dette est deja solde");
        }
        if (paidAmount > debt.getAmount()){
            throw new IllegalArgumentException("Le montant paye depasse le montant du");
        }

        //garentire la coherence
        try {
            Connection conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try {
                double remaining = debt.getAmount() - paidAmount;
                double newPaidAmount = (debt.getPaidAmount() == null ? 0 : debt.getPaidAmount()) + paidAmount;

                if (remaining == 0) {
                    //paiment total
                    debt.setStatus("Paid");
                    debt.setPaidAt(Date.valueOf(LocalDate.now()));
                    debt.setPaidAmount(newPaidAmount);
                    debtDAO.update(debt);

                    //Marque le saleIte, correspondant comme paye
                    SaleItem item = saleItemDAO.findById(debt.getSaleItemId());

                    if (item != null) {
                        item.setPaid(true);
                        saleItemDAO.update(item);
                    }
                } else {
                    //Paiement partiel , on reduit le montant
                    debt.setAmount(remaining);
                    debt.setPaidAmount(newPaidAmount);
                    debtDAO.update(debt);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } finally {

        }
    }

    //Retourne toutes les dettes en attentes(no soldes)
    public List<Debt> getPendingDebts() throws SQLException {
        return debtDAO.findPending();
    }

    //retourne les dettes d'un client
//    public List<Debt> getDebtsByClientName(String clientName) throws SQLException {
//        return debtDAO.findByDebtorName(clientName);
//    }


    /**
     * Retourne les dettes en souffrance (échéance dépassée et non payées).
     */
//    public List<Debt> getOverdueDebts() throws SQLException {
//        return debtDAO.findOverdue();
//    }

    /**
     * Calcule le total des dettes en attente.
     */
//    public double getTotalPendingAmount() throws SQLException {
//        return debtDAO.getTotalPendingDebts();
//    }


    /**
     * Crée une nouvelle dette (par exemple après une vente à crédit).
     * Cette méthode est généralement appelée par SaleService, mais on la rend disponible.
     */
    public void createDebt(Debt debt) throws SQLException {
        if (debt.getAmount() <= 0) {
            throw new IllegalArgumentException("Le montant de la dette doit être positif.");
        }
        if (debt.getDueDate() == null) {
            debt.setDueDate(Date.valueOf(LocalDate.now().plusDays(30))); // échéance par défaut 30 jours
        }
        debt.setStatus("pending");
        debtDAO.create(debt);
    }


    public List<Debt> getDebtsBySaleItemId(int id) {
        return List.of();
    }
}
