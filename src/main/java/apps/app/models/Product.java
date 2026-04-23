package apps.app.models;

import java.sql.Timestamp;

public class Product {

        //Les attributs
        private int id;
        private int departementId;
        private String name;
        private String description;
        private double unitPrice;
        private int currentStock;
        private int lowStockThreshold;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        public Product(){

        }

        // Getters et Setters
    public int getId() { return id; }
    public int getDepartementId() { return departementId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getUnitPrice() { return unitPrice; }
    public int getCurrentStock() { return currentStock; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    //Les setters
    public void setId(int id) { this.id = id; }
    public void setDepartementId(int departementId) { this.departementId = departementId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }


}
