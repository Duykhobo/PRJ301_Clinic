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
    // 🔑 2. CÁC NGHỆP VỤ DAO DỊCH VỤ
    // =========================================================================

    /**
     * Lấy danh sách tất cả các Dịch vụ đang Hoạt động (status = 1) để Bệnh nhân chọn Đặt lịch.
     * Sắp xếp theo Tên Dịch vụ tăng dần (A-Z).
     *
     * @return Danh sách các Dịch vụ Active
     */
    public List<Service> findAllActive() {
        String sql = "SELECT * FROM Services WHERE status = 1 ORDER BY service_name ASC";
        return queryList(sql, this::mapResultSetToService);
    }

    /**
     * Tìm thông tin Dịch vụ theo ID.
     *
     * @param id Mã Dịch vụ
     * @return Đối tượng Service hoặc null nếu không tìm thấy
     */
    public Service findById(int id) {
        String sql = "SELECT * FROM Services WHERE id = ?";
        return queryOne(sql, this::mapResultSetToService, id);
    }

    /**
     * Thêm Dịch vụ mới vào CSDL (Dành cho Admin).
     *
     * @param service Đối tượng Service chứa thông tin thêm mới
     * @return true nếu thêm thành công
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
     * Cập nhật thông tin chi tiết Dịch vụ (Dành cho Admin).
     *
     * @param service Đối tượng Service chứa thông tin cần cập nhật
     * @return true nếu cập nhật thành công
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
     * Cập nhật trạng thái Hiển thị / Ẩn Dịch vụ (Dành cho Admin).
     *
     * @param id     Mã Dịch vụ
     * @param status true: Hiển thị (Active), false: Ẩn (Hidden)
     * @return true nếu cập nhật thành công
     */
    public boolean updateStatus(int id, boolean status) {
        String sql = "UPDATE Services SET status = ? WHERE id = ?";
        return executeUpdate(sql, status, id);
    }
}
