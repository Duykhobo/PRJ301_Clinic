package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.Service;

/**
 * Lớp ServiceDAO quản lý thao tác CSDL cho Bảng Services (Danh mục Dịch vụ Khám
 * & Spa).
 * Kế thừa BaseDAO<Service> áp dụng nguyên tắc DRY (Don't Repeat Yourself).
 */
public class ServiceDAO extends BaseDAO<Service> {

    // =========================================================================
    // 🧱 1. HELPER MAPPER (CHUẨN DRY)
    // =========================================================================
    /**
     * Helper Mapper chuyển 1 dòng ResultSet từ SQL Server thành đối tượng Service.
     */
    protected Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getInt("id"));
        service.setServiceName(rs.getString("service_name"));
        service.setPrice(rs.getBigDecimal("price"));
        service.setDurationMinutes(rs.getInt("duration_minutes"));
        service.setDescription(rs.getString("description"));
        service.setImageUrl(rs.getString("image_url"));
        service.setStatus(rs.getBoolean("status"));
        return service;
    }

    // =========================================================================
    // 🔑 2. CÁC NGHỆP VỤ DAO DỊCH VỤ (TODO BẠN TỰ GÕ CODE THỰC HÀNH)
    // =========================================================================

    /**
     * TODO 1: Lấy danh sách tất cả dịch vụ đang Active (status = 1) cho Bệnh nhân
     * chọn đặt lịch
     * Gợi ý: SELECT * FROM Services WHERE status = 1 ORDER BY service_name ASC
     * Dùng: queryList(sql, this::mapResultSetToService)
     */
    public List<Service> findAllActive() {
        String sql = "SELECT * FROM Services WHERE status = 1 ORDER BY service_name ASC";
        return queryList(sql, this::mapResultSetToService);
    }

    /**
     * TODO 2: Tìm thông tin Dịch vụ theo ID
     * Gợi ý: SELECT * FROM Services WHERE id = ?
     * Dùng: queryOne(sql, this::mapResultSetToService, id)
     */
    public Service findById(int id) {
        String sql = "SELECT * FROM Services WHERE id = ?";
        return queryOne(sql, this::mapResultSetToService, id);
    }

    /**
     * TODO 3: Thêm Dịch vụ mới (Admin)
     * Gợi ý: INSERT INTO Services (service_name, price, duration_minutes,
     * description, image_url, status) VALUES (?, ?, ?, ?, ?, ?)
     * Dùng: executeUpdate(sql, service.getServiceName(), service.getPrice(),
     * service.getDurationMinutes(), service.getDescription(),
     * service.getImageUrl(), service.isStatus())
     */
    public boolean insert(Service service) {
        String sql = "INSERT INTO Services (service_name, price, duration_minutes, description, image_url, status) VALUES (?, ?, ?, ?, ?, ?)";

        return executeUpdate(sql,
                service.getServiceName(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.getDescription(),
                service.getImageUrl(),
                service.isStatus());
    }

    /**
     * TODO 4: Cập nhật thông tin Dịch vụ (Admin)
     * Gợi ý: UPDATE Services SET service_name = ?, price = ?, duration_minutes = ?,
     * description = ?, image_url = ?, status = ? WHERE id = ?
     * Dùng: executeUpdate(sql, ...)
     */
    public boolean update(Service service) {
        String sql = "UPDATE Services SET service_name = ?, price = ?, duration_minutes = ?, description = ?, image_url = ?, status = ? WHERE id = ?";

        return executeUpdate(sql,
                service.getServiceName(), service.getPrice(),
                service.getDurationMinutes(),
                service.getDescription(), service.getImageUrl(),
                service.isStatus(),
                service.getId());
    }

    /**
     * TODO 5: Cập nhật trạng thái Hiển thị / Ẩn Dịch vụ (Admin)
     * Gợi ý: UPDATE Services SET status = ? WHERE id = ?
     */
    public boolean updateStatus(int id, boolean status) {
        String sql = "UPDATE Services SET status = ? WHERE id = ?";
        return executeUpdate(sql,
                status,
                id);
    }
}
