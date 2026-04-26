package apps.app.dao; // adaptez à votre package

import apps.app.models.Debt;
import apps.app.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Array.setInt;

public class DebtDAO {

    // create
    public void create(Debt debt){
        String sql = "INSERT INTO debts (debtor_type, debtor_name, employee_id, amount, sale_item_id, due_date, status, paid_at, paid_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, debt.getDebtorType());
            stmt.setString(2, debt.getDebtorName());
            if (debt.getEmployeeId() != null) {
                stmt.setInt(3, debt.getEmployeeId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setDouble(4, debt.getAmount());
            stmt.setInt(5, debt.getSaleItemId());
            stmt.setDate(6, debt.getDueDate());
            stmt.setString(7, debt.getStatus());
            stmt.setDate(8, debt.getPaidAt());
            if (debt.getPaidAmount() != null) {
                stmt.setDouble(9, debt.getPaidAmount());
            } else {
                stmt.setNull(9, Types.DOUBLE);
            }
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                debt.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // findById
    public Debt findById(int id) {
        String sql = "SELECT * FROM debts WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToDebt(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // findAll
    public List<Debt> findAll() {
        List<Debt> list = new ArrayList<>();
        String sql = "SELECT * FROM debts ORDER BY due_date ASC";
        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToDebt(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // findPending
    public List<Debt> findPending(){
        List<Debt> list = new ArrayList<>();
        String sql = "SELECT * FROM debts WHERE status = 'pending' ORDER BY due_date ASC";
        try {
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToDebt(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // findBySaleItem
    public List<Debt> findBySaleItem(int saleItemId){
        List<Debt> list = new ArrayList<>();
        String sql = "SELECT * FROM debts WHERE sale_item_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, saleItemId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToDebt(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // update
    public void update(Debt debt) throws SQLException {
        String sql = "UPDATE debts SET debtor_type = ?, debtor_name = ?, employee_id = ?, amount = ?, sale_item_id = ?, " +
                "due_date = ?, status = ?, paid_at = ?, paid_amount = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, debt.getDebtorType());
            stmt.setString(2, debt.getDebtorName());
            if (debt.getEmployeeId() != null) {
                stmt.setInt(3, debt.getEmployeeId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setDouble(4, debt.getAmount());
            stmt.setInt(5, debt.getSaleItemId());
            stmt.setDate(6, debt.getDueDate());
            stmt.setString(7, debt.getStatus());
            stmt.setDate(8, debt.getPaidAt());
            if (debt.getPaidAmount() != null) {
                stmt.setDouble(9, debt.getPaidAmount());
            } else {
                stmt.setNull(9, Types.DOUBLE);
            }
            stmt.setInt(10, debt.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // delete
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM debts WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Aucune dette trouvée avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // mapper les donnees depuis la base des donnees
    private Debt mapRowToDebt(ResultSet rs) {
        try {
            Debt debt = new Debt();
            debt.setId(rs.getInt("id"));
            debt.setDebtorType(rs.getString("debtor_type"));
            debt.setDebtorName(rs.getString("debtor_name"));
            int empId = rs.getInt("employee_id");
            if (rs.wasNull()) {
                debt.setEmployeeId(null);
            } else {
                debt.setEmployeeId(empId);
            }
            debt.setAmount(rs.getDouble("amount"));
            debt.setSaleItemId(rs.getInt("sale_item_id"));
            debt.setDueDate(rs.getDate("due_date"));
            debt.setStatus(rs.getString("status"));
            debt.setPaidAt(rs.getDate("paid_at"));
            double paidAmt = rs.getDouble("paid_amount");
            if (rs.wasNull()) {
                debt.setPaidAmount(null);
            } else {
                debt.setPaidAmount(paidAmt);
            }
            debt.setCreatedAt(rs.getTimestamp("created_at"));
            debt.setUpdatedAt(rs.getTimestamp("updated_at"));
            return debt;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //chercher par critere
    public List<Debt> findByCriteria(String debtorType, String debtorName, String status, Integer saleItemId, Date fromDueDate, Date toDueDate) throws SQLException {
        List<Debt> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM debts WHERE 1=1"); //utile pour ajouter d'autres conditions
        List<Object> params = new ArrayList<>();

        if (debtorType != null && !debtorType.isEmpty()) {
            sql.append(" AND debtor_type = ?");
            params.add(debtorType);
        }
        if (debtorName != null && !debtorName.isEmpty()) {
            sql.append(" AND debtor_name LIKE ?");
            params.add("%" + debtorName + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (saleItemId != null) {
            sql.append(" AND sale_item_id = ?");
            params.add(saleItemId);
        }
        if (fromDueDate != null) {
            sql.append(" AND due_date >= ?");
            params.add(fromDueDate);
        }
        if (toDueDate != null) {
            sql.append(" AND due_date <= ?");
            params.add(toDueDate);
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToDebt(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //finc by departement
    public List<Debt> findByDepartement(int departementId) throws SQLException {
        List<Debt> list = new ArrayList<>();
        String sql = "SELECT d.* FROM debts d " +
                "JOIN sale_items si ON d.sale_item_id = si.id " +
                "JOIN sales s ON si.sale_id = s.id " +
                "WHERE s.departement_id = ? " +
                "ORDER BY d.due_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departementId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToDebt(rs));
                }
            }
        }
        return list;
    }


    // Dans DebtDAO
    public List<Debt> findByDepartmentAndStatus(int departmentId, String status) throws SQLException {
        List<Debt> list = new ArrayList<>();
        String sql = "SELECT d.* FROM debts d " +
                "JOIN sale_items si ON d.sale_item_id = si.id " +
                "JOIN sales s ON si.sale_id = s.id " +
                "WHERE s.department_id = ? AND d.status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            stmt.setString(2, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToDebt(rs));
            }
        }
        return list;
    }

    //find by status
   public void findByStatus(String status){
        List<Debt> list = new ArrayList<>();
        String sql = "SELECT * FROM debts WHERE status = ? ORDER BY due_date ASC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToDebt(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
   }

   //
   public double getTotalPendingDebts() throws SQLException {
       String sql = "SELECT COALESCE(SUM(amount), 0) FROM debts WHERE status = 'pending'";
       try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
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