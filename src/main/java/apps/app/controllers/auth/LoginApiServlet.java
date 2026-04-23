package apps.app.controllers.auth;

import apps.app.dao.EmployeeDAO;
import apps.app.dao.UserDAO;
import apps.app.models.Employee;
import apps.app.models.Users;
import apps.app.utils.PasswordUtil;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/login")
public class LoginApiServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        Map<String, String> credentials = gson.fromJson(reader, Map.class);
        String email = credentials.get("email");
        String password = credentials.get("password");

        try {
            Users user = userDAO.findByEmail(email);
            if (user != null && PasswordUtil.check(password, user.getPassword())) {
                HttpSession session = req.getSession();
                session.setAttribute("userId", user.getId());
                session.setAttribute("role", user.getRole());
                session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());

                // Pour le manager : récupérer et stocker son département
                if ("manager".equals(user.getRole())) {
                    Employee employee = employeeDAO.findByUserId(user.getId());
                    if (employee != null) {
                        session.setAttribute("departementId", employee.getDepartementId());
                    }
                }

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("role", user.getRole());
                responseData.put("userId", user.getId());
                responseData.put("userName", user.getFirstName() + " " + user.getLastName());
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(responseData));
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"Email ou mot de passe incorrect\"}");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}