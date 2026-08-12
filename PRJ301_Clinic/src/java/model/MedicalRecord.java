package model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model MedicalRecord - Ánh xạ bảng MedicalRecords trong CSDL SQL Server.
 */
public class MedicalRecord implements Serializable {

    private int id;
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private String diagnosis;
    private String prescriptionOrResult;
    private Integer rating; // 1 - 5 stars
    private String reviewComment;
    private Timestamp createdAt;

    // Joined Fields
    private String patientName;
    private String doctorName;
    private String serviceName;

    public MedicalRecord() {
    }

    public MedicalRecord(int id, int appointmentId, int patientId, int doctorId, String diagnosis, String prescriptionOrResult, Integer rating, String reviewComment, Timestamp createdAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.prescriptionOrResult = prescriptionOrResult;
        this.rating = rating;
        this.reviewComment = reviewComment;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescriptionOrResult() {
        return prescriptionOrResult;
    }

    public void setPrescriptionOrResult(String prescriptionOrResult) {
        this.prescriptionOrResult = prescriptionOrResult;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
