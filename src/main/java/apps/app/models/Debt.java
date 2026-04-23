package apps.app.models;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.sql.Date;

public class Debt {

    private int id;
    private String debtorType; // "client" ou "employee"
    private String debtorName; // pour client
    private Integer employeeId; // si debtorType = "employee"
    private double amount;
    private int saleItemId;
    private Date dueDate;
    private String status; // "pending" ou "paid"
    private Date paidAt;
    private Double paidAmount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Debt(){

    }

    // Getters
    public int getId() { return id; }
    public String getDebtorType() { return debtorType; }
    public String getDebtorName() { return debtorName; }
    public Integer getEmployeeId() { return employeeId; }
    public double getAmount() { return amount; }
    public int getSaleItemId() { return saleItemId; }
    public Date getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public Date getPaidAt() { return paidAt; }
    public Double getPaidAmount() { return paidAmount; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    //Les setters
    public void setId(int id) { this.id = id; }
    public void setDebtorType(String debtorType) { this.debtorType = debtorType; }
    public void setDebtorName(String debtorName) { this.debtorName = debtorName; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setSaleItemId(int saleItemId) { this.saleItemId = saleItemId; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public void setStatus(String status) { this.status = status; }
    public void setPaidAt(Date paidAt) { this.paidAt = paidAt; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}

