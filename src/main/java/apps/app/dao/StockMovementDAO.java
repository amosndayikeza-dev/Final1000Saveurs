package apps.app.dao;

import apps.app.models.StockMovement;
import apps.app.utils.DBConnection;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockMovementDAO {

    //CREATE
    public void create(StockMovement movement) {
        String sql = "INSERT INTO stock_movements (product_id,quantity,type,reason,reference_id,created_by) VALUES(?,?,?,?,?,?)";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,movement.getProductId());
            stmt.setInt(2,movement.getQuantity());
            stmt.setString(3,movement.getType());
            stmt.setString(4,movement.getReason());
            stmt.setInt(5,movement.getReferenceId());
            stmt.setInt(6,movement.getCreatedBy());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Read by id
    public StockMovement findById(int id){
        String sql = "SELECT * FROM stock_movements WHERE id =?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return mapRowToStockMovement(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //READ ALL(avec ordre)
    public List<StockMovement> findAll() {
        List<StockMovement> list = new ArrayList<>();
        String sql = "SELECT*FROM stock_movements ORDER BY created_at DESC";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    list.add(mapRowToStockMovement(rs));
                }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //READ by product
    public List<StockMovement> findByProduct(int productId) {
        List<StockMovement> list = new ArrayList<>();
        String sql = "SELECT * FROM stock_movements WHERE product_id = ? ORDER BY created_at DESC";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                list.add(mapRowToStockMovement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //  Delete
    public void delete(int id){
        String sql = "DELETE FROM stock_movements WHERE id =?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,id);
            int affected = stmt.executeUpdate();
            if (affected == 0){
                throw new SQLException("Aucun mouvement de stock trouve avec l'ID" + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // Helper : transformation ResultSet -> StockMovement
    private StockMovement mapRowToStockMovement(ResultSet rs) {
        try {
            StockMovement movement = new StockMovement();
            movement.setId(rs.getInt("id"));
            movement.setProductId(rs.getInt("product_id"));
            movement.setQuantity(rs.getInt("quantity"));
            movement.setType(rs.getString("type"));
            movement.setReason(rs.getString("reason"));
            int refId = rs.getInt("reference_id");
            if (rs.wasNull()) {
                movement.setReferenceId(null);
            } else {
                movement.setReferenceId(refId);
            }
            movement.setCreatedBy(rs.getInt("created_by"));
            movement.setCreatedAt(rs.getTimestamp("created_at"));
            movement.setUpdatedAt(rs.getTimestamp("updated_at"));
            return movement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //checher par critere
    public List<StockMovement> findByCriteria(Integer productId, String type, String reason, Date fromDate, Date toDate) throws SQLException {
        List<StockMovement> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM stock_movements WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (productId != null) {
            sql.append(" AND product_id = ?");
            params.add(productId);
        }
        if (type != null && !type.isEmpty()) {
            sql.append(" AND type = ?");
            params.add(type);
        }
        if (reason != null && !reason.isEmpty()) {
            sql.append(" AND reason = ?");
            params.add(reason);
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
                list.add(mapRowToStockMovement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}

