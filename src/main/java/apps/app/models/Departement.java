package apps.app.models;

import java.sql.Timestamp;

public class Departement {


        private int id;
        private String name;
        private String address;
        private String description;
        private int managerId;  // référence vers User (gérant)
        private Timestamp createdAt;
        private Timestamp updatedAt;

        //constructeur
        public Departement(){

        }

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getAddress() { return address; }
        public String getDescription() { return description; }
        public int getManagerId() { return managerId; }
        public Timestamp getCreatedAt() { return createdAt; }
        public Timestamp getUpdatedAt() { return updatedAt; }

        //Les setters
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setAddress(String address) { this.address = address; }
        public void setDescription(String description) { this.description = description; }
        public void setManagerId(int managerId) { this.managerId = managerId; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}



