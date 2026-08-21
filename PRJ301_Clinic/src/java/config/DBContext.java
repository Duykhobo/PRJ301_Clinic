package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DBContext - Quản lý kết nối CSDL SQL Server với cơ chế ThreadLocal
 * Connection.
 *
 */
public class DBContext {

    private static final Logger LOGGER = Logger.getLogger(DBContext.class.getName());
    private static final String DRIVER_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    static {
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            System.err.println("=== CHƯA NẠP ĐƯỢC DRIVER SQL SERVER (sqljdbc4.jar) ===");
            e.printStackTrace();
        }
    }

    /**
     * Lấy Connection của thread hiện tại.
     *
     * @return Connection của thread hiện tại (tạo mới nếu chưa có hoặc đã đóng).
     * @throws SQLException nếu không kết nối được tới CSDL.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn == null || conn.isClosed()) {
            String url = buildJdbcUrl();
            String user = buildUsername();
            String password = buildPassword();

            conn = DriverManager.getConnection(url, user, password);

            connectionHolder.set(conn);
        }
        return conn;
    }

    /**
     * Đóng Connection của thread hiện tại và xóa khỏi ThreadLocal.
     *
     */
    public static void clearConnection() {
        Connection conn = connectionHolder.get();
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đóng Connection trong clearConnection()", e);
        } finally {
            connectionHolder.remove();
        }
    }

    // =========================================================================
    // Helper: đọc config từ env variable hoặc system property
    // =========================================================================
    private static String getEnvOrDefault(String name, String defaultValue) {
        String val = System.getenv(name);
        if (val == null || val.trim().isEmpty()) {
            val = System.getProperty(name, defaultValue);
        }
        return val != null ? val.trim() : defaultValue;
    }

    /**
     * Xây dựng JDBC URL từ các env variable.
     * Tách ra để {@link #getConnection()} gọi khi cần tạo connection mới.
     */
    private static String buildJdbcUrl() {
        String dbHost = getEnvOrDefault("DB_HOST", "localhost");
        String dbPort = getEnvOrDefault("DB_PORT", "1433");
        String dbName = getEnvOrDefault("DB_NAME", "PRJ301_ClinicDB");

        String url = System.getenv("DB_URL");
        if (url == null || url.trim().isEmpty()) {
            url = System.getProperty("DB_URL");
        }
        if (url == null || url.trim().isEmpty()) {
            url = "jdbc:sqlserver://" + dbHost + ":" + dbPort
                    + ";databaseName=" + dbName
                    + ";encrypt=false;trustServerCertificate=true";
        }
        return url;
    }

    private static String buildUsername() {
        return getEnvOrDefault("DB_USERNAME", "sa");
    }

    private static String buildPassword() {
        return getEnvOrDefault("DB_PASSWORD", "12345");
    }

    public static void shutdown() {
        // Safe no-op — clearConnection() đã xử lý từng request
    }
}
