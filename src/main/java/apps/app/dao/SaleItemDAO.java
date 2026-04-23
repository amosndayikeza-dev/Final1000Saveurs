package apps.app.dao;

import apps.app.models.Debt;
import apps.app.models.Sale;
import apps.app.models.SaleItem;
import apps.app.utils.DBConnection;
import com.mysql.cj.xdevapi.PreparableStatement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleItemDAO {

    //create
    public void create(SaleItem item) throws SQLException {
        String sql = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price, is_paid, client_name) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getSaleId());
            stmt.setInt(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getUnitPrice());
            stmt.setBoolean(5, item.isPaid());
            stmt.setString(6, item.getClientName());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                item.setId(rs.getInt(1));
            }
        }
    }

    //find by id
    public SaleItem findById(int id){

        String sql = "SELECT * FROM sale+items WHERE id = ? ";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1,id);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()){
                    return mapRowToSaleItem(rs);
                }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //find by sale id
    public List<SaleItem> findBySaleId(int saleId){
        List<SaleItem> list = new ArrayList<>();
        String sql = "SELECT * FROM sale_items WHERE sale_items = ? ORDER BY";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1,saleId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                list.add(mapRowToSaleItem(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //update
    public void update(SaleItem item){
        String sql = "UPDATE sale_items SET sale_id = ?, product_id = ?, quantity = ?, unit_price = ?,is_paid = ?, client_name = ? WHERE id = ?";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1,item.getSaleId());
            stmt.setInt(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getUnitPrice());
            stmt.setBoolean(5, item.isPaid());
            stmt.setString(6, item.getClientName());
            stmt.setInt(7, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //delete
    public void delete(int id){
        String sql = "DELETE FROM sale_items WHERE id = ? ";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1,id);

            int affected = stmt.executeUpdate();
            if (affected == 0 ){
                throw new SQLException("Aucun SaleItem trouve avec l'ID " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //mapper les donnees provenant de la base des donnees
    private SaleItem mapRowToSaleItem(ResultSet rs){
        try {
            SaleItem item = new SaleItem();
            item.setId(rs.getInt("id"));
            item.setSaleId(rs.getInt("sale_id"));
            item.setProductId(rs.getInt("product_id"));
            item.setQuantity(rs.getInt("quantity"));
            item.setUnitPrice(rs.getDouble("unit_price"));
            item.setPaid(rs.getBoolean("is_paid"));
            item.setClientName(rs.getString("client_name"));
            item.setCreatedAt(rs.getTimestamp("created_at"));
            item.setUpdatedAt(rs.getTimestamp("updated_at"));
            return item;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //chercher par critere
    public List<SaleItem> findByCriteria(Integer saleId, Integer productId, Boolean isPaid, String clientName) throws SQLException {
        List<SaleItem> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM sale_items WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (saleId != null) {
            sql.append(" AND sale_id = ?");
            params.add(saleId);
        }
        if (productId != null) {
            sql.append(" AND product_id = ?");
            params.add(productId);
        }
        if (isPaid != null) {
            sql.append(" AND is_paid = ?");
            params.add(isPaid);
        }
        if (clientName != null && !clientName.isEmpty()) {
            sql.append(" AND client_name LIKE ?");
            params.add("%" + clientName + "%");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToSaleItem(rs));
            }
        }
        return list;
    }


    public void deleteBySaleId(int saleId) throws SQLException {
        String sql = "DELETE FROM sale_items WHERE sale_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, saleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
