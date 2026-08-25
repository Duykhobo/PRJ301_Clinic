package dao;

import java.util.ArrayList;
import java.util.List;
import model.Service;

/**
 * Lớp ServiceDAO quản lý thao tác CSDL cho Bảng Services (Danh mục Dịch vụ Khám & Spa).
 * Kế thừa BaseDAO<Service> áp dụng nguyên tắc SOLID và DRY (AutoMapper Reflection).
 */
public class ServiceDAO extends BaseDAO<Service> {

    // =========================================================================
    // 🧱 1. ROWMAPPER (TỰ ĐỘNG BẰNG REFLECTION CHUẨN DRY & SOLID)
    // =========================================================================
    private final RowMapper<Service> mapper = autoMapper(Service.class);

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
        return queryList(sql, mapper);
    }

    /**
     * Tìm thông tin Dịch vụ theo ID.
     *
     * @param id Mã Dịch vụ
     * @return Đối tượng Service hoặc null nếu không tìm thấy
     */
    public Service findById(int id) {
        String sql = "SELECT * FROM Services WHERE id = ?";
        return queryOne(sql, mapper, id);
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

    /**
     * Lấy toàn bộ danh sách Dịch vụ (bao gồm cả Dịch vụ bị Ẩn) cho Admin.
     */
    public List<Service> findAllForAdmin() {
        String sql = "SELECT * FROM Services ORDER BY id DESC";
        return queryList(sql, mapper);
    }

    /**
     * Đảo trạng thái Ẩn / Hiện Dịch vụ.
     */
    public boolean toggleStatus(int id) {
        Service s = findById(id);
        if (s == null) return false;
        String sql = "UPDATE Services SET status = ? WHERE id = ?";
        return executeUpdate(sql, !s.isStatus(), id);
    }

    /**
     * Lấy danh sách Dịch vụ có Phân Trang cho Admin (SQL Server OFFSET...FETCH NEXT).
     */
    public List<Service> findAllForAdminPaginated(int offset, int limit) {
        String sql = "SELECT * FROM Services ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, mapper, offset, limit);
    }

    /**
     * Đếm tổng số lượng Dịch vụ trong hệ thống.
     */
    public int countAllForAdmin() {
        return queryCount("SELECT COUNT(*) FROM Services");
    }

    /**
     * Lọc và tìm kiếm Dịch vụ theo Tên/Mô tả và Trạng thái cho Admin.
     */
    public List<Service> findFilteredPaginated(String search, String status, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM Services WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (service_name LIKE ? OR description LIKE ?) ");
            String like = "%" + search.trim() + "%";
            params.add(like);
            params.add(like);
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status.trim())) {
            sql.append("AND status = ? ");
            params.add("ACTIVE".equalsIgnoreCase(status.trim()));
        }

        sql.append("ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(limit);

        return queryList(sql.toString(), mapper, params.toArray());
    }

    /**
     * Đếm tổng số Dịch vụ khớp bộ lọc cho Admin.
     */
    public int countFiltered(String search, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Services WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (service_name LIKE ? OR description LIKE ?) ");
            String like = "%" + search.trim() + "%";
            params.add(like);
            params.add(like);
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status.trim())) {
            sql.append("AND status = ? ");
            params.add("ACTIVE".equalsIgnoreCase(status.trim()));
        }

        return queryCount(sql.toString(), params.toArray());
    }
}
