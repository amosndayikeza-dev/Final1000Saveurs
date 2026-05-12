package apps.app.controllers.api.patron;

import apps.app.dao.DepartementDAO;
import apps.app.dao.EmployeeDAO;
import apps.app.dao.UserDAO;
import apps.app.models.Departement;
import apps.app.models.Employee;
import apps.app.models.Users;
import apps.app.utils.PasswordUtil;
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
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/api/patron/employees/*")
public class PatronEmployeesApiServlet extends HttpServlet {

    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    public PatronEmployeesApiServlet() throws SQLException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                String departementIdParam = req.getParameter("departement_id");
                List<Map<String, Object>> employees;
                if (departementIdParam != null && !departementIdParam.equals("all")) {
                    int deptId = Integer.parseInt(departementIdParam);
                    employees = employeeDAO.findAllWithUserDetails().stream()
                        .filter(e -> (Integer) e.get("departementId") == deptId)
                        .collect(Collectors.toList());
                } else {
                    employees = employeeDAO.findAllWithUserDetails();
                }
                resp.getWriter().write(gson.toJson(employees));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Map<String, Object> emp = employeeDAO.findAllWithUserDetails().stream()
                    .filter(e -> (Integer) e.get("id") == id)
                    .findFirst().orElse(null);
                if (emp == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Employé non trouvé\"}");
                } else {
                    resp.getWriter().write(gson.toJson(emp));
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ID invalide\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        Map<String, Object> data = gson.fromJson(reader, Map.class);
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String email = (String) data.get("email");
        double salary = ((Number) data.get("salary")).doubleValue();
        String role = (String) data.get("role");
        int departementId = ((Number) data.get("departementId")).intValue();

        // Create user
        Users user = new Users();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(PasswordUtil.hash("password123")); // default password
        user.setRole("employe");
        user.setActive(true);
        userDAO.create(user);

        // Create employee
        Employee emp = new Employee();
        emp.setUserId(user.getId());
        emp.setDepartementId(departementId);
        emp.setPosition(role);
        emp.setSalary(salary);
        emp.setHiredAt(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        employeeDAO.create(emp);

        Map<String, Object> response = Map.of(
            "id", emp.getId(),
            "userId", user.getId(),
            "firstName", firstName,
            "lastName", lastName,
            "email", email,
            "departementId", departementId,
            "role", role,
            "salary", salary,
            "hireDate", emp.getHiredAt()
        );
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(response));
    }

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
        Map<String, Object> data = gson.fromJson(reader, Map.class);
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String email = (String) data.get("email");
        double salary = ((Number) data.get("salary")).doubleValue();
        String role = (String) data.get("role");
        int departementId = ((Number) data.get("departementId")).intValue();

        Employee emp = employeeDAO.findById(id);
        if (emp != null) {
            Users user = userDAO.findById(emp.getUserId());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            userDAO.update(user);

            emp.setDepartementId(departementId);
            emp.setPosition(role);
            emp.setSalary(salary);
            employeeDAO.update(emp);

            Map<String, Object> response = Map.of(
                "id", emp.getId(),
                "userId", user.getId(),
                "firstName", firstName,
                "lastName", lastName,
                "email", email,
                "departementId", departementId,
                "role", role,
                "salary", salary,
                "hireDate", emp.getHiredAt()
            );
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Employé non trouvé\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int id = Integer.parseInt(pathInfo.substring(1));
        employeeDAO.delete(id);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
