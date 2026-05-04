package apps.app.services;

// Importation des classes nécessaires pour l'impression
import com.github.anastaciocintra.escpos.EscPos; // Classe principale pour envoyer des commandes ESC/POS
import com.github.anastaciocintra.escpos.EscPosConst; // Constantes (alignement, etc.)
import com.github.anastaciocintra.escpos.Style; // Permet de définir le style du texte (taille, gras, etc.)
import com.github.anastaciocintra.escpos.image.Bitonal; // Pour éventuellement imprimer un logo (non utilisé ici)
import apps.app.models.Sale;
import apps.app.models.SaleItem;
import apps.app.models.Product;
import apps.app.dao.ProductDAO;
import apps.app.utils.PrinterConfig; // Notre utilitaire de configuration de l'imprimante
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException; // Gestion des erreurs d'E/S avec l'imprimante
import java.sql.SQLException; // Pour gérer les erreurs d'accès à la base
import java.text.SimpleDateFormat; // Formatage de la date
import java.util.Date; // Date de la vente
import java.util.List; // Liste des lignes de vente

/**
 * Service dédié à l'impression des tickets de caisse.
 * Utilise la bibliothèque escpos-coffee et la configuration PrinterConfig.
 */
public class PrinterService {

    // Logger (optionnel, peut être commenté si non utilisé)
    private static final Logger logger = LoggerFactory.getLogger(PrinterService.class);

    // DAO pour accéder au nom des produits (car SaleItem ne contient que productId)
    private ProductDAO productDAO = new ProductDAO();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    /**
     * Imprime un ticket pour une vente donnée.
     * Génère le ticket, envoie à l'imprimante via PrinterConfig.
     *
     * @param sale   La vente (contient les infos globales : date, total, etc.)
     * @param items  Les lignes de vente (produits, quantités, prix)
     * @throws IOException    Si la communication avec l'imprimante échoue
     * @throws SQLException   Si la lecture du nom du produit échoue
     */
    public void printTicket(Sale sale, List<SaleItem> items) throws IOException, SQLException {

        try (EscPos escpos = PrinterConfig.getEscPos()) {


            Style style = new Style();

            // --- En-tête du ticket ---
            // 2.1 Titre principal : "1000Saveurs" en gras et agrandi
            style.setFontSize(Style.FontSize._2, Style.FontSize._2); // Double hauteur/largeur
            style.setBold(true);
            escpos.writeLF(style, "1000Saveurs");
            escpos.feed(1); // Une ligne vierge après le titre

            // 2.2 Nom du département (extraire du nom ? sale.getDepartementId() ? Optionnel)
            //    On peut afficher simplement "Ticket de caisse"
            style.setFontSize(Style.FontSize._1, Style.FontSize._1); // Taille normale
            style.setBold(false);
            escpos.writeLF(style, "Ticket de caisse");
            escpos.feed(1);

            // 2.3 Date et numéro de ticket (on utilise l'ID de la vente comme numéro)
            //style.setItalic(true);// Italique pour les infos secondaires
            String dateStr = dateFormat.format(sale.getSoldAt());
            escpos.writeLF(style, "Date : " + dateStr);
            escpos.writeLF(style, "Ticket n° : " + sale.getId());
            escpos.writeLF(style, "Caissier : " + sale.getCreatedBy()); // ID du caissier (on pourrait afficher son nom)
            escpos.feed(1);

            // 2.4 Ligne de séparation
            escpos.writeLF("--------------------------------");

            // --- Détail des produits (tableau) ---
            // 3. Entêtes du tableau
            style.setBold(true);
            // On peut écrire en colonnes fixes (exemple : 10 chars pour qté, 20 pour nom, 10 pour prix)
            escpos.writeLF(style, String.format("%-3s %-20s %6s", "Qté", "Produit", "Prix"));
            style.setBold(false);
            escpos.writeLF("--------------------------------");

            // 4. Parcourir chaque ligne de vente pour imprimer la ligne correspondante
            double total = 0.0;
            for (SaleItem item : items) {

                Product product = productDAO.findById(item.getProductId());
                String productName = product.getName();
                // Tronquer si trop long (20 caractères)
                if (productName.length() > 20) productName = productName.substring(0, 20);

                int quantity = item.getQuantity();
                double unitPrice = item.getUnitPrice();
                double lineTotal = quantity * unitPrice;
                total += lineTotal;

                // Format : "2   Cola                2.50"
                // Utilisation de String.format pour aligner les colonnes
                String line = String.format("%-3d %-20s %6.2f", quantity, productName, unitPrice);
                escpos.writeLF(line);
            }

            // 5. Ligne de séparation
            escpos.writeLF("--------------------------------");

            // 6. Total TTC
            style.setBold(true);
            escpos.writeLF(style, String.format("TOTAL : %.2f FBU", total));
            style.setBold(false);
            escpos.feed(1);

            // 7. Mentions additionnelles (par exemple : TVA non applicable, règlement, etc.)
            escpos.writeLF("Merci de votre visite");
            escpos.writeLF("A bientôt chez 1000Saveurs !");
            escpos.feed(2); // Deux lignes blanches avant la coupe

            // 8. Couper le papier (full cut)
            escpos.cut(EscPos.CutMode.FULL);

            // 9. (Optionnel) Loguer le succès de l'impression
            if (logger.isInfoEnabled()) {
                logger.info("Ticket imprimé pour la vente ID {}", sale.getId());
            }
        } catch (IOException e) {
            // Relancer l'exception avec un message plus explicite
            logger.error("Erreur lors de l'impression du ticket (vente {}) : {}", sale.getId(), e.getMessage());
            throw new IOException("Impossible d'imprimer le ticket. Vérifiez l'imprimante.", e);
        } catch (SQLException e) {
            logger.error("Erreur base de données lors du chargement du nom du produit pour le ticket vente {}", sale.getId(), e);
            throw e; // Propager pour que l'appelant puisse gérer
        }
    }
}