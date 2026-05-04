package apps.app.controllers.api.manager;

// Imports nécessaires
import apps.app.dao.SaleDAO;
import apps.app.dao.SaleItemDAO;
import apps.app.models.Sale;
import apps.app.models.SaleItem;
import apps.app.services.PrinterService;
import com.google.gson.Gson;
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
 * Servlet pour la réimpression d'un ticket de caisse (par le manager).
 * Endpoint : GET /api/manager/sales/{saleId}/ticket/reprint
 */
@WebServlet("/api/manager/sales/*")
public class ManagerReprintTicketServlet extends HttpServlet {

    // DAO pour accéder aux données des ventes et lignes
    private SaleDAO saleDAO = new SaleDAO();
    private SaleItemDAO saleItemDAO = new SaleItemDAO();

    // Service d'impression (réutilise la même logique que pour le caissier)
    private PrinterService printerService = new PrinterService();

    // Gson pour formater les réponses JSON
    private Gson gson = new Gson();

    /**
     * Gère les requêtes GET pour réimprimer un ticket existant.
     * URL attendue : /api/manager/sales/{saleId}/ticket/reprint
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Vérifier la session (utilisateur connecté)
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendError(resp, 401, "Non authentifié");
            return;
        }

        // 2. Vérifier le rôle (manager ou admin uniquement)
        String role = (String) session.getAttribute("role");
        if (role == null || (!"manager".equals(role) && !"admin".equals(role))) {
            sendError(resp, 403, "Accès interdit : réservé aux managers et admins");
            return;
        }

        // 3. Extraire l'ID de la vente depuis l'URL
        //    Le chemin attendu est : /api/manager/sales/{saleId}/ticket/reprint
        String pathInfo = req.getPathInfo();   // ex: "/123/ticket/reprint"
        if (pathInfo == null || !pathInfo.matches("/\\d+/ticket/reprint")) {
            sendError(resp, 400, "URL invalide. Utilisez GET /api/manager/sales/{saleId}/ticket/reprint");
            return;
        }

        int saleId;
        try {
            // Découper le pathInfo : parties = ["", "123", "ticket", "reprint"]
            String[] parts = pathInfo.split("/");
            saleId = Integer.parseInt(parts[1]);  // l'ID est à l'index 1
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

        // 5. Vérifier que le manager a le droit d'accéder à cette vente (même département)
        //    Le département du manager est stocké en session.
        Integer managerDepartementId = (Integer) session.getAttribute("departementId");
        if (managerDepartementId != null && sale.getDepartementId() != managerDepartementId) {
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

        // 7. Appeler le service d'impression pour réimprimer le ticket
        try {
            printerService.printTicket(sale, items);
            // 8. Réponse de succès
            resp.setStatus(HttpServletResponse.SC_OK);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ticket réimprimé avec succès");
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(response));
        } catch (IOException e) {
            // Erreur d'impression (imprimante hors ligne, mauvaises coordonnées...)
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