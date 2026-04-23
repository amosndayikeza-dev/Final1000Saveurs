package apps.app.models;

import java.sql.Timestamp;
import java.text.DateFormat;

public class Notification {
    private int id;
    private int userId;
    private String type;
    private String message;
    private String link;
    private Timestamp readAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    //Contructeur
    public Notification(){

    }


    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getLink() { return link; }
    public Timestamp getReadAt() { return readAt; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    //Les setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setLink(String link) { this.link = link; }
    public void setReadAt(Timestamp readAt) { this.readAt = readAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}