package apps.app.dao;

import apps.app.models.Product;
import apps.app.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAO {

    //create
    public void create(Product product) throws SQLException {
        String sql = "INSERT INTO products (departement_id, name, description, unit_price, current_stock, low_stock_threshold) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
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
    public Product findById(int id) throws SQLException {
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
    public List<Product> findAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";
        try(
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
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

    public List<Product> findByDepartement(int departementId) throws SQLException{
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE departement_id = ? ORDER BY name";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
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
    public List<Product> findLowStock() throws SQLException{
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE current_stock <= low_stock_threshold ORDER BY current_stock ASC";
        try (
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
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
    // Mettre à jour un produit (utilisé dans PUT et pour l'ajustement)
    public void update(Product product) throws SQLException {
        String sql = "UPDATE product SET name=?, description=?, unit_price=?, current_stock=?, low_stock_threshold=?, updated_at=NOW() WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getUnitPrice());
            stmt.setInt(4, product.getCurrentStock());
            stmt.setInt(5, product.getLowStockThreshold());
            stmt.setInt(6, product.getId());
            stmt.executeUpdate();
        }
    }

    //delete
    public boolean delete(int id) throws SQLException{
        String sql = "DELETE FROM products WHERE id = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Aucun produit trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())){
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


    // Ajoutez ces méthodes dans votre classe ProductDAO

    /**
     * Récupère tous les produits avec le nom de leur département.
     * @return une liste de Map contenant les champs du produit + "departement_name"
     */
    public List<Map<String, Object>> findAllWithDepartementName() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        // Construction de la requête SQL avec jointure
        String sql = "SELECT p.*, d.name AS departement_name " +
                "FROM products p " +
                "JOIN departements d ON p.departement_id = d.id";
        try (
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("departement_id", rs.getInt("departement_id"));
                map.put("departement_name", rs.getString("departement_name")); // ← nom du département
                map.put("name", rs.getString("name"));
                map.put("description", rs.getString("description"));
                map.put("unit_price", rs.getDouble("unit_price"));
                map.put("current_stock", rs.getInt("current_stock"));
                map.put("low_stock_threshold", rs.getInt("low_stock_threshold"));
                map.put("created_at", rs.getTimestamp("created_at"));
                map.put("updated_at", rs.getTimestamp("updated_at"));
                list.add(map);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Récupère les produits d'un département donné avec le nom du département.
     * @param departementId l'ID du département
     * @return liste de Map
     */
    public List<Map<String, Object>> findByDepartementWithName(int departementId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.*, d.name AS departement_name " +
                "FROM products p " +
                "JOIN departements d ON p.departement_id = d.id " +
                "WHERE p.departement_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departementId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("departement_id", rs.getInt("departement_id"));
                map.put("departement_name", rs.getString("departement_name"));
                map.put("name", rs.getString("name"));
                map.put("description", rs.getString("description"));
                map.put("unit_price", rs.getDouble("unit_price"));
                map.put("current_stock", rs.getInt("current_stock"));
                map.put("low_stock_threshold", rs.getInt("low_stock_threshold"));
                map.put("created_at", rs.getTimestamp("created_at"));
                map.put("updated_at", rs.getTimestamp("updated_at"));
                list.add(map);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // Liste paginée avec filtres et tri (uniquement actifs)
    public List<Product> findByDepartementWithFilters(int departementId, String search, String sortBy, String order, int page, int size) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE departement_id = ? AND active = true");
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ?)");
        }
        if (sortBy != null && !sortBy.isEmpty()) {
            sql.append(" ORDER BY ").append(sortBy).append(" ").append("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");
        } else {
            sql.append(" ORDER BY name ASC");
        }
        sql.append(" LIMIT ? OFFSET ?");
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            stmt.setInt(idx++, departementId);
            if (search != null && !search.isEmpty()) {
                stmt.setString(idx++, "%" + search + "%");
                stmt.setString(idx++, "%" + search + "%");
            }
            stmt.setInt(idx++, size);
            stmt.setInt(idx, (page - 1) * size);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) products.add(mapRowToProduct(rs));
            }
        }
        return products;
    }

    // Compte le nombre total (pour pagination)
    public int countByDepartementWithFilters(int departementId, String search) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM product WHERE departement_id = ? AND active = true");
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ?)");
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            stmt.setInt(idx++, departementId);
            if (search != null && !search.isEmpty()) {
                stmt.setString(idx++, "%" + search + "%");
                stmt.setString(idx++, "%" + search + "%");
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        }
    }

    // Suppression logique
    public boolean softDelete(int id) throws SQLException {
        String sql = "UPDATE product SET active = false, updated_at = NOW() WHERE id = ? AND active = true";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }
}

