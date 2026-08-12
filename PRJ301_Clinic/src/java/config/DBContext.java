package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBContext {

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();

            // Driver & Connection String SQL Server
            config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            config.setJdbcUrl("jdbc:sqlserver://localhost:1433;databaseName=PRJ301_ClinicDB;encrypt=false;trustServerCertificate=true");
            String dbUsername = System.getenv("DB_USERNAME");
            if (dbUsername == null || dbUsername.trim().isEmpty()) {
                dbUsername = "sa";
            }

            String dbPassword = System.getenv("DB_PASSWORD");
            if (dbPassword == null || dbPassword.trim().isEmpty()) {
                dbPassword = "12345";
            }

            config.setUsername(dbUsername);
            config.setPassword(dbPassword);

            // Cấu hình HikariCP Connection Pool
            config.setMaximumPoolSize(15);
            config.setMinimumIdle(5);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(20000);
            config.setMaxLifetime(1800000);
            config.setPoolName("PRJ301ClinicPool");

            // Tối ưu hóa PreparedStatements cho SQL Server
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
        } catch (Throwable e) {
            System.err.println("=== LỖI KHỞI TẠO DBCONTEXT STATIC BLOCK ===");
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Lấy một kết nối SQL Connection từ HikariCP Pool.
     *
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
