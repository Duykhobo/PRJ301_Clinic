package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * EmailUtil - Trợ lý Gửi Email HTML Thực Tế Ngầm (Async SSLSocket SMTP).
 * Tự động gửi Email thực tế về Hòm Thư Gmail Bệnh nhân dùng chuẩn SSL JDK 8
 * thuần (Zero JAR dependencies).
 */
public class EmailUtil {

    private static final Logger LOGGER = Logger.getLogger(EmailUtil.class.getName());
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);

    private static String getSenderEmail() {
        String email = System.getenv("MAIL_USERNAME");
        if (email == null || email.trim().isEmpty()) {
            email = System.getenv("SMTP_USERNAME");
        }
        if (email == null || email.trim().isEmpty()) {
            email = System.getProperty("MAIL_USERNAME", "nthanhduy310@gmail.com");
        }
        return email;
    }

    private static String getSenderPassword() {
        String pass = System.getenv("MAIL_PASSWORD");
        if (pass == null || pass.trim().isEmpty()) {
            pass = System.getenv("SMTP_PASSWORD");
        }
        if (pass == null || pass.trim().isEmpty()) {
            pass = System.getProperty("MAIL_PASSWORD", "ivbsjdfpchmkfqgb");
        }
        return pass.replace(" ", "");
    }

    /**
     * Gửi Email HTML Xác Nhận Đặt Lịch Hẹn Khám Bệnh Thực Tế (Async SSLSocket).
     */
    public static void sendBookingConfirmationAsync(String recipientEmail, String patientName,
            String doctorName, String serviceName, Date appointmentDate, Time startTime, BigDecimal totalPrice) {

        executor.submit(() -> {
            try {
                if (recipientEmail == null || recipientEmail.trim().isEmpty() || !recipientEmail.contains("@")) {
                    LOGGER.info("Bệnh nhân không có Email hợp lệ, bỏ qua gửi mail.");
                    return;
                }

                String subject = "🏥 XÁC NHẬN ĐẶT LỊCH HẸN KHÁM KHÁCH HÀNG — PRJ301 CLINIC";
                String htmlContent = "<div style=\"font-family: Arial, sans-serif; background-color: #0f172a; padding: 30px; color: #ffffff;\">"
                        + "  <div style=\"max-width: 600px; margin: 0 auto; background: #1e293b; border-radius: 16px; padding: 25px; border: 1px solid #334155;\">"
                        + "    <h2 style=\"color: #38bdf8; text-align: center;\">PRJ301 CLINIC &amp; SPA</h2>"
                        + "    <h3 style=\"color: #4ade80; text-align: center;\">XÁC NHẬN ĐẶT LỊCH HẸN THÀNH CÔNG</h3>"
                        + "    <p>Xin chào <strong>" + patientName + "</strong>,</p>"
                        + "    <p>Lịch hẹn khám bệnh của bạn đã được hệ thống ghi nhận với thông tin chi tiết như sau:</p>"
                        + "    <table style=\"width: 100%; color: #ffffff; border-collapse: collapse; margin-top: 15px;\">"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Dịch vụ:</td><td style=\"padding: 8px; font-weight: bold; color: #fbbf24;\">"
                        + serviceName + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Bác sĩ phụ trách:</td><td style=\"padding: 8px; font-weight: bold;\">"
                        + doctorName + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Ngày khám:</td><td style=\"padding: 8px; font-weight: bold;\">"
                        + appointmentDate.toString() + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Khung giờ:</td><td style=\"padding: 8px; font-weight: bold; color: #38bdf8;\">"
                        + (startTime != null ? startTime.toString() : "Ca 60 phút") + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Tổng chi phí:</td><td style=\"padding: 8px; font-weight: bold; color: #4ade80;\">"
                        + totalPrice.toString() + " VNĐ</td></tr>"
                        + "    </table>"
                        + "    <p style=\"margin-top: 20px; font-size: 0.9em; color: #94a3b8; text-align: center;\">Cảm ơn bạn đã lựa chọn dịch vụ của PRJ301 Clinic &amp; Spa!</p>"
                        + "  </div>"
                        + "</div>";

                sendRealSmtpEmail(recipientEmail, subject, htmlContent);

            } catch (Exception e) {
                LOGGER.log(Level.WARNING,
                        "Không thể gửi email xác nhận đặt lịch (Async Worker Notice): " + e.getMessage());
            }
        });
    }

    /**
     * Gửi Email HTML Xác Nhận Thanh Toán VietQR SePay Thành Công Thực Tế (Async
     * SSLSocket).
     */
    public static void sendPaymentSuccessAsync(String recipientEmail, String patientName, String transactionCode,
            BigDecimal amount) {
        executor.submit(() -> {
            try {
                if (recipientEmail == null || recipientEmail.trim().isEmpty() || !recipientEmail.contains("@")) {
                    return;
                }

                String subject = "💳 HÓA ĐƠN THANH TOÁN VIETQR SEPAY THÀNH CÔNG — PRJ301 CLINIC";
                String htmlContent = "<div style=\"font-family: Arial, sans-serif; background-color: #0f172a; padding: 30px; color: #ffffff;\">"
                        + "  <div style=\"max-width: 600px; margin: 0 auto; background: #1e293b; border-radius: 16px; padding: 25px; border: 1px solid #334155;\">"
                        + "    <h2 style=\"color: #38bdf8; text-align: center;\">PRJ301 CLINIC &amp; SPA</h2>"
                        + "    <h3 style=\"color: #4ade80; text-align: center;\">XÁC NHẬN THANH TOÁN VIETQR THÀNH CÔNG</h3>"
                        + "    <p>Xin chào <strong>" + patientName + "</strong>,</p>"
                        + "    <p>Giao dịch thanh toán Chuyển khoản VietQR của bạn đã được xác thực tự động thành công:</p>"
                        + "    <table style=\"width: 100%; color: #ffffff; border-collapse: collapse; margin-top: 15px;\">"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Mã giao dịch SePay:</td><td style=\"padding: 8px; font-weight: bold; color: #fbbf24;\">"
                        + transactionCode + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Số tiền đã nhận:</td><td style=\"padding: 8px; font-weight: bold; color: #4ade80;\">"
                        + amount.toString() + " VNĐ</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Trạng thái:</td><td style=\"padding: 8px; font-weight: bold; color: #38bdf8;\">ĐÃ THANH TOÁN (PAID)</td></tr>"
                        + "    </table>"
                        + "    <p style=\"margin-top: 20px; font-size: 0.9em; color: #94a3b8; text-align: center;\">Cảm ơn quý khách đã hoàn tất thanh toán!</p>"
                        + "  </div>"
                        + "</div>";

                sendRealSmtpEmail(recipientEmail, subject, htmlContent);

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Lỗi gửi email xác nhận thanh toán (Async): " + e.getMessage());
            }
        });
    }

    public static void sendRefundNotificationAsync(String recipientEmail, String patientName, int appointmentId,
            String doctorName, String serviceName, String appointmentDate, String startTime, BigDecimal amount) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            return;
        }

        executor.submit(() -> {
            try {
                String subject = "[PRJ301 Clinic] Thông báo Hủy lịch & Cam kết Hoàn tiền Cuộc hẹn #" + appointmentId;
                String formattedAmount = (amount != null) ? String.format("%,.0f VNĐ", amount.doubleValue()) : "0 VNĐ";
                String htmlContent = "<div style='font-family: Arial, sans-serif; background-color: #0f172a; color: #ffffff; padding: 30px; border-radius: 12px; max-width: 600px; margin: 0 auto; border: 1px solid #1e293b;'>"
                        + "<h2 style='color: #ef4444; margin-top: 0;'>⚠️ THÔNG BÁO HỦY LỊCH KHÁM & CAM KẾT HOÀN TIỀN</h2>"
                        + "<p>Xin chào <strong>" + patientName + "</strong>,</p>"
                        + "<p>Chúng tôi rất tiếc phải thông báo rằng cuộc hẹn <strong>#" + appointmentId
                        + "</strong> của quý khách đã bị hủy do lịch trình đột xuất của Bác sĩ.</p>"
                        + "<div style='background: #1e293b; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #ef4444;'>"
                        + "<p style='margin: 5px 0;'>🏥 <strong>Bác sĩ:</strong> " + doctorName + "</p>"
                        + "<p style='margin: 5px 0;'>🦷 <strong>Dịch vụ:</strong> " + serviceName + "</p>"
                        + "<p style='margin: 5px 0;'>📅 <strong>Ngày & Giờ:</strong> " + appointmentDate + " lúc "
                        + startTime + "</p>"
                        + "<p style='margin: 5px 0; font-size: 1.1em; color: #38bdf8;'>💰 <strong>Số tiền hoàn trả:</strong> "
                        + formattedAmount + "</p>"
                        + "</div>"
                        + "<div style='background: rgba(239, 68, 68, 0.1); padding: 15px; border-radius: 8px; border: 1px solid rgba(239, 68, 68, 0.3); margin-bottom: 20px;'>"
                        + "<p style='margin: 0; color: #fca5a5;'>🛡️ <strong>Cam kết quyền lợi:</strong> Yêu cầu hoàn tiền của quý khách đã được chuyển sang hàng đợi <strong>[CHỜ HOÀN TIỀN]</strong>. Bộ phận Kế toán / Lễ tân của Phòng khám sẽ chuyển khoản lại 100% số tiền vào tài khoản của quý khách trong vòng 24h hoặc hỗ trợ đổi sang lịch khám khác miễn phí.</p>"
                        + "</div>"
                        + "<p>Mọi thắc mắc vui lòng liên hệ Hotline <strong>1900 6868</strong> để được hỗ trợ tức thì.</p>"
                        + "<p style='color: #94a3b8; font-size: 0.85em; margin-top: 30px;'>Trân trọng,<br>Đội ngũ Chăm sóc Khách hàng PRJ301 Clinic & Spa</p>"
                        + "</div>";

                sendRealSmtpEmail(recipientEmail, subject, htmlContent);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Không thể gửi email thông báo hoàn tiền (Async): " + e.getMessage());
            }
        });
    }

    /**
     * Gửi Email HTML Khôi Phục Mật Khẩu Đồng Bộ (Báo thành công / thất bại trực tiếp cho Controller).
     */
    public static boolean sendPasswordResetSync(String recipientEmail, String patientName, String newPassword) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty() || !recipientEmail.contains("@")) {
            return false;
        }

        String subject = "🔑 YÊU CẦU KHÔI PHỤC MẬT KHẨU TÀI KHOẢN — PRJ301 CLINIC";
        String htmlContent = "<div style=\"font-family: Arial, sans-serif; background-color: #0f172a; padding: 30px; color: #ffffff;\">"
                + "  <div style=\"max-width: 600px; margin: 0 auto; background: #1e293b; border-radius: 16px; padding: 25px; border: 1px solid #334155;\">"
                + "    <h2 style=\"color: #38bdf8; text-align: center;\">PRJ301 CLINIC &amp; SPA</h2>"
                + "    <h3 style=\"color: #fbbf24; text-align: center;\">KHÔI PHỤC MẬT KHẨU TÀI KHOẢN</h3>"
                + "    <p>Xin chào <strong>" + patientName + "</strong>,</p>"
                + "    <p>Hệ thống đã nhận được yêu cầu khôi phục mật khẩu cho tài khoản liên kết với địa chỉ Email này.</p>"
                + "    <div style=\"background: #0f172a; padding: 18px; border-radius: 12px; text-align: center; margin: 20px 0; border: 1px solid #0ea5e9;\">"
                + "      <p style=\"margin: 0; color: #94a3b8; font-size: 0.9em;\">Mật khẩu tạm thời mới của bạn là:</p>"
                + "      <h2 style=\"color: #38bdf8; margin: 10px 0; letter-spacing: 2px;\">" + newPassword + "</h2>"
                + "    </div>"
                + "    <p style=\"color: #fca5a5; font-size: 0.88em;\">⚠️ Vì lý do bảo mật, vui lòng đăng nhập ngay và đổi lại mật khẩu cá nhân tại trang Hồ Sơ Cá Nhân.</p>"
                + "    <p style=\"margin-top: 20px; font-size: 0.85em; color: #94a3b8; text-align: center;\">Cảm ơn bạn đã lựa chọn dịch vụ của PRJ301 Clinic &amp; Spa!</p>"
                + "  </div>"
                + "</div>";

        return sendRealSmtpEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Thuật toán Gửi Real Email qua Gmail SSLSocket Port 465 (100% Thuần JDK 8 -
     * Không cần JAR ngoài).
     * @return true nếu gửi thành công, false nếu kết nối máy chủ Mail bị thất bại
     */
    public static boolean sendRealSmtpEmail(String recipientEmail, String subject, String htmlContent) {
        String senderEmail = getSenderEmail();
        String senderPass = getSenderPassword();

        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket("smtp.gmail.com", 465);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    PrintWriter writer = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                readSmtpResponse(reader); // 220 Greeting

                sendSmtpCmd(writer, reader, "EHLO localhost");
                sendSmtpCmd(writer, reader, "AUTH LOGIN");
                sendSmtpCmd(writer, reader,
                        Base64.getEncoder().encodeToString(senderEmail.getBytes(StandardCharsets.UTF_8)));
                sendSmtpCmd(writer, reader,
                        Base64.getEncoder().encodeToString(senderPass.getBytes(StandardCharsets.UTF_8)));

                sendSmtpCmd(writer, reader, "MAIL FROM:<" + senderEmail + ">");
                sendSmtpCmd(writer, reader, "RCPT TO:<" + recipientEmail + ">");
                sendSmtpCmd(writer, reader, "DATA");

                writer.println("From: PRJ301 Clinic & Spa <" + senderEmail + ">");
                writer.println("To: <" + recipientEmail + ">");
                writer.println("Subject: =?UTF-8?B?"
                        + Base64.getEncoder().encodeToString(subject.getBytes(StandardCharsets.UTF_8)) + "?=");
                writer.println("MIME-Version: 1.0");
                writer.println("Content-Type: text/html; charset=UTF-8");
                writer.println();
                writer.println(htmlContent);
                writer.println(".");
                writer.flush();

                readSmtpResponse(reader); // 250 Message accepted
                sendSmtpCmd(writer, reader, "QUIT");

                LOGGER.info("📩 [REAL SMTP GMAIL DELIVERED] Email HTML đã được gửi THỰC TẾ thành công về Inbox của: "
                        + recipientEmail);
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể kết nối Gmail SSLSocket SMTP: " + e.getMessage());
            return false;
        }
    }

    private static String readSmtpResponse(BufferedReader reader) throws Exception {
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            LOGGER.info("SMTP Server: " + line);
            sb.append(line).append("\n");
            if (line.length() >= 4 && line.charAt(3) == ' ') {
                break;
            }
        }
        return sb.toString();
    }

    private static void sendSmtpCmd(PrintWriter writer, BufferedReader reader, String cmd) throws Exception {
        LOGGER.info("SMTP Client: " + (cmd.startsWith("AUTH") || cmd.length() > 20 ? "[PROTECTED_DATA]" : cmd));
        writer.println(cmd);
        writer.flush();
        String resp = readSmtpResponse(reader);
        if (resp != null && (resp.startsWith("5") || resp.startsWith("4"))) {
            throw new IOException("SMTP Command Failed: " + resp.trim());
        }
    }
}
