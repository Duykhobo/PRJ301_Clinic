package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.DBContext;
import model.LoyaltyProfile;

/**
 * LoyaltyDAO - Truy vấn và tính toán điểm tích lũy & hạng hội viên từ dữ liệu hóa đơn thật trong CSDL.
 */
public class LoyaltyDAO extends BaseDAO<LoyaltyProfile> {

    private static final Logger LOGGER = Logger.getLogger(LoyaltyDAO.class.getName());

    /**
     * Lấy thông tin Hạng Hội viên & Điểm thưởng của Bệnh nhân tính từ các hóa đơn đã thanh toán (PAID).
     */
    public LoyaltyProfile getLoyaltyProfileByPatient(int patientId) {
        String sql = "SELECT COALESCE(SUM(total_price), 0) AS total_spent "
                   + "FROM Appointments "
                   + "WHERE patient_id = ? AND payment_status = 'PAID'";

        BigDecimal totalSpent = BigDecimal.ZERO;
        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal val = rs.getBigDecimal("total_spent");
                        if (val != null) {
                            totalSpent = val;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn LoyaltyProfile cho patientId: " + patientId, e);
        }

        return new LoyaltyProfile(patientId, totalSpent);
    }
}
