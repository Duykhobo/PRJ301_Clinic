package model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model User - Ánh xạ bảng Users trong CSDL SQL Server.
 */
public class User implements Serializable {

    private int id;
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String phone;
    private String role; // ADMIN, DOCTOR, PATIENT, RECEPTIONIST
    private boolean status; // true: Active, false: Inactive/Banned
    private Timestamp createdAt;

    public User() {
    }

    public User(int id, String username, String password, String email, String fullname, String phone, String role, boolean status, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
