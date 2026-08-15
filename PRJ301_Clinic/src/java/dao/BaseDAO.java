package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.DBContext;

/**
 * Lớp trừu tượng BaseDAO giúp triệt tiêu 90% lặp code trong JDBC (DRY
 * Principle). Tự động lấy Connection qua {@link config.DBContext} (được quản lý
 * bởi TransactionFilter), try-with-resources cho Statement/ResultSet và tham số
 * Varargs.
 *
 * @param <T> Kiểu dữ liệu Model POJO
 */
public abstract class BaseDAO<T> {

    private static final Logger LOGGER = Logger.getLogger(BaseDAO.class.getName());

    /**
     * Truy vấn SELECT trả về 1 bản ghi duy nhất.
     */
    protected T queryOne(String sql, RowMapper<T> mapper, Object... params) {
        try {
            Connection conn = DBContext.getConnection();
            try ( PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try ( ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapper.mapRow(rs);
                    }
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
        try {
            Connection conn = DBContext.getConnection();
            try ( PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapper.mapRow(rs));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thực thi queryList: " + sql, e);
        }
        return list;
    }

    /**
     * Truy vấn {@code SELECT COUNT(*)} trả về một số nguyên.
     */
    protected int queryCount(String sql, Object... params) {
        try {
            Connection conn = DBContext.getConnection();
            try ( PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                try ( ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi queryCount: " + sql, e);
        }
        return 0;
    }

    /**
     * Thực thi lệnh INSERT, UPDATE, DELETE.
     */
    protected boolean executeUpdate(String sql, Object... params) {
        try {
            Connection conn = DBContext.getConnection();
            try ( PreparedStatement ps = conn.prepareStatement(sql)) {
                setParameters(ps, params);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thực thi executeUpdate: " + sql, e);
        }
        return false;
    }

    /**
     * Overload queryOne cho phép thực thi chung trong 1 Connection Transaction.
     */
    protected <E> E queryOne(Connection conn, String sql, RowMapper<E> mapper, Object... params) throws SQLException {
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Overload executeUpdate cho phép thực thi chung trong 1 Connection
     * Transaction.
     */
    protected boolean executeUpdate(Connection conn, String sql, Object... params) throws SQLException {
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Thực thi câu lệnh INSERT và trả về khóa chính (ID) tự động tăng. Hàm này
     * ném trực tiếp SQLException để Filter ở ngoài có thể rollback.
     */
    protected int executeInsertAndGetGeneratedKey(String sql, Object... params) throws SQLException {
        Connection conn = DBContext.getConnection();
        try ( PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            setParameters(ps, params);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try ( ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        return gk.getInt(1);
                    }
                }
            }
        }
        return -1;
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
