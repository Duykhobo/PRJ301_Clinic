package model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Model Service - Ánh xạ bảng Services trong CSDL SQL Server.
 */
public class Service implements Serializable {

    private int id;
    private String serviceName;
    private BigDecimal price;
    private int durationMinutes;
    private String description;
    private String imageUrl;
    private boolean status;

    public Service() {
    }

    public Service(int id, String serviceName, BigDecimal price, int durationMinutes, String description, String imageUrl, boolean status) {
        this.id = id;
        this.serviceName = serviceName;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
