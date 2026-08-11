package models;

import java.io.Serializable;

/**
 * Model DoctorProfile - Ánh xạ bảng DoctorProfiles trong CSDL SQL Server.
 */
public class DoctorProfile implements Serializable {

    private int id;
    private int userId;
    private String specialty;
    private int experienceYears;
    private String roomNumber;
    private String bio;

    // Joined Fields
    private String doctorName;
    private String doctorPhone;
    private String doctorEmail;
    private double averageRating;

    public DoctorProfile() {
    }

    public DoctorProfile(int id, int userId, String specialty, int experienceYears, String roomNumber, String bio) {
        this.id = id;
        this.userId = userId;
        this.specialty = specialty;
        this.experienceYears = experienceYears;
        this.roomNumber = roomNumber;
        this.bio = bio;
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
