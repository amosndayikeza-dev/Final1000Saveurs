package apps.app.controllers;

import apps.app.dao.UserDAO;
import apps.app.models.Users;
import apps.app.utils.PasswordUtil;
// Imports pour les servlets (version javax, compatible Tomcat 9)
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/auth/*")
public class AuthController extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo(); // Récupère la partie après "/auth"
        if ("/login".equals(path)) {
            // Affiche la page de connexion statique login.html
            req.getRequestDispatcher("/login.html").forward(req, resp);
        } else if ("/logout".equals(path)) {
            // Invalide la session et redirige vers login
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/auth/login");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if ("/login".equals(path)) {
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            Users user = userDAO.findByEmail(email);
            // Vérifie l'existence de l'utilisateur et la correspondance du mot de passe
            if (user != null && PasswordUtil.check(password, user.getPassword())) {
                HttpSession session = req.getSession();
                session.setAttribute("userId", user.getId());
                session.setAttribute("role", user.getRole());
                session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());

                // Redirection selon le rôle stocké en base
                switch (user.getRole()) {
                    case "manager":
                        resp.sendRedirect(req.getContextPath() + "/manager/dashboardpatron.php");
                        break;
                    case "admin":
                        resp.sendRedirect(req.getContextPath() + "/admin/dashboard.html");
                        break;
                    default: // patron ou autre
                        resp.sendRedirect(req.getContextPath() + "/patron/dashboardpatron.php");
                }
            } else {
                // Identifiants incorrects : redirection avec paramètre d'erreur
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=1");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}