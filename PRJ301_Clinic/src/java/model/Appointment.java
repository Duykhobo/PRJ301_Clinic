package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Model Appointment - Ánh xạ bảng Appointments trong CSDL SQL Server.
 */
public class Appointment implements Serializable {

    private int id;
    private int patientId;
    private int doctorId;
    private int serviceId;
    private int scheduleId;
    private Date appointmentDate;
    private Time startTime;
    private BigDecimal totalPrice;
    private String status;         // PENDING, CONFIRMED, COMPLETED, CANCELLED
    private String paymentStatus;  // UNPAID, PAID
    private String paymentMethod;  // CASH, SEPAY_QR
    private String paymentContent; // CLINIC<id>
    private String transactionCode;
    private String notes;
    private Timestamp createdAt;

    // Joined Fields
    private String patientName;
    private String patientPhone;
    private String doctorName;
    private String serviceName;

    public Appointment() {
    }

    public Appointment(int id, int patientId, int doctorId, int serviceId, int scheduleId, Date appointmentDate, Time startTime, BigDecimal totalPrice, String status, String paymentStatus, String paymentMethod, String paymentContent, String transactionCode, String notes, Timestamp createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.serviceId = serviceId;
        this.scheduleId = scheduleId;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paymentContent = paymentContent;
        this.transactionCode = transactionCode;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentContent() {
        return paymentContent;
    }

    public void setPaymentContent(String paymentContent) {
        this.paymentContent = paymentContent;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
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
