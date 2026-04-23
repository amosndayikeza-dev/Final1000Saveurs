package apps.app.models;

import java.sql.Timestamp;
import java.sql.Date;

public class SaleItem {

    //Les attributs
    private int id;
    private int saleId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private boolean isPaid;
    private String clientName; // pour dette client
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public SaleItem(){

    }


    // Getters et Setters
    public int getId() { return id; }
    public int getSaleId() { return saleId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public boolean isPaid() { return isPaid; }
    public String getClientName() { return clientName; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    //Les setters
    public void setId(int id) { this.id = id; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setPaid(boolean paid) { isPaid = paid; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}

