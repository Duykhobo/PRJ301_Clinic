package model;

import java.math.BigDecimal;

/**
 * Model TreatmentPackage - Đại diện cho Gói Liệu Trình Spa / Điều Trị Da Liễu nhiều buổi.
 */
public class TreatmentPackage {

    private int id;
    private int patientId;
    private int serviceId;
    private String patientName;
    private String patientPhone;
    private String packageName;
    private String serviceName;
    private int totalSessions;
    private int completedSessions;
    private int remainingSessions;
    private int progressPercent;
    private BigDecimal price;
    private String status;

    public TreatmentPackage() {
    }

    public TreatmentPackage(int id, int patientId, String packageName, String serviceName, int totalSessions, int completedSessions, BigDecimal price, String status) {
        this.id = id;
        this.patientId = patientId;
        this.packageName = packageName;
        this.serviceName = serviceName;
        this.totalSessions = totalSessions > 0 ? totalSessions : 1;
        this.completedSessions = completedSessions >= 0 ? completedSessions : 0;
        this.remainingSessions = Math.max(0, this.totalSessions - this.completedSessions);
        this.progressPercent = (int) Math.min(100, Math.round(((double) this.completedSessions / this.totalSessions) * 100));
        this.price = price != null ? price : BigDecimal.ZERO;
        this.status = status != null ? status : "Đang Điều Trị";
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

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions > 0 ? totalSessions : 1;
        recalculateProgress();
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(int completedSessions) {
        this.completedSessions = completedSessions >= 0 ? completedSessions : 0;
        recalculateProgress();
    }

    public int getRemainingSessions() {
        return remainingSessions;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private void recalculateProgress() {
        this.remainingSessions = Math.max(0, this.totalSessions - this.completedSessions);
        this.progressPercent = (int) Math.min(100, Math.round(((double) this.completedSessions / this.totalSessions) * 100));
    }
}
