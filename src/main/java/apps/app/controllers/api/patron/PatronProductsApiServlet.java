package apps.app.controllers.api.patron;

import apps.app.dao.ProductDAO;
import apps.app.models.Product;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/patron/products/*")
public class PatronProductsApiServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            //  NOUVEAU : Récupérer le paramètre de filtrage
            String deptParam = req.getParameter("departement_id");

            // Cas 1 : Filtrer par département
            if (deptParam != null && !deptParam.isEmpty()) {
                int deptId = Integer.parseInt(deptParam);
                List<Product> products = productDAO.findByDepartement(deptId);
                resp.getWriter().write(gson.toJson(products));
                return;
            }

            // Cas 2 : Lister tous les produits
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Product> products = productDAO.findAll();
                resp.getWriter().write(gson.toJson(products));
                return;
            }

            // Cas 3 : Produit par ID
            int id = Integer.parseInt(pathInfo.substring(1));
            Product product = productDAO.findById(id);
            if (product == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Produit non trouvé\"}");
            } else {
                resp.getWriter().write(gson.toJson(product));
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID invalide\"}");
        } catch (SQLException e) {
            // ✅ CORRIGÉ : Ne pas lancer RuntimeException, renvoyer une erreur HTTP
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Erreur base de données: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        Product product = gson.fromJson(reader, Product.class);
        try {
            productDAO.create(product);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(product));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int id = Integer.parseInt(pathInfo.substring(1));
        BufferedReader reader = req.getReader();
        Product product = gson.fromJson(reader, Product.class);
        product.setId(id);
        try {
            productDAO.update(product);
            // ✅ CORRIGÉ : Envoyer la réponse seulement après succès
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(product));
        } catch (SQLException e) {
            // ✅ CORRIGÉ : Ne pas lancer RuntimeException
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int id = Integer.parseInt(pathInfo.substring(1));
        try {
            productDAO.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            // ✅ CORRIGÉ : Gérer l'erreur correctement
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}