package apps.app.models;

import java.sql.Timestamp;

public  class Users {

    //les proprietes
    private  int id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Boolean isActive;
    private String role; //admin,patron,manager
    private Timestamp createdAt;
    private Timestamp updatedAt;

    //constructeur
    public Users(){

    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public boolean isActive() { return this.isActive; }
    public String getRole() { return role; }
    public Timestamp getCreatedAt() { return this.createdAt; }
    public Timestamp getUpdatedAt() { return this.updatedAt; }

    //Les setters

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setActive(boolean active) { isActive = active; }
    public void setRole(String role) { this.role = role; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

}
