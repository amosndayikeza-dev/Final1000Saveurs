package apps.app.dao;

import apps.app.models.Users;
import apps.app.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // CREATE
    public void create(Users user)
    {
        String sql = "INSERT INTO users (name, first_name, last_name, email, password, phone, is_active, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getPhone());
            stmt.setBoolean(7, user.isActive());
            stmt.setString(8, user.getRole());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // READ by ID
    public Users findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // READ by email
    public Users findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // READ all
    public List<Users> findAll(){
        List<Users> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // UPDATE
    public void update(Users user) {
        String sql = "UPDATE users SET name = ?, first_name = ?, last_name = ?, email = ?, " +
                "password = ?, phone = ?, is_active = ?, role = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getPhone());
            stmt.setBoolean(7, user.isActive());
            stmt.setString(8, user.getRole());
            stmt.setInt(9, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Aucun utilisateur trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // transformation d'une ligne en objet User
    private Users mapRowToUser(ResultSet rs) {
        try {
            Users user = new Users();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setPhone(rs.getString("phone"));
            user.setActive(rs.getBoolean("is_active"));
            user.setRole(rs.getString("role"));
            user.setCreatedAt(rs.getTimestamp("created_at"));
            user.setUpdatedAt(rs.getTimestamp("updated_at"));
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Trouver les utilisateur par role
    public List<Users> findByRole(String role){
        List<Users> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY name";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // compter les utilisateurs par role
    public int countUserRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Methode pour les recherchea
    public List<Users> findByCriteria(String name,String email,String role,Boolean isActive) throws SQLException{
        List<Users> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            sql.append(" AND (name LIKE ? OR first_name LIKE ? OR last_name LIKE?)");
            String like = "%" + name + "%";
            params.add(like);
            params.add(like);
            params.add(like);

        }
        if (email != null && !email.isEmpty()){
            sql.append(" AND email = ?");
            params.add(email);
        }
        if (role != null && !role.isEmpty()){
            sql.append(" AND role = ?");
            params.add(role);
        }
        if (isActive != null){
            sql.append(" AND is_active = ?");
            params.add(isActive);
        }
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i< params.size(); i++){
                stmt.setObject(i+1,params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                list.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}