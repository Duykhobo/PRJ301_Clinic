package dao;

import java.util.Collections;
import java.util.List;

/**
 * Interface Generic IDAO đại diện cho tầng Data Access Object (DAO) chuẩn Enterprise.
 * Áp dụng trọn vẹn 2 nguyên tắc SOLID:
 * 1. Interface Segregation Principle (I): Sử dụng Java 8 Default Methods giúp các DAO con 
 *    không bị ép buộc phải triển khai những hàm không cần thiết.
 * 2. Dependency Inversion Principle (D): Cho phép tầng Service phụ thuộc vào Interface 
 *    thay vì Class DAO cụ thể, thuận tiện cho việc viết Unit Test Mocking.
 *
 * @param <T> Kiểu Entity Model POJO (User, Service, Appointment...)
 * @param <K> Kiểu khóa chính Primary Key (Integer, Long, String...)
 */
public interface IDAO<T, K> {

    /**
     * Tìm kiếm một bản ghi duy nhất theo khóa chính.
     *
     * @param id Khóa chính
     * @return Entity tương ứng hoặc null nếu không tìm thấy
     */
    default T findById(K id) {
        return null;
    }

    /**
     * Lấy toàn bộ danh sách bản ghi khả dụng trong CSDL.
     *
     * @return List các Entity
     */
    default List<T> findAll() {
        return Collections.emptyList();
    }

    /**
     * Thêm mới một bản ghi vào CSDL.
     *
     * @param entity Đối tượng cần lưu
     * @return true nếu thêm thành công, ngược lại false
     */
    default boolean insert(T entity) {
        return false;
    }

    /**
     * Cập nhật thông tin bản ghi hiện có theo khóa chính.
     *
     * @param entity Đối tượng chứa dữ liệu mới
     * @return true nếu cập nhật thành công, ngược lại false
     */
    default boolean update(T entity) {
        return false;
    }

    /**
     * Xóa bản ghi khỏi CSDL theo khóa chính (Xóa cứng hoặc Xóa mềm).
     *
     * @param id Khóa chính
     * @return true nếu xóa thành công, ngược lại false
     */
    default boolean delete(K id) {
        return false;
    }
}
