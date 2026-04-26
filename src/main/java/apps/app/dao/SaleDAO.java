package apps.app.dao;

import apps.app.models.Sale;
import apps.app.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    //create
    public void create(Sale sale){
        String sql = "INSERT INTO sales(departement_id, sold_at, total_amount, created_by, notes) VALUES (?, ?, ?, ?, ?)";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, sale.getDepartementId());
            stmt.setDate(2, (Date) sale.getSoldAt());
            stmt.setDouble(3, sale.getTotalAmount());
            stmt.setInt(4, sale.getCreatedBy());
            stmt.setString(5, sale.getNotes());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            while (rs.next()){
              sale.setId(rs.getInt(1));;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //find by id
    public Sale findById(int id) throws SQLException{
        String sql = "SELECT * FROM sales WHERE id = ?";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1,id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return mapRowToSale(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //display all sales
    public List<Sale> findAll(){
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales ORDER BY sold_at DESC";

        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    list.add(mapRowToSale(rs));
                }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //findByDepartement
    public List<Sale> findByDepartement(int departementId){
        List<Sale> list = new ArrayList<>();

        String sql = "SELECT * FROM sales WHERE departement_id = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1,departementId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                list.add(mapRowToSale(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //update
    public void update(Sale sale) throws SQLException {
        String sql = "UPDATE sales SET departement_id = ?, sold_at = ?, total_amount = ?, created_by = ?, notes = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sale.getDepartementId());
            stmt.setDate(2, sale.getSoldAt());
            stmt.setDouble(3, sale.getTotalAmount());
            stmt.setInt(4, sale.getCreatedBy());
            stmt.setString(5, sale.getNotes());
            stmt.setInt(6, sale.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //delete
    public void delete(int id) {
        String sql = "DELETE FROM sales WHERE id = ?";
        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Aucune vente trouvée avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Méthodes additionnelles utiles pour les rapports
    public List<Sale> findByDateRange(Date startDate, Date endDate){
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales WHERE sold_at BETWEEN ? AND ? ORDER BY sold_at DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToSale(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //Mapper les dommees
    public Sale mapRowToSale(ResultSet rs){
        Sale sale = null;
        try {
            sale = new Sale();
            sale.setId(rs.getInt("id"));
            sale.setDepartementId(rs.getInt("departement_id"));
            sale.setSoldAt(rs.getDate("sold_at"));
            sale.setTotalAmount(rs.getDouble("total_amount"));
            sale.setCreatedBy(rs.getInt("created_by"));
            sale.setNotes(rs.getString("notes"));
            sale.setCreatedAt(rs.getTimestamp("created_at"));
            sale.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return sale;
    }

    //chercher par critere
    public List<Sale> findByCriteria(Integer departementId, Integer createdBy, Date fromDate, Date toDate, Double minAmount, Double maxAmount) throws SQLException {
        List<Sale> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM sales WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (departementId != null) {
            sql.append(" AND departement_id = ?");
            params.add(departementId);
        }
        if (createdBy != null) {
            sql.append(" AND created_by = ?");
            params.add(createdBy);
        }
        if (fromDate != null) {
            sql.append(" AND sold_at >= ?");
            params.add(fromDate);
        }
        if (toDate != null) {
            sql.append(" AND sold_at <= ?");
            params.add(toDate);
        }
        if (minAmount != null) {
            sql.append(" AND total_amount >= ?");
            params.add(minAmount);
        }
        if (maxAmount != null) {
            sql.append(" AND total_amount <= ?");
            params.add(maxAmount);
        }

        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToSale(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Sale> findByDepartementAndDate(int departementId, Date start, Date end) throws SQLException {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales WHERE departement_id = ? AND sold_at BETWEEN ? AND ? ORDER BY sold_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departementId);
            stmt.setDate(2, start);
            stmt.setDate(3, end);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToSale(rs));
            }
        }
        return list;
    }


    public double getTotalSalesByPeriod(Date start, Date end) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM sales WHERE sold_at BETWEEN ? AND ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, start);
            stmt.setDate(2, end);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}