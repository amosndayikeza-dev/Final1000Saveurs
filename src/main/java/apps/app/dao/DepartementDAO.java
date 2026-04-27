package apps.app.dao;

import apps.app.models.Departement;
import apps.app.models.Departement;
import apps.app.utils.DBConnection;
import java.sql.*;
import java.util.*;

public class DepartementDAO {

    public void create(Departement departement) {
        String sql = "INSERT INTO departements (name, address, description, manager_id) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, departement.getName());
            stmt.setString(2, departement.getAddress());
            stmt.setString(3, departement.getDescription());
            if (departement.getManagerId() == 0) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, departement.getManagerId());
            }
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                departement.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Departement findById(int id) {
        String sql = "SELECT * FROM departements WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToDepartement(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Departement> findAll() {
        List<Departement> list = new ArrayList<>();
        String sql = "SELECT * FROM departements ORDER BY name";
        try {
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToDepartement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void update(Departement departement) {
        String sql = "UPDATE departements SET name = ?, address = ?, description = ?, manager_id = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, departement.getName());
            stmt.setString(2, departement.getAddress());
            stmt.setString(3, departement.getDescription());
            if (departement.getManagerId() == 0) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, departement.getManagerId());
            }
            stmt.setInt(5, departement.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM departements WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Aucun département trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Departement mapRowToDepartement(ResultSet rs) throws SQLException {
        try {
            Departement dept = new Departement();
            dept.setId(rs.getInt("id"));
            dept.setName(rs.getString("name"));
            dept.setAddress(rs.getString("address"));
            dept.setDescription(rs.getString("description"));
            dept.setManagerId(rs.getInt("manager_id"));
            dept.setCreatedAt(rs.getTimestamp("created_at"));
            dept.setUpdatedAt(rs.getTimestamp("updated_at"));
            return dept;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //compter les employes d'un departement
    public int countEmployees(int departementId) {
        String sql = "SELECT COUNT(*) FROM eployees WHERE departement_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departementId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //methode pour faire des recherches
    public List<Departement> findByCriteria(String name,Integer managerId){
        List<Departement> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM departements WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (name != null && !name.isEmpty()){
            sql.append("AND name LIKE ? ");
            params.add("%" + name + "%");
        }
        if (managerId != null){
            sql.append("AND manager_id = ?");
            params.add(managerId);
        }
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            for (int i = 0;i < params.size();i++){
                stmt.setObject(i+1,params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                list.add(mapRowToDepartement(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Map<String, String> getLastDepartement() throws SQLException {
        String sql = "SELECT name, created_at AS date FROM departements ORDER BY id DESC LIMIT 1";
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

