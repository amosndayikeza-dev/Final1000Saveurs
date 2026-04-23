package apps.app.models;

import java.sql.Timestamp;
import java.sql.Date;

public class Sale {

        //Les attributs
        private int id;
        private int departementId;
        private Date soldAt;
        private double totalAmount;
        private int createdBy;   // référence User
        private String notes;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        public Sale(){

        }

        // Getters
        public int getId() { return id; }
        public int getDepartementId() { return departementId; }
        public Date getSoldAt() { return soldAt; }
        public double getTotalAmount() { return totalAmount; }
        public int getCreatedBy() { return createdBy; }
        public String getNotes() { return notes; }
        public Timestamp getCreatedAt() { return createdAt; }
        public Timestamp getUpdatedAt() { return updatedAt; }

        //Les setters
        public void setId(int id) { this.id = id; }
        public void setDepartementId(int departementId) { this.departementId = departementId; }
        public void setSoldAt(Date soldAt) { this.soldAt = soldAt; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
        public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
        public void setNotes(String notes) { this.notes = notes; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }


}
