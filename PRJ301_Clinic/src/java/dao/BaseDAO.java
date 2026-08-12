package dao;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp trừu tượng BaseDAO giúp triệt tiêu 90% lặp code trong JDBC (DRY Principle).
 * Tự động quản lý Connection Pool HikariCP, try-with-resources và tham số Varargs (Object... params).
 *
 * @param <T> Kiểu dữ liệu Model POJO
 */
public abstract class BaseDAO<T> {

    private static final Logger LOGGER = Logger.getLogger(BaseDAO.class.getName());

    /**
     * Truy vấn SELECT trả về 1 bản ghi duy nhất.
     */
    protected T queryOne(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setParameters(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thực thi queryOne: " + sql, e);
        }
        return null;
    }

    /**
     * Truy vấn SELECT trả về danh sách List<T>.
     */
    protected List<T> queryList(String sql, RowMapper<T> mapper, Object... params) {
        List<T> list = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setParameters(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thực thi queryList: " + sql, e);
        }
        return list;
    }

    /**
     * Thực thi lệnh INSERT, UPDATE, DELETE.
     *
     * @return số dòng bị ảnh hưởng (affected rows) > 0 nếu thành công.
     */
    protected boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setParameters(ps, params);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thực thi executeUpdate: " + sql, e);
        }
        return false;
    }

    /**
     * Helper gán tham số động Varargs (Object... params) vào PreparedStatement.
     */
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
    }
}
