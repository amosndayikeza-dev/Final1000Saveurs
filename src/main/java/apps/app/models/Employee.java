package apps.app.models;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.sql.Date;


public class Employee {

    //Les attributs
        private int id;
        private int userId;        // référence vers User
        private int departementId;  // référence vers Department
        private String position;
        private double salary;
        private String hiredAt;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        //Le controlleur
        public Employee(){

        }

        // Getters

        public int getId() { return id; }
        public int getUserId() { return userId; }
        public int getDepartementId() { return departementId; }
        public String getPosition() { return position; }
        public double getSalary() { return salary; }
        public String getHiredAt() { return hiredAt; }
        public Timestamp getCreatedAt() { return createdAt; }
        public Timestamp getUpdatedAt() { return updatedAt; }

        //Setters
        public void setId(int id) { this.id = id; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setDepartementId(int departementId) { this.departementId = departementId; }
        public void setPosition(String position) { this.position = position; }
        public void setSalary(double salary) { this.salary = salary; }
        public void setHiredAt(String hiredAt) { this.hiredAt = hiredAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

}
