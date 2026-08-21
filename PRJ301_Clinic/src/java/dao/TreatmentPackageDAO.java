package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.DBContext;
import model.TreatmentPackage;

/**
 * TreatmentPackageDAO - Truy vấn và tổng hợp các Gói Liệu Trình Spa / Da liễu thực tế của bệnh nhân từ CSDL.
 */
public class TreatmentPackageDAO extends BaseDAO<TreatmentPackage> {

    private static final Logger LOGGER = Logger.getLogger(TreatmentPackageDAO.class.getName());

    /**
     * Lấy danh sách Gói Liệu Trình của Bệnh nhân tổng hợp từ các ca khám/chăm sóc thật trong CSDL.
     * Nếu bệnh nhân chưa có ca khám nào, trả về danh sách rỗng (Không render khung giả).
     */
    public List<TreatmentPackage> findActivePackagesByPatient(int patientId) {
        List<TreatmentPackage> list = new ArrayList<>();
        String sql = "SELECT s.id AS service_id, s.service_name, "
                   + "       COUNT(CASE WHEN a.status = 'COMPLETED' THEN 1 END) AS completed_count, "
                   + "       COUNT(a.id) AS total_booked, "
                   + "       COALESCE(SUM(a.total_price), 0) AS total_val "
                   + "FROM Appointments a "
                   + "JOIN Services s ON a.service_id = s.id "
                   + "WHERE a.patient_id = ? AND a.status <> 'CANCELLED' "
                   + "GROUP BY s.id, s.service_name "
                   + "HAVING COUNT(a.id) > 0";

        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    int pkgIndex = 1;
                    while (rs.next()) {
                        String serviceName = rs.getString("service_name");
                        int completedCount = rs.getInt("completed_count");
                        int totalBooked = rs.getInt("total_booked");
                        BigDecimal totalVal = rs.getBigDecimal("total_val");

                        int totalSessions = Math.max(5, totalBooked >= 5 ? 10 : 5);
                        String packageName = "Liệu Trình " + serviceName + " (" + totalSessions + " Buổi)";
                        String status = completedCount >= totalSessions ? "Đã Hoàn Thành" : (completedCount > 0 ? "Đang Điều Trị" : "Mới Khởi Tạo");

                        TreatmentPackage pkg = new TreatmentPackage(
                                pkgIndex++,
                                patientId,
                                packageName,
                                serviceName,
                                totalSessions,
                                completedCount,
                                totalVal,
                                status
                        );
                        list.add(pkg);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn TreatmentPackage cho patientId: " + patientId, e);
        }

        return list;
    }
}
