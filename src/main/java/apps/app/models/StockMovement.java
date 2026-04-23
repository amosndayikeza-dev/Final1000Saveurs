package apps.app.models;

import java.sql.Timestamp;

public class StockMovement {

    //Les attributs(encapsulation)
    private int id;
    private int productId;
    private int quantity;
    private String type; // "in" ou "out"
    private String reason; // "sale", "purchase", "adjustment", "loss"
    private Integer referenceId; // peut être sale_id ou autre
    private int createdBy; // user_id
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public StockMovement() {

    }


    // Getters
    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getType() { return type; }
    public String getReason() { return reason; }
    public Integer getReferenceId() { return referenceId; }
    public int getCreatedBy() { return createdBy; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    //Les setters
    public void setId(int id) { this.id = id; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setType(String type) { this.type = type; }
    public void setReason(String reason) { this.reason = reason; }
    public void setReferenceId(Integer referenceId) { this.referenceId = referenceId; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }


}
