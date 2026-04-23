package apps.app.controllers.api.patron;

import apps.app.dao.DepartementDAO;
import apps.app.models.Departement;
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

@WebServlet("/api/patron/departements/*")
public class PatronDepartementsApiServlet extends HttpServlet {
    private DepartementDAO departementDAO = new DepartementDAO();
    private Gson gson = new Gson();

    // GET : liste (sans ID) ou détail (avec ID)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Liste de tous les départements
                List<Departement> departements = departementDAO.findAll();
                resp.getWriter().write(gson.toJson(departements));
            } else {
                // Détail d'un département (ex: /api/patron/departments/5)
                int id = Integer.parseInt(pathInfo.substring(1));
                Departement dept = departementDAO.findById(id);
                if (dept == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Département non trouvé\"}");
                } else {
                    resp.getWriter().write(gson.toJson(dept));
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID invalide\"}");
        }
    }

    // POST : création
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        Departement dept = gson.fromJson(reader, Departement.class);
        departementDAO.create(dept);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(dept));
    }

    // PUT : mise à jour complète
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int id = Integer.parseInt(pathInfo.substring(1));
        BufferedReader reader = req.getReader();
        Departement dept = gson.fromJson(reader, Departement.class);
        dept.setId(id);
        departementDAO.update(dept);
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(dept));
    }

    // DELETE : suppression
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int id = Integer.parseInt(pathInfo.substring(1));
        departementDAO.delete(id);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}