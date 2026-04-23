package apps.app.models;

import java.sql.Timestamp;
import java.text.DateFormat;

public class SalaryReport {
    private int id;
    private int departmentId;
    private int managerId;
    private int month;
    private int year;
    private double totalSalary;
    private String status; // "pending" ou "approved"
    private Timestamp submittedAt;
    private Timestamp approvedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    //constructeur
    public SalaryReport(){

    }

    // Getters
    public int getId() { return id; }
    public int getDepartementId() { return departmentId; }
    public int getManagerId() { return managerId; }
    public int getMonth() { return month; }
    public double getTotalSalary() { return totalSalary; }
    public int getYear() { return year; }
    public String getStatus() { return status; }
    public Timestamp getSubmittedAt() { return submittedAt; }
    public Timestamp getApprovedAt() { return approvedAt; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }


    //Les setters
    public void setId(int id) { this.id = id; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public void setManagerId(int managerId) { this.managerId = managerId; }
    public void setMonth(int month) { this.month = month; }
    public void setYear(int year) { this.year = year; }
    public void setTotalSalary(double totalSalary) { this.totalSalary = totalSalary; }
    public void setStatus(String status) { this.status = status; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }
    public void setApprovedAt(Timestamp approvedAt) { this.approvedAt = approvedAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}