package apps.app.controllers.api.cashier;

// Imports nécessaires
import apps.app.dao.SaleDAO;                 // Accès aux ventes
import apps.app.dao.SaleItemDAO;            // Accès aux lignes de vente
import apps.app.models.Sale;                // Modèle Vente
import apps.app.models.SaleItem;            // Modèle Ligne de vente
import apps.app.services.PrinterService;    // Service d'impression ticket
import com.google.gson.Gson;                // Pour les réponses JSON
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet pour l'impression d'un ticket de caisse (par le caissier ou le manager).
 * Endpoint : POST /api/cashier/sales/{saleId}/print
 */
@WebServlet("/api/cashier/sales/*")
public class CashierPrintTicketServlet extends HttpServlet {

    // DAO pour récupérer la vente et ses lignes
    private SaleDAO saleDAO = new SaleDAO();
    private SaleItemDAO saleItemDAO = new SaleItemDAO();

    // Service d'impression
    private PrinterService printerService = new PrinterService();

    // Gson pour formater les réponses JSON
    private Gson gson = new Gson();

    /**
     * Gère les requêtes POST pour imprimer un ticket.
     * URL attendue : /api/cashier/sales/{saleId}/print
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Vérifier la session (utilisateur connecté)
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendError(resp, 401, "Non authentifié");
            return;
        }

        // 2. Vérifier le rôle (caissier ou manager, pas admin, pas autre)
        String role = (String) session.getAttribute("role");
        if (role == null || (!"cashier".equals(role) && !"manager".equals(role))) {
            sendError(resp, 403, "Accès interdit : réservé aux caissiers et managers");
            return;
        }

        // 3. Extraire l'ID de la vente depuis l'URL
        String pathInfo = req.getPathInfo();                // ex: "/123/print"
        if (pathInfo == null || !pathInfo.matches("/\\d+/print")) {
            sendError(resp, 400, "URL invalide. Utilisez POST /api/cashier/sales/{saleId}/print");
            return;
        }

        int saleId;
        try {
            // Découper le pathInfo pour récupérer l'ID
            String[] parts = pathInfo.split("/");
            saleId = Integer.parseInt(parts[1]);  // parts[0] = "", parts[1] = "123", parts[2] = "print"
        } catch (NumberFormatException e) {
            sendError(resp, 400, "ID de vente invalide");
            return;
        }

        // 4. Récupérer la vente en base
        Sale sale;
        try {
            sale = saleDAO.findById(saleId);
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur base de données : " + e.getMessage());
            return;
        }

        if (sale == null) {
            sendError(resp, 404, "Vente non trouvée");
            return;
        }

        // 5. Vérifier que la vente appartient au département du caissier (optionnel, mais recommandé)
        Integer departementIdSession = (Integer) session.getAttribute("departementId");
        if (departementIdSession != null && sale.getDepartementId() != departementIdSession) {
            sendError(resp, 403, "Cette vente n'appartient pas à votre département");
            return;
        }

        // 6. Récupérer les lignes de la vente
        List<SaleItem> items;
        try {
            items = saleItemDAO.findBySaleId(saleId);
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur lors de la récupération des lignes : " + e.getMessage());
            return;
        }

        if (items == null || items.isEmpty()) {
            sendError(resp, 404, "Aucune ligne trouvée pour cette vente");
            return;
        }

        // 7. Appeler le service d'impression
        try {
            printerService.printTicket(sale, items);
            // 8. Réponse de succès
            resp.setStatus(HttpServletResponse.SC_OK);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ticket imprimé avec succès");
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(response));
        } catch (IOException e) {
            // Erreur imprimante (hors ligne, mauvaises coordonnées...)
            sendError(resp, 500, "Erreur d'impression : " + e.getMessage());
        } catch (SQLException e) {
            sendError(resp, 500, "Erreur base de données lors de l'impression : " + e.getMessage());
        }
    }

    /**
     * Méthode utilitaire pour envoyer une réponse d'erreur en JSON.
     */
    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        resp.getWriter().write(gson.toJson(error));
    }
}