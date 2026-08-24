package dao;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.DBContext;

/**
 * Lớp trừu tượng BaseDAO giúp triệt tiêu 90% lặp code trong JDBC (DRY
 * Principle). Tự động lấy Connection qua {@link config.DBContext} (được quản lý
 * bởi TransactionFilter), try-with-resources cho Statement/ResultSet, tham số
 * Varargs và cơ chế Auto-Reflection RowMapper.
 *
 * @param <T> Kiểu dữ liệu Model POJO
 */
public abstract class BaseDAO<T> {

    private static final Logger LOGGER = Logger.getLogger(BaseDAO.class.getName());

    // =========================================================================
    // 🚀 1. AUTO-REFLECTION ROWMAPPER (MINI-ORM THUẦN JAVA)
    // =========================================================================
    /**
     * Tự động ánh xạ ResultSet sang Model POJO bất kỳ bằng Java Reflection &
     * ResultSetMetaData. Tự động chuẩn hóa chuyển đổi giữa snake_case
     * (Database) và camelCase (Java POJO).
     *
     * @param <E> Kiểu Model cần ánh xạ
     * @param clazz Class của Model POJO
     * @return RowMapper<E>
     */
    public static <E> RowMapper<E> autoMapper(Class<E> clazz) {
        return rs -> {
            try {
                E instance = clazz.getDeclaredConstructor().newInstance();
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();

                // Lưu map: "tencotkhongdau" -> Chỉ số cột trong ResultSet (1-indexed)
                Map<String, Integer> colMap = new HashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    String rawColName = meta.getColumnLabel(i);
                    colMap.put(rawColName.replace("_", "").toLowerCase(), i);
                }

                for (Field field : clazz.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }
                    field.setAccessible(true);

                    // Chuẩn hóa tên trường Java: "phoneNumber" -> "phonenumber"
                    String cleanFieldName = field.getName().replace("_", "").toLowerCase();
                    Integer colIdx = colMap.get(cleanFieldName);

                    if (colIdx != null) {
                        Class<?> type = field.getType();
                        Object val = null;

                        if (type == String.class) {
                            val = rs.getString(colIdx);
                        } else if (type == int.class || type == Integer.class) {
                            val = rs.getObject(colIdx) != null ? rs.getInt(colIdx) : (type == int.class ? 0 : null);
                        } else if (type == long.class || type == Long.class) {
                            val = rs.getObject(colIdx) != null ? rs.getLong(colIdx) : (type == long.class ? 0L : null);
                        } else if (type == double.class || type == Double.class) {
                            val = rs.getObject(colIdx) != null ? rs.getDouble(colIdx) : (type == double.class ? 0.0 : null);
                        } else if (type == float.class || type == Float.class) {
                            val = rs.getObject(colIdx) != null ? rs.getFloat(colIdx) : (type == float.class ? 0.0f : null);
                        } else if (type == boolean.class || type == Boolean.class) {
                            val = rs.getBoolean(colIdx);
                        } else if (type == BigDecimal.class) {
                            val = rs.getBigDecimal(colIdx);
                        } else if (type == Date.class) {
                            val = rs.getDate(colIdx);
                        } else if (type == Time.class) {
                            val = rs.getTime(colIdx);
                        } else if (type == Timestamp.class) {
                            val = rs.getTimestamp(colIdx);
                        } else {
                            val = rs.getObject(colIdx);
                        }

                        if (val != null || !type.isPrimitive()) {
                            field.set(instance, val);
                        }
                    }
                }
                return instance;
            } catch (Exception e) {
                throw new SQLException("Lỗi AutoMapper cho class " + clazz.getSimpleName() + ": " + e.getMessage(), e);
            }
        };
    }

    /**
     * Truy vấn SELECT 1 bản ghi tự động bằng Reflection AutoMapper.
     */
    protected <E> E queryOneAuto(String sql, Class<E> clazz, Object... params) {
        return queryOne(sql, autoMapper(clazz), params);
    }

    /**
     * Truy vấn SELECT danh sách List tự động bằng Reflection AutoMapper.
     */
    protected <E> List<E> queryListAuto(String sql, Class<E> clazz, Object... params) {
        return queryList(sql, autoMapper(clazz), params);
    }

    // =========================================================================
    // 2. STANDARD JDBC QUERY HELPERS
    // =========================================================================
    /**
     * Truy vấn SELECT trả về 1 bản ghi duy nhất.
     */
    protected <E> E queryOne(String sql, RowMapper<E> mapper, Object... params) {
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
     * Truy vấn SELECT trả về danh sách List.
     */
    protected <E> List<E> queryList(String sql, RowMapper<E> mapper, Object... params) {
        List<E> list = new ArrayList<>();
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
     * Thực thi câu lệnh INSERT và trả về khóa chính (ID) tự động tăng.
     */
    protected int executeInsertAndGetGeneratedKey(String sql, Object... params) throws SQLException {
        Connection conn = DBContext.getConnection();
        try ( PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
