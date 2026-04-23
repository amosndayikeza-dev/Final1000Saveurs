package apps.app.controllers.api.patron;

import apps.app.dao.DepartementDAO;
import apps.app.dao.EmployeeDAO;
import apps.app.models.Departement;
import apps.app.models.Employee;
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

@WebServlet("/api/patron/employees/*")
public class PatronEmployeesApiServlet extends HttpServlet {

    private EmployeeDAO employeeDAO = new EmployeeDAO();
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
                List<Employee> employees = employeeDAO.findAll();
                resp.getWriter().write(gson.toJson(employees));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Employee emp = employeeDAO.findById(id);
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
        Employee emp = gson.fromJson(reader, Employee.class);
        employeeDAO.create(emp);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(emp));
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
        Employee emp = gson.fromJson(reader, Employee.class);
        emp.setId(id);
        employeeDAO.update(emp);
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(emp));
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

























































