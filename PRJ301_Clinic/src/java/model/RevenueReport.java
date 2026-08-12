package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class RevenueReport implements Serializable {

    private int totalAppointments;
    private int completedAppointments;
    private int cancelledAppointments;
    private BigDecimal totalRevenuePaid;
    private BigDecimal sepayRevenue;
    private BigDecimal cashRevenue;

    public RevenueReport() {
        this.totalAppointments = 0;
        this.completedAppointments = 0;
        this.cancelledAppointments = 0;
        this.totalRevenuePaid = BigDecimal.ZERO;
        this.sepayRevenue = BigDecimal.ZERO;
        this.cashRevenue = BigDecimal.ZERO;
    }

    public RevenueReport(int totalAppointments, int completedAppointments, int cancelledAppointments,
                         BigDecimal totalRevenuePaid, BigDecimal sepayRevenue, BigDecimal cashRevenue) {
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.totalRevenuePaid = totalRevenuePaid != null ? totalRevenuePaid : BigDecimal.ZERO;
        this.sepayRevenue = sepayRevenue != null ? sepayRevenue : BigDecimal.ZERO;
        this.cashRevenue = cashRevenue != null ? cashRevenue : BigDecimal.ZERO;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public int getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(int completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public int getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(int cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public BigDecimal getTotalRevenuePaid() {
        return totalRevenuePaid;
    }

    public void setTotalRevenuePaid(BigDecimal totalRevenuePaid) {
        this.totalRevenuePaid = totalRevenuePaid;
    }

    public BigDecimal getSepayRevenue() {
        return sepayRevenue;
    }

    public void setSepayRevenue(BigDecimal sepayRevenue) {
        this.sepayRevenue = sepayRevenue;
    }

    public BigDecimal getCashRevenue() {
        return cashRevenue;
    }

    public void setCashRevenue(BigDecimal cashRevenue) {
        this.cashRevenue = cashRevenue;
    }
}
