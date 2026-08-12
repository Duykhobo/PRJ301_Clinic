package test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import config.DBContext;

/**
 * TestDBConnection - Chương trình kiểm thử kết nối CSDL SQL Server qua
 * HikariCP.
 */
public class TestDBConnection {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  ĐANG KIỂM TRA KẾT NỐI SQL SERVER QUA HIKARICP   ");
        System.out.println("==================================================");

        try (Connection conn = config.DBContext.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ KẾT NỐI THÀNH CÔNG TỚI CSDL PRJ301_ClinicDB!");

                // Truy vấn thử bảng Users
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total_users FROM Users");
                if (rs.next()) {
                    System.out.println("📊 Số lượng tài khoản hiện tại trong bảng Users: " + rs.getInt("total_users"));
                }

                // Truy vấn thử bảng Services
                ResultSet rsServices = stmt.executeQuery("SELECT COUNT(*) AS total_services FROM Services");
                if (rsServices.next()) {
                    System.out.println("🏷️ Số lượng dịch vụ hiện tại trong bảng Services: "
                            + rsServices.getInt("total_services"));
                }

                System.out.println("==================================================");
                System.out.println("🎉 HỆ THỐNG CSDL VẬN HÀNH HOÀN HẢO!");
            }
        } catch (Exception e) {
            System.err.println("❌ KHÔNG THỂ KẾT NỐI TỚI CSDL SQL SERVER!");
            System.err.println("Chi tiết lỗi: " + e.getMessage());
            System.err.println("\n💡 VUI LÒNG KIỂM TRA LẠI:");
            System.err.println("1. Dịch vụ SQL Server (MSSQLSERVER / SQLEXPRESS) đã được START chưa?");
            System.err.println("2. Đã tạo Database 'PRJ301_ClinicDB' và chạy file 'database.sql' chưa?");
            System.err
                    .println("3. Username ('sa') và Mật khẩu trong DBContext.java đã đúng với SQL Server local chưa?");
        } finally {
            DBContext.shutdown();
        }
    }
}
