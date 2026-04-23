package apps.app.dao;

import apps.app.models.ActivityLog;
import apps.app.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {
    public List<ActivityLog> findAll() throws SQLException {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs ORDER BY created_at DESC";
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setAction(rs.getString("action"));
                log.setDetails(rs.getString("details"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(log);
            }
        } finally {

        }
        return list;
    }
}