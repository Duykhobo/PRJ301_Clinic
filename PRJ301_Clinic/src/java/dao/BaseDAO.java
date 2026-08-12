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
     * Overload queryOne cho phép thực thi chung trong 1 Connection Transaction.
     */
    protected <E> E queryOne(Connection conn, String sql, RowMapper<E> mapper, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Overload executeUpdate cho phép thực thi chung trong 1 Connection Transaction.
     */
    protected boolean executeUpdate(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Helper thực thi INSERT và tự động lấy ID sinh tự động (Generated Key) trong 1 Transaction.
     */
    protected int executeInsertAndGetGeneratedKey(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            setParameters(ps, params);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        return gk.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Functional Interface phục vụ thực thi Transaction chung 1 Connection.
     */
    @FunctionalInterface
    protected interface TransactionCallback<E> {
        E doInTransaction(Connection conn) throws Exception;
    }

    /**
     * Helper quản lý Giao dịch Nguyên tử (Atomic Transaction Helper).
     * Tự động mở connection, disable auto-commit, commit khi thành công và rollback khi gặp sự cố.
     */
    protected <E> E executeTransaction(TransactionCallback<E> action) throws Exception {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            E result = action.doInTransaction(conn); // Thực thi các bước dùng chung connection này

            conn.commit(); // Commit giao dịch
            return result;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback an toàn khi gặp sự cố
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Lỗi rollback transaction", ex);
                }
            }
            throw e; // Ném lại ngoại lệ cho tầng trên xử lý
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close(); // Đóng connection trả lại Pool
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Lỗi đóng connection", ex);
                }
            }
        }
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
