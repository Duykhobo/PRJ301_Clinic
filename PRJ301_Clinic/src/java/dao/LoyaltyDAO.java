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

        LoyaltyProfile profile = queryOne(sql, rs -> {
            BigDecimal totalSpent = rs.getBigDecimal("total_spent");
            return new LoyaltyProfile(patientId, totalSpent != null ? totalSpent : BigDecimal.ZERO);
        }, patientId);

        return profile != null ? profile : new LoyaltyProfile(patientId, BigDecimal.ZERO);
    }
}
