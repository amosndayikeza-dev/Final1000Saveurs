package apps.app.dao;

import apps.app.models.SalaryReport;
import apps.app.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryReportDAO {

    //create
    public void create(SalaryReport report) {
        String sql = "INSERT INTO salary_reports (departement_id, manager_id, month, year, total_salary, status, submitted_at, approved_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, report.getDepartementId());
            stmt.setInt(2, report.getManagerId());
            stmt.setInt(3, report.getMonth());
            stmt.setInt(4, report.getYear());
            stmt.setDouble(5, report.getTotalSalary());
            stmt.setString(6, report.getStatus());
            stmt.setTimestamp(7, report.getSubmittedAt());
            stmt.setTimestamp(8, report.getApprovedAt());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                report.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //find by ID
    public SalaryReport findById(int id) {
        String sql = "SELECT * FROM salary_reports WHERE id = ?";
        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToSalaryReport(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //FIND All
    public List<SalaryReport> findAll(){
        List<SalaryReport> list = new ArrayList<>();
        String sql = "SELECT * FROM salary_reports ORDER BY submitted_at DESC";
        try {
             Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToSalaryReport(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //find by departement
    public List<SalaryReport> findByDepartement(int departementId){
        List<SalaryReport> list = new ArrayList<>();
        String sql = "SELECT * FROM salary_reports WHERE departement_id = ? ORDER BY year DESC, month DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departementId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToSalaryReport(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;

    }


    //find by status
    public List<SalaryReport> findByStatus(String status){
        List<SalaryReport> list = new ArrayList<>();
        String sql = "SELECT * FROM salary_reports WHERE status = ? ORDER BY submitted_at DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToSalaryReport(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //update
    public void update(SalaryReport report) {
        String sql = "UPDATE salary_reports SET departement_id = ?, manager_id = ?, month = ?, year = ?, " +
                "total_salary = ?, status = ?, submitted_at = ?, approved_at = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, report.getDepartementId());
            stmt.setInt(2, report.getManagerId());
            stmt.setInt(3, report.getMonth());
            stmt.setInt(4, report.getYear());
            stmt.setDouble(5, report.getTotalSalary());
            stmt.setString(6, report.getStatus());
            stmt.setTimestamp(7, report.getSubmittedAt());
            stmt.setTimestamp(8, report.getApprovedAt());
            stmt.setInt(9, report.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //delete
    public void delete(int id) {
        String sql = "DELETE FROM salary_reports WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Aucun rapport de salaire trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //mapper les donnees

    // Helper : mapping d'un ResultSet en objet SalaryReport
    private SalaryReport mapRowToSalaryReport(ResultSet rs) {
        try {
            SalaryReport report = new SalaryReport();
            report.setId(rs.getInt("id"));
            report.setDepartmentId(rs.getInt("departement_id"));
            report.setManagerId(rs.getInt("manager_id"));
            report.setMonth(rs.getInt("month"));
            report.setYear(rs.getInt("year"));
            report.setTotalSalary(rs.getDouble("total_salary"));
            report.setStatus(rs.getString("status"));
            report.setSubmittedAt(rs.getTimestamp("submitted_at"));
            report.setApprovedAt(rs.getTimestamp("approved_at"));
            report.setCreatedAt(rs.getTimestamp("created_at"));
            report.setUpdatedAt(rs.getTimestamp("updated_at"));
            return report;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //chercher par critere
    public List<SalaryReport> findByCriteria(Integer departmentId, Integer managerId, Integer month, Integer year, String status) throws SQLException {
        List<SalaryReport> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM salary_reports WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (departmentId != null) {
            sql.append(" AND departement_id = ?");
            params.add(departmentId);
        }
        if (managerId != null) {
            sql.append(" AND manager_id = ?");
            params.add(managerId);
        }
        if (month != null && month >= 1 && month <= 12) {
            sql.append(" AND month = ?");
            params.add(month);
        }
        if (year != null) {
            sql.append(" AND year = ?");
            params.add(year);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToSalaryReport(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //verifier l'existence par periode
    public boolean existsByDepartementAndMonthYear(int departementId, int month, int year) {
        String sql = "SELECT COUNT(*) FROM salary_reports WHERE departement_id = ? AND month = ? AND year = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, departementId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}

