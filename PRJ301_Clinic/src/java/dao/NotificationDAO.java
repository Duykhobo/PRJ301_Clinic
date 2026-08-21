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
import model.Notification;

/**
 * NotificationDAO - Quản lý truy vấn và thông báo thời gian thực đa vai trò.
 */
public class NotificationDAO extends BaseDAO<Notification> {

    private static final Logger LOGGER = Logger.getLogger(NotificationDAO.class.getName());

    protected Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setType(rs.getString("type"));
        n.setIsRead(rs.getBoolean("is_read"));
        n.setLink(rs.getString("link"));
        n.setCreatedAt(rs.getTimestamp("created_at"));
        return n;
    }

    /**
     * Lấy danh sách thông báo mới nhất của người dùng.
     */
    public List<Notification> findByUserId(int userId, int limit) {
        String sql = "SELECT TOP (?) id, user_id, title, message, type, is_read, link, created_at "
                + "FROM Notifications WHERE user_id = ? ORDER BY created_at DESC, id DESC";
        return queryList(sql, this::mapResultSetToNotification, limit > 0 ? limit : 20, userId);
    }

    /**
     * Đếm số lượng thông báo CHƯA ĐỌC của người dùng.
     */
    public int countUnreadByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM Notifications WHERE user_id = ? AND is_read = 0";
        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm thông báo chưa đọc của user " + userId, e);
        }
        return 0;
    }

    /**
     * Đánh dấu 1 thông báo cụ thể là đã đọc.
     */
    public boolean markAsRead(int notificationId, int userId) {
        String sql = "UPDATE Notifications SET is_read = 1 WHERE id = ? AND user_id = ?";
        return executeUpdate(sql, notificationId, userId);
    }

    /**
     * Đánh dấu TẤT CẢ thông báo của người dùng là đã đọc.
     */
    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE Notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        return executeUpdate(sql, userId);
    }

    /**
     * Đẩy thông báo dùng Connection hiện tại (Tham gia cùng 1 Transaction).
     */
    public static void pushNotification(Connection conn, int userId, String title, String message, String type, String link) {
        if (conn == null || userId <= 0) return;
        String sql = "INSERT INTO Notifications (user_id, title, message, type, is_read, link, created_at) VALUES (?, ?, ?, ?, 0, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title != null ? title : "Thông Báo");
            ps.setString(3, message != null ? message : "");
            ps.setString(4, type != null ? type : "INFO");
            ps.setString(5, link);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi chèn Notification trong Transaction cho user " + userId, e);
        }
    }

    /**
     * Đẩy thông báo độc lập (Tự lấy Connection từ ThreadLocal).
     */
    public static void pushNotification(int userId, String title, String message, String type, String link) {
        if (userId <= 0) return;
        String sql = "INSERT INTO Notifications (user_id, title, message, type, is_read, link, created_at) VALUES (?, ?, ?, ?, 0, ?, GETDATE())";
        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setString(2, title != null ? title : "Thông Báo");
                ps.setString(3, message != null ? message : "");
                ps.setString(4, type != null ? type : "INFO");
                ps.setString(5, link);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi chèn Notification cho user " + userId, e);
        }
    }

    /**
     * Đẩy thông báo đến toàn bộ người dùng theo Vai Trò (Role: RECEPTIONIST, ADMIN, etc.)
     */
    public static void pushNotificationToRole(String role, String title, String message, String type, String link) {
        if (role == null || role.trim().isEmpty()) return;
        String sql = "INSERT INTO Notifications (user_id, title, message, type, is_read, link, created_at) "
                + "SELECT id, ?, ?, ?, 0, ?, GETDATE() FROM Users WHERE role = ? AND status = 1";
        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, title != null ? title : "Thông Báo Hệ Thống");
                ps.setString(2, message != null ? message : "");
                ps.setString(3, type != null ? type : "INFO");
                ps.setString(4, link);
                ps.setString(5, role.trim().toUpperCase());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gửi thông báo tới Role " + role, e);
        }
    }

    /**
     * Dọn dẹp dữ liệu thông báo cũ đã đọc (Data Retention Policy).
     */
    public int cleanOldReadNotifications(int days) {
        int d = days > 0 ? days : 30;
        String sql = "DELETE FROM Notifications WHERE is_read = 1 AND created_at < DATEADD(DAY, -?, GETDATE())";
        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, d);
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi dọn dẹp thông báo cũ", e);
        }
        return 0;
    }
}