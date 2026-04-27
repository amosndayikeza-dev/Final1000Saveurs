package apps.app.dao;

import apps.app.models.Employee;
import apps.app.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {

    //create
    public void create(Employee employee) {
        String sql = "INSERT INTO employees (user_id, departement_id, position, salary, hired_at) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, employee.getUserId());
            stmt.setInt(2, employee.getDepartementId());
            stmt.setString(3, employee.getPosition());
            stmt.setDouble(4, employee.getSalary());
            stmt.setString(5, employee.getHiredAt());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                employee.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Employee findById(int id){
        String sql = "SELECT * FROM employees WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToEmployee(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Employee> findAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY id";
        try {
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Map<String,Object>> findAllWithUserDetails(){
        List<Map<String,Object>> list = new ArrayList<>();
        String sql = "SELECT e.id,e.user_id,u.first_name,u.last_name,e.position FROM employees e JOIN users u ON e.user_id = u.id";
        try{
            Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    Map<String,Object> map = new HashMap<>();
                    map.put("id",rs.getInt("id"));
                    map.put("user_id",rs.getInt("user_id"));
                    map.put("first_name",rs.getString("first_name"));
                    map.put("last_name",rs.getString("last_name"));
                    map.put("position",rs.getString("position"));
                    list.add(map);
                }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    public List<Employee> findByDepartement(int departementId) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE departement_id = ? ORDER BY id";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departementId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void update(Employee employee) {
        String sql = "UPDATE employees SET user_id = ?, departement_id = ?, position = ?, salary = ?, hired_at = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employee.getUserId());
            stmt.setInt(2, employee.getDepartementId());
            stmt.setString(3, employee.getPosition());
            stmt.setDouble(4, employee.getSalary());
            stmt.setString(5, employee.getHiredAt());
            stmt.setInt(6, employee.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void delete(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Aucun employé trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //chercher par critere
    public List<Employee> findByCriteria(Integer departementId, String position, Double minSalary, Double maxSalary, Date fromHire, Date toHire) throws SQLException {
        List<Employee> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM employees WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (departementId != null) {
            sql.append(" AND department_id = ?");
            params.add(departementId);
        }
        if (position != null && !position.isEmpty()) {
            sql.append(" AND position LIKE ?");
            params.add("%" + position + "%");
        }
        if (minSalary != null) {
            sql.append(" AND salary >= ?");
            params.add(minSalary);
        }
        if (maxSalary != null) {
            sql.append(" AND salary <= ?");
            params.add(maxSalary);
        }
        if (fromHire != null) {
            sql.append(" AND hired_at >= ?");
            params.add(fromHire);
        }
        if (toHire != null) {
            sql.append(" AND hired_at <= ?");
            params.add(toHire);
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //totaly fo salary in each departement
    public double getTotalSalaryByDepartement(int departementId) throws SQLException {
        String sql = "SELECT SUM(salary) AS total FROM employees WHERE departement_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departementId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    public Employee findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM employees WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToEmployee(rs);
            }
        }
        return null;
    }


    private Employee mapRowToEmployee(ResultSet rs){
        try {
            Employee emp = new Employee();
            emp.setId(rs.getInt("id"));
            emp.setUserId(rs.getInt("user_id"));
            emp.setDepartementId(rs.getInt("departement_id"));
            emp.setPosition(rs.getString("position"));
            emp.setSalary(rs.getDouble("salary"));
            emp.setHiredAt(rs.getString("hired_at"));
            emp.setCreatedAt(rs.getTimestamp("created_at"));
            emp.setUpdatedAt(rs.getTimestamp("updated_at"));
            return emp;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getLastEmployee() throws SQLException {
        String sql = "SELECT CONCAT(u.first_name, ' ', u.last_name) AS name, e.created_at AS date " +
                "FROM employees e JOIN users u ON e.user_id = u.id " +
                "ORDER BY e.id DESC LIMIT 1"; // ou ORDER BY e.created_at DESC
        try {
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("name", rs.getString("name"));
                map.put("date", rs.getTimestamp("date").toString());
                return map;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
