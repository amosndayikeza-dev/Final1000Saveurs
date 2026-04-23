package apps.app.dao;

import apps.app.models.Notification;
import apps.app.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    //create
        public void create(Notification notif) {
            String sql = "INSERT INTO notifications (user_id, type, message, link, read_at) VALUES (?, ?, ?, ?, ?)";
            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, notif.getUserId());
                stmt.setString(2, notif.getType());
                stmt.setString(3, notif.getMessage());
                stmt.setString(4, notif.getLink());
                stmt.setTimestamp(5, notif.getReadAt());
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    notif.setId(rs.getInt(1));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    //findbyID
    public Notification findById(int id){
        String sql = "SELECT * FROM notifications WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToNotification(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //find all
    public List<Notification> findAll(){
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications ORDER BY created_at DESC";
        try {
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //find by user
    public List<Notification> findByUser(int userId){
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // find unread by user
    public List<Notification> findUnreadByUser(int userId){
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND read_at IS NULL ORDER BY created_at DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //mark as read
    public void markAsRead(int id){
        String sql = "UPDATE notifications SET read_at = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //markAll as read
    public void markAllAsRead(int userId){
        String sql = "UPDATE notifications SET read_at = ? WHERE user_id = ? AND read_at IS NULL";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //update
    public void update(Notification notif) {
        String sql = "UPDATE notifications SET user_id = ?, type = ?, message = ?, link = ?, read_at = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, notif.getUserId());
            stmt.setString(2, notif.getType());
            stmt.setString(3, notif.getMessage());
            stmt.setString(4, notif.getLink());
            stmt.setTimestamp(5, notif.getReadAt());
            stmt.setInt(6, notif.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //delete
    public void delete(int id) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Aucune notification trouvée avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //map datas
    private Notification mapRowToNotification(ResultSet rs){
        try {
            Notification notif = new Notification();
            notif.setId(rs.getInt("id"));
            notif.setUserId(rs.getInt("user_id"));
            notif.setType(rs.getString("type"));
            notif.setMessage(rs.getString("message"));
            notif.setLink(rs.getString("link"));
            notif.setReadAt(rs.getTimestamp("read_at"));
            notif.setCreatedAt(rs.getTimestamp("created_at"));
            notif.setUpdatedAt(rs.getTimestamp("updated_at"));
            return notif;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //chercher par critere
    public List<Notification> findByCriteria(Integer userId, String type, Boolean isRead, Date fromDate, Date toDate) throws SQLException {
        List<Notification> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM notifications WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append(" AND user_id = ?");
            params.add(userId);
        }
        if (type != null && !type.isEmpty()) {
            sql.append(" AND type = ?");
            params.add(type);
        }
        if (isRead != null) {
            if (isRead) {
                sql.append(" AND read_at IS NOT NULL");
            } else {
                sql.append(" AND read_at IS NULL");
            }
        }
        if (fromDate != null) {
            sql.append(" AND created_at >= ?");
            params.add(fromDate);
        }
        if (toDate != null) {
            sql.append(" AND created_at <= ?");
            params.add(toDate);
        }

        try {
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}


