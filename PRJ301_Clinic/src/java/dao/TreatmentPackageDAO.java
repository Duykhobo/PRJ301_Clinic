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
 * TreatmentPackageDAO - Quản lý Gói Liệu Trình Spa / Điều Trị Da Liễu từ bảng TreatmentPackages.
 */
public class TreatmentPackageDAO extends BaseDAO<TreatmentPackage> {

    private static final Logger LOGGER = Logger.getLogger(TreatmentPackageDAO.class.getName());

    protected TreatmentPackage mapResultSetToPackage(ResultSet rs) throws SQLException {
        TreatmentPackage pkg = new TreatmentPackage();
        pkg.setId(rs.getInt("id"));
        pkg.setPatientId(rs.getInt("patient_id"));
        pkg.setServiceId(rs.getInt("service_id"));
        pkg.setPackageName(rs.getString("package_name"));
        pkg.setServiceName(rs.getString("service_name"));
        try {
            pkg.setPatientName(rs.getString("patient_name"));
            pkg.setPatientPhone(rs.getString("patient_phone"));
        } catch (Exception ignored) {
        }
        pkg.setTotalSessions(rs.getInt("total_sessions"));
        pkg.setCompletedSessions(rs.getInt("completed_sessions"));
        try {
            pkg.setPrice(rs.getBigDecimal("price"));
        } catch (Exception ignored) {
        }
        pkg.setStatus(rs.getString("status"));
        return pkg;
    }

    /**
     * Lấy danh sách Gói Liệu Trình của một Bệnh nhân từ bảng TreatmentPackages (ưu tiên) hoặc tổng hợp từ Appointments.
     */
    public List<TreatmentPackage> findActivePackagesByPatient(int patientId) {
        String sql = "SELECT tp.id, tp.patient_id, tp.service_id, tp.package_name, tp.total_sessions, tp.completed_sessions, tp.status, "
                   + "       s.service_name, s.price, u.fullname AS patient_name, u.phone AS patient_phone "
                   + "FROM TreatmentPackages tp "
                   + "JOIN Services s ON tp.service_id = s.id "
                   + "JOIN Users u ON tp.patient_id = u.id "
                   + "WHERE tp.patient_id = ? "
                   + "ORDER BY tp.id DESC";

        List<TreatmentPackage> list = queryList(sql, this::mapResultSetToPackage, patientId);
        if (list != null && !list.isEmpty()) {
            return list;
        }

        // Fallback tự động tổng hợp từ lịch sử Appointments nếu chưa có bản ghi trong bảng TreatmentPackages
        return findAggregatedPackagesByPatient(patientId);
    }

    /**
     * Lấy tất cả Gói Liệu Trình của toàn bộ bệnh nhân (Dành cho Bác sĩ & Admin theo dõi).
     */
    public List<TreatmentPackage> findAllPackages() {
        String sql = "SELECT tp.id, tp.patient_id, tp.service_id, tp.package_name, tp.total_sessions, tp.completed_sessions, tp.status, "
                   + "       s.service_name, s.price, u.fullname AS patient_name, u.phone AS patient_phone "
                   + "FROM TreatmentPackages tp "
                   + "JOIN Services s ON tp.service_id = s.id "
                   + "JOIN Users u ON tp.patient_id = u.id "
                   + "ORDER BY CASE WHEN tp.status = 'ACTIVE' THEN 0 ELSE 1 END, tp.id DESC";

        return queryList(sql, this::mapResultSetToPackage);
    }

    /**
     * Tạo mới một Gói Liệu Trình (5 hoặc 10 buổi) cho Bệnh nhân.
     */
    public boolean createPackage(int patientId, int serviceId, String packageName, int totalSessions) {
        String sql = "INSERT INTO TreatmentPackages (patient_id, service_id, package_name, total_sessions, completed_sessions, status) "
                   + "VALUES (?, ?, ?, ?, 0, 'ACTIVE')";
        return executeUpdate(sql, patientId, serviceId, packageName, totalSessions > 0 ? totalSessions : 5);
    }

    /**
     * Bác sĩ ghi nhận hoàn thành +1 buổi cho gói liệu trình.
     */
    public boolean incrementCompletedSession(int packageId) {
        String selectSql = "SELECT * FROM TreatmentPackages WHERE id = ?";
        TreatmentPackage pkg = queryOne(selectSql, rs -> {
            TreatmentPackage p = new TreatmentPackage();
            p.setId(rs.getInt("id"));
            p.setTotalSessions(rs.getInt("total_sessions"));
            p.setCompletedSessions(rs.getInt("completed_sessions"));
            return p;
        }, packageId);

        if (pkg != null) {
            int newCompleted = pkg.getCompletedSessions() + 1;
            String newStatus = newCompleted >= pkg.getTotalSessions() ? "COMPLETED" : "ACTIVE";
            String updateSql = "UPDATE TreatmentPackages SET completed_sessions = ?, status = ?, updated_at = GETDATE() WHERE id = ?";
            return executeUpdate(updateSql, newCompleted, newStatus, packageId);
        }
        return false;
    }

    /**
     * Helper tìm hoặc tạo gói liệu trình khi Bác sĩ khám ca thuộc gói.
     */
    public boolean advanceOrCreatePackageForAppointment(int patientId, int serviceId, String serviceName, int totalSessions) {
        String findSql = "SELECT TOP 1 id FROM TreatmentPackages WHERE patient_id = ? AND service_id = ? AND status = 'ACTIVE' ORDER BY id DESC";
        Integer existingPkgId = queryOne(findSql, rs -> rs.getInt("id"), patientId, serviceId);

        if (existingPkgId != null && existingPkgId > 0) {
            return incrementCompletedSession(existingPkgId);
        } else {
            String pkgName = "Liệu Trình " + (serviceName != null ? serviceName : "Trị Liệu") + " (" + totalSessions + " Buổi)";
            String insertSql = "INSERT INTO TreatmentPackages (patient_id, service_id, package_name, total_sessions, completed_sessions, status) "
                             + "VALUES (?, ?, ?, ?, 1, ?)";
            String status = totalSessions <= 1 ? "COMPLETED" : "ACTIVE";
            return executeUpdate(insertSql, patientId, serviceId, pkgName, totalSessions, status);
        }
    }

    /**
     * Fallback tổng hợp từ lịch hẹn Appointments.
     */
    private List<TreatmentPackage> findAggregatedPackagesByPatient(int patientId) {
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
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn aggregated TreatmentPackage cho patientId: " + patientId, e);
        }
        return list;
    }
}
