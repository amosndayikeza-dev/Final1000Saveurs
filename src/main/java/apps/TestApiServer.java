package apps;

import apps.app.models.Departement;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import apps.app.dao.UserDAO;
import apps.app.dao.DepartementDAO;
import apps.app.dao.EmployeeDAO;
import apps.app.models.Users;
import apps.app.models.Departement;
import apps.app.models.Employee;
import apps.app.utils.PasswordUtil;
import com.google.gson.Gson;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class TestApiServer {

//    private static final Gson gson = new Gson();
//    private static final UserDAO userDAO = new UserDAO();
//    private static final DepartementDAO departementDAO = new DepartementDAO();
//    private static final EmployeeDAO employeeDAO = new EmployeeDAO();
//
//    public static void main(String[] args) throws IOException {
//        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
//
//        server.createContext("/api/auth/login", new LoginHandler());
//        server.createContext("/api/patron/departements", new DepartementsHandler());
//        server.createContext("/api/patron/employees", new EmployeesHandler());
//
//        server.setExecutor(null);
//        server.start();
//        System.out.println("Serveur de test démarré sur http://localhost:8082");
//        System.out.println("POST /api/auth/login");
//        System.out.println("GET /api/patron/departements");
//        System.out.println("GET /api/patron/employees");
//    }
//
//    static class LoginHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            if (!"POST".equals(exchange.getRequestMethod())) {
//                sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");
//                return;
//            }
//            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
//            Map<String, String> creds = gson.fromJson(body, Map.class);
//            String email = creds.get("email");
//            String password = creds.get("password");
//            Users user = userDAO.findByEmail(email);
//            if (user != null && PasswordUtil.check(password, user.getPassword())) {
//                Map<String, Object> resp = new HashMap<>();
//                resp.put("success", true);
//                resp.put("role", user.getRole());
//                resp.put("userId", user.getId());
//                resp.put("userName", user.getFirstName() + " " + user.getLastName());
//                sendResponse(exchange, 200, gson.toJson(resp));
//            } else {
//                sendResponse(exchange, 401, "{\"error\":\"Identifiants incorrects\"}");
//            }
//        }
//    }
//
//    static class DepartementsHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            if ("GET".equals(exchange.getRequestMethod())) {
//                List<Departement> depts = departementDAO.findAll();
//                sendResponse(exchange, 200, gson.toJson(depts));
//            } else if ("POST".equals(exchange.getRequestMethod())) {
//                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
//                Departement dept = gson.fromJson(body, Departement.class);
//                departementDAO.create(dept);
//                sendResponse(exchange, 201, gson.toJson(dept));
//            } else {
//                sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");
//            }
//        }
//    }
//
//    static class EmployeesHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            String path = exchange.getRequestURI().getPath();
//            String method = exchange.getRequestMethod();
//            String query = exchange.getRequestURI().getQuery();
//
//            if (path.equals("/api/patron/employees")) {
//                if ("GET".equals(method)) {
//                    List<Map<String, Object>> emps;
//                    if (query != null && query.contains("departement_id=")) {
//                        String[] params = query.split("=");
//                        if (params.length == 2) {
//                            int deptId = Integer.parseInt(params[1]);
//                            emps = employeeDAO.findAllWithUserDetails().stream()
//                                .filter(e -> (Integer) e.get("departementId") == deptId)
//                                .collect(Collectors.toList());
//                        } else {
//                            emps = employeeDAO.findAllWithUserDetails();
//                        }
//                    } else {
//                        emps = employeeDAO.findAllWithUserDetails();
//                    }
//                    sendResponse(exchange, 200, gson.toJson(emps));
//                } else if ("POST".equals(method)) {
//                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
//                    Map<String, Object> data = gson.fromJson(body, Map.class);
//                    String firstName = (String) data.get("firstName");
//                    String lastName = (String) data.get("lastName");
//                    String email = (String) data.get("email");
//                    double salary = ((Number) data.get("salary")).doubleValue();
//                    String role = (String) data.get("role");
//                    int departementId = ((Number) data.get("departementId")).intValue();
//
//                    // Create user
//                    Users user = new Users();
//                    user.setFirstName(firstName);
//                    user.setLastName(lastName);
//                    user.setEmail(email);
//                    user.setPassword(PasswordUtil.hash("password123")); // default password
//                    user.setRole("employe");
//                    user.setActive(true);
//                    userDAO.create(user);
//
//                    // Create employee
//                    Employee emp = new Employee();
//                    emp.setUserId(user.getId());
//                    emp.setDepartementId(departementId);
//                    emp.setPosition(role);
//                    emp.setSalary(salary);
//                    emp.setHiredAt(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
//                    employeeDAO.create(emp);
//
//                    Map<String, Object> resp = new HashMap<>();
//                    resp.put("id", emp.getId());
//                    resp.put("userId", user.getId());
//                    resp.put("firstName", firstName);
//                    resp.put("lastName", lastName);
//                    resp.put("email", email);
//                    resp.put("departementId", departementId);
//                    resp.put("role", role);
//                    resp.put("salary", salary);
//                    resp.put("hireDate", emp.getHiredAt());
//                    sendResponse(exchange, 201, gson.toJson(resp));
//                } else {
//                    sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");
//                }
//            } else if (path.matches("/api/patron/employees/\\d+")) {
//                String[] parts = path.split("/");
//                int id = Integer.parseInt(parts[parts.length - 1]);
//                if ("GET".equals(method)) {
//                    Map<String, Object> emp = employeeDAO.findAllWithUserDetails().stream()
//                        .filter(e -> (Integer) e.get("id") == id)
//                        .findFirst().orElse(null);
//                    if (emp != null) {
//                        sendResponse(exchange, 200, gson.toJson(emp));
//                    } else {
//                        sendResponse(exchange, 404, "{\"error\":\"Employé non trouvé\"}");
//                    }
//                } else if ("PUT".equals(method)) {
//                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
//                    Map<String, Object> data = gson.fromJson(body, Map.class);
//                    String firstName = (String) data.get("firstName");
//                    String lastName = (String) data.get("lastName");
//                    String email = (String) data.get("email");
//                    double salary = ((Number) data.get("salary")).doubleValue();
//                    String role = (String) data.get("role");
//                    int departementId = ((Number) data.get("departementId")).intValue();
//
//                    Employee emp = employeeDAO.findById(id);
//                    if (emp != null) {
//                        Users user = userDAO.findById(emp.getUserId());
//                        user.setFirstName(firstName);
//                        user.setLastName(lastName);
//                        user.setEmail(email);
//                        userDAO.update(user);
//
//                        emp.setDepartementId(departementId);
//                        emp.setPosition(role);
//                        emp.setSalary(salary);
//                        employeeDAO.update(emp);
//
//                        Map<String, Object> resp = new HashMap<>();
//                        resp.put("id", emp.getId());
//                        resp.put("userId", user.getId());
//                        resp.put("firstName", firstName);
//                        resp.put("lastName", lastName);
//                        resp.put("email", email);
//                        resp.put("departementId", departementId);
//                        resp.put("role", role);
//                        resp.put("salary", salary);
//                        resp.put("hireDate", emp.getHiredAt());
//                        sendResponse(exchange, 200, gson.toJson(resp));
//                    } else {
//                        sendResponse(exchange, 404, "{\"error\":\"Employé non trouvé\"}");
//                    }
//                } else if ("DELETE".equals(method)) {
//                    Employee emp = employeeDAO.findById(id);
//                    if (emp != null) {
//                        employeeDAO.delete(id);
//                        // Optionally delete user too, but maybe not
//                        sendResponse(exchange, 200, "{\"message\":\"Employé supprimé\"}");
//                    } else {
//                        sendResponse(exchange, 404, "{\"error\":\"Employé non trouvé\"}");
//                    }
//                } else {
//                    sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");
//                }
//            } else {
//                sendResponse(exchange, 404, "{\"error\":\"Endpoint non trouvé\"}");
//            }
//        }
//    }
//
//    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
//        exchange.getResponseHeaders().set("Content-Type", "application/json");
//        exchange.sendResponseHeaders(statusCode, response.length());
//        OutputStream os = exchange.getResponseBody();
//        os.write(response.getBytes());
//        os.close();
//    }
}