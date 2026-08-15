package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DBContext - Quản lý kết nối CSDL SQL Server với cơ chế ThreadLocal Connection.
 *
 * <p><b>📖 Kiến thức cần nắm:</b></p>
 * <ul>
 *   <li>{@link ThreadLocal} lưu một giá trị riêng cho mỗi Thread — các thread khác nhau
 *       không thấy giá trị của nhau.</li>
 *   <li>Trong môi trường Servlet, mỗi HTTP Request chạy trên 1 thread riêng từ Thread Pool.</li>
 *   <li>ThreadLocal Connection cho phép các DAO khác nhau trong cùng 1 Request
 *       dùng chung 1 Connection → hỗ trợ Transaction nguyên tử.</li>
 * </ul>
 *
 * <p><b>⚠️ Quy tắc bắt buộc:</b> Sau khi xử lý xong request, PHẢI gọi
 * {@link #clearConnection()} trong khối {@code finally} để tránh connection leak.</p>
 */
public class DBContext {

    private static final Logger LOGGER = Logger.getLogger(DBContext.class.getName());
    private static final String DRIVER_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    // =========================================================================
    // TODO [BƯỚC 1]: Khai báo ThreadLocal<Connection>
    // =========================================================================
    // Gợi ý: ThreadLocal<Connection> là một biến static, private, final.
    // Cú pháp: private static final ThreadLocal<Connection> NAME = new ThreadLocal<>();
    // Tên biến gợi ý: connectionHolder
    //
    // ❓ Tại sao phải là static?
    //    → Vì DBContext không được khởi tạo (dùng static method), nên
    //      ThreadLocal cũng phải là static để tồn tại cùng ClassLoader.
    //
    // ❓ Tại sao phải là final?
    //    → ThreadLocal container không bao giờ thay đổi (chỉ nội dung bên trong thay đổi).
    // =========================================================================

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
     * <p><b>📖 Logic cần implement:</b></p>
     * <ol>
     *   <li>Gọi {@code connectionHolder.get()} để lấy connection đang được lưu.</li>
     *   <li>Kiểm tra: nếu connection là {@code null} <b>hoặc</b> đã bị đóng
     *       ({@code conn.isClosed() == true}):
     *       <ul>
     *         <li>Tạo connection mới bằng {@code DriverManager.getConnection(url, user, pass)}.</li>
     *         <li>Lưu vào ThreadLocal bằng {@code connectionHolder.set(conn)}.</li>
     *       </ul>
     *   </li>
     *   <li>Trả về connection.</li>
     * </ol>
     *
     * @return Connection của thread hiện tại (tạo mới nếu chưa có hoặc đã đóng).
     * @throws SQLException nếu không kết nối được tới CSDL.
     */
    public static Connection getConnection() throws SQLException {
        // TODO [BƯỚC 1a]: Lấy connection hiện tại từ ThreadLocal
        // Connection conn = ???

        // TODO [BƯỚC 1b]: Nếu conn == null hoặc conn.isClosed()...
        //   → Tạo connection mới (copy logic URL building từ phiên bản cũ bên dưới)
        //   → Lưu vào ThreadLocal
        //   Gợi ý: dùng getEnvOrDefault() có sẵn để build URL

        // TODO [BƯỚC 1c]: return conn;
        throw new UnsupportedOperationException("TODO: Implement getConnection() với ThreadLocal");
    }

    /**
     * Đóng Connection của thread hiện tại và xóa khỏi ThreadLocal.
     *
     * <p><b>📖 Logic cần implement:</b></p>
     * <ol>
     *   <li>Gọi {@code connectionHolder.get()} để lấy connection hiện tại.</li>
     *   <li>Nếu connection không null:
     *       <ul>
     *         <li>Gọi {@code conn.close()} trong try-catch (log lỗi nếu đóng thất bại).</li>
     *       </ul>
     *   </li>
     *   <li>Gọi {@code connectionHolder.remove()} — <b>luôn luôn phải gọi</b> dù đóng
     *       có lỗi hay không, để tránh thread pool tái sử dụng thread với connection cũ.</li>
     * </ol>
     *
     * <p><b>⚠️ Gọi method này trong khối {@code finally} của TransactionFilter.</b></p>
     */
    public static void clearConnection() {
        // TODO [BƯỚC 1d]: Lấy connection từ ThreadLocal
        // TODO [BƯỚC 1e]: Nếu không null → đóng connection trong try-catch, log lỗi nếu có
        // TODO [BƯỚC 1f]: Gọi connectionHolder.remove() — LUÔN LUÔN ở cuối (trong finally hoặc cuối method)
        throw new UnsupportedOperationException("TODO: Implement clearConnection()");
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

