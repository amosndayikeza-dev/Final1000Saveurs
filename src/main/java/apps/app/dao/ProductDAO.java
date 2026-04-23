package apps.app.dao;

import apps.app.models.Product;
import apps.app.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    //create
    public void create(Product product) throws SQLException {
        String sql = "INSERT INTO products (departement_id, name, description, unit_price, current_stock, low_stock_threshold) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, product.getDepartementId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setDouble(4, product.getUnitPrice());
            stmt.setInt(5, product.getCurrentStock());
            stmt.setInt(6, product.getLowStockThreshold());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                product.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //find by id
    public Product findById(int id){
        String sql = "SELECT * FROM products WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToProduct(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //find all
    public List<Product> findAll(){
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";
        try{
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //find by departement

    public List<Product> findByDepartement(int departementId){
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE departement_id = ? ORDER BY name";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departementId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //find low stock
    public List<Product> findLowStock(){
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE current_stock <= low_stock_threshold ORDER BY current_stock ASC";
        try {
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //update
    public void update(Product product){
        String sql = "UPDATE products SET departement_id = ?, name = ?, description = ?, unit_price = ?, " +
                "current_stock = ?, low_stock_threshold = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, product.getDepartementId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setDouble(4, product.getUnitPrice());
            stmt.setInt(5, product.getCurrentStock());
            stmt.setInt(6, product.getLowStockThreshold());
            stmt.setInt(7, product.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //delete
    public void delete(int id){
        String sql = "DELETE FROM products WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Aucun produit trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // mapping d'un ResultSet en objet Product
    private Product mapRowToProduct(ResultSet rs){
        try {
            Product product = new Product();
            product.setId(rs.getInt("id"));
            product.setDepartementId(rs.getInt("departement_id"));
            product.setName(rs.getString("name"));
            product.setDescription(rs.getString("description"));
            product.setUnitPrice(rs.getDouble("unit_price"));
            product.setCurrentStock(rs.getInt("current_stock"));
            product.setLowStockThreshold(rs.getInt("low_stock_threshold"));
            product.setCreatedAt(rs.getTimestamp("created_at"));
            product.setUpdatedAt(rs.getTimestamp("updated_at"));
            return product;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //chercher par critere
    public List<Product> findByCriteria(Integer departementId, String name, Double minPrice, Double maxPrice, Integer minStock, Integer maxStock) throws SQLException {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (departementId != null) {
            sql.append(" AND departement_id = ?");
            params.add(departementId);
        }
        if (name != null && !name.isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }
        if (minPrice != null) {
            sql.append(" AND unit_price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND unit_price <= ?");
            params.add(maxPrice);
        }
        if (minStock != null) {
            sql.append(" AND current_stock >= ?");
            params.add(minStock);
        }
        if (maxStock != null) {
            sql.append(" AND current_stock <= ?");
            params.add(maxStock);
        }

        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}

