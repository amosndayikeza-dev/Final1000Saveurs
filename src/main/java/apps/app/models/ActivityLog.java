package apps.app.models;

import java.sql.Timestamp;

public class ActivityLog {

        private int id;
        private int userId;
        private String action;
        private String details;
        private String ipAddress;
        private Timestamp createdAt;

        // Constructeur par défaut
        public ActivityLog() {}

        // Getters et Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }

        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }

