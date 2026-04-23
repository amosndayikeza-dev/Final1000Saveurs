package apps;

import apps.app.models.Departement;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import apps.app.dao.UserDAO;
import apps.app.dao.DepartementDAO;
import apps.app.models.Users;
import apps.app.models.Departement;
import apps.app.utils.PasswordUtil;
import com.google.gson.Gson;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

public class TestApiServer {

    private static final Gson gson = new Gson();
    private static final UserDAO userDAO = new UserDAO();
    private static final DepartementDAO departementDAO = new DepartementDAO();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);

        server.createContext("/api/auth/login", new LoginHandler());
        server.createContext("/api/patron/departements", new DepartementsHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Serveur de test démarré sur http://localhost:8082");
        System.out.println("POST /api/auth/login");
        System.out.println("GET /api/patron/departements");
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");
                return;
            }
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> creds = gson.fromJson(body, Map.class);
            String email = creds.get("email");
            String password = creds.get("password");
            Users user = userDAO.findByEmail(email);
            if (user != null && PasswordUtil.check(password, user.getPassword())) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("role", user.getRole());
                resp.put("userId", user.getId());
                resp.put("userName", user.getFirstName() + " " + user.getLastName());
                sendResponse(exchange, 200, gson.toJson(resp));
            } else {
                sendResponse(exchange, 401, "{\"error\":\"Identifiants incorrects\"}");
            }
        }
    }

    static class DepartementsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Departement> depts = departementDAO.findAll();
                sendResponse(exchange, 200, gson.toJson(depts));
            } else if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Departement dept = gson.fromJson(body, Departement.class);
                departementDAO.create(dept);
                sendResponse(exchange, 201, gson.toJson(dept));
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");
            }
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}