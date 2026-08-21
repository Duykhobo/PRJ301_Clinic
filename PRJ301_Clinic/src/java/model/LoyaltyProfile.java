package model;

import java.math.BigDecimal;

/**
 * Model LoyaltyProfile - Đại diện cho Hạng Hội Viên & Điểm Thưởng tích lũy thật của Bệnh Nhân.
 */
public class LoyaltyProfile {

    private int patientId;
    private String tierName;
    private String tierBadgeClass;
    private BigDecimal totalSpent;
    private int totalPoints;
    private int discountPercent;
    private String specialBenefit;

    public LoyaltyProfile() {
    }

    public LoyaltyProfile(int patientId, BigDecimal totalSpent) {
        this.patientId = patientId;
        this.totalSpent = totalSpent != null ? totalSpent : BigDecimal.ZERO;
        this.totalPoints = this.totalSpent.divideToIntegralValue(BigDecimal.valueOf(1000)).intValue();

        // Tính toán hạng thành viên thực tế dựa trên tổng tiền chi tiêu
        double spentVal = this.totalSpent.doubleValue();
        if (spentVal >= 2000000.0) {
            this.tierName = "Diamond VIP Member";
            this.tierBadgeClass = "bg-info bg-opacity-25 text-cyan border border-info border-opacity-40";
            this.discountPercent = 10;
            this.specialBenefit = "Giảm 10% tất cả dịch vụ & Miễn phí 1 lần soi da y khoa chuyên sâu";
        } else if (spentVal >= 500000.0) {
            this.tierName = "Gold VIP Member";
            this.tierBadgeClass = "bg-warning bg-opacity-25 text-warning border border-warning border-opacity-40";
            this.discountPercent = 5;
            this.specialBenefit = "Giảm 5% tất cả dịch vụ & Ưu tiên chọn Bác sĩ, Kỹ thuật viên";
        } else {
            this.tierName = "Silver Member";
            this.tierBadgeClass = "bg-secondary bg-opacity-25 text-white-50 border border-secondary border-opacity-40";
            this.discountPercent = 0;
            this.specialBenefit = "Tích lũy 1 điểm cho mỗi 1.000 VNĐ chi tiêu";
        }
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getTierName() {
        return tierName;
    }

    public String getTierBadgeClass() {
        return tierBadgeClass;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public String getSpecialBenefit() {
        return specialBenefit;
    }
}
