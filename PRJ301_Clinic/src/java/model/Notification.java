package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Model Notification - Ánh xạ bảng Notifications trong CSDL SQL Server.
 */
public class Notification implements Serializable {

    private int id;
    private int userId;
    private String title;
    private String message;
    private String type; // 'APPOINTMENT', 'SCHEDULE', 'PAYMENT', 'MEDICAL', 'SYSTEM'
    private boolean isRead;
    private String link;
    private Timestamp createdAt;

    public Notification() {
    }

    public Notification(int id, int userId, String title, String message, String type, boolean isRead, String link, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type != null ? type : "INFO";
        this.isRead = isRead;
        this.link = link;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsRead() {
        return isRead;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Tính thời gian tương đối bằng tiếng Việt (Vừa xong, 5 phút trước, 2 giờ trước...)
     */
    public String getTimeAgo() {
        if (createdAt == null) {
            return "Vừa xong";
        }
        try {
            LocalDateTime notiTime = createdAt.toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(notiTime, now);

            long seconds = duration.getSeconds();
            if (seconds < 60) {
                return "Vừa xong";
            }
            long minutes = duration.toMinutes();
            if (minutes < 60) {
                return minutes + " phút trước";
            }
            long hours = duration.toHours();
            if (hours < 24) {
                return hours + " giờ trước";
            }
            long days = duration.toDays();
            if (days < 30) {
                return days + " ngày trước";
            }
            return (days / 30) + " tháng trước";
        } catch (Exception e) {
            return "Gần đây";
        }
    }

    @Override
    public String toString() {
        return "Notification{" + "id=" + id + ", userId=" + userId + ", title=" + title + ", type=" + type + ", isRead=" + isRead + '}';
    }
}