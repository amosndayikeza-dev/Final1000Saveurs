package apps.app.controllers.auth;

import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/me")
public class AuthMeApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Non authentifié\"}");
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("userId", session.getAttribute("userId"));
        data.put("userName", session.getAttribute("userName"));
        data.put("role", session.getAttribute("role"));
        resp.setContentType("application/json");
        new Gson().toJson(data, resp.getWriter());
    }
}