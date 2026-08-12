package dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Functional Interface dùng để map 1 dòng trong ResultSet thành đối tượng POJO T (Java 8 Lambda).
 *
 * @param <T> Kiểu Model (vd: User, Service, Appointment)
 */
@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
