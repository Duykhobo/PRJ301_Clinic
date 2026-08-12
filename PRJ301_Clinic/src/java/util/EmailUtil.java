package util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * EmailUtil - Trợ lý Gửi Email Xác Nhận Bệnh Nhân Không Khóa Luồng (Async Non-Blocking).
 * Tự động gửi Email HTML thiết kế chuyên nghiệp trong Thread Pool riêng biệt.
 */
public class EmailUtil {

    private static final Logger LOGGER = Logger.getLogger(EmailUtil.class.getName());
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);

    // Cấu hình Nạp Key từ Env Variable (Bảo mật 100% chống lộ Key)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = System.getenv("MAIL_USERNAME") != null 
            ? System.getenv("MAIL_USERNAME") : "nthanhduy310@gmail.com";
    private static final String SENDER_PASSWORD = System.getenv("MAIL_PASSWORD") != null 
            ? System.getenv("MAIL_PASSWORD") : "";

    /**
     * Gửi Email HTML Xác Nhận Đặt Lịch Hẹn Khám Bệnh (Async).
     */
    public static void sendBookingConfirmationAsync(String recipientEmail, String patientName,
            String doctorName, String serviceName, Date appointmentDate, Time startTime, BigDecimal totalPrice) {
        
        executor.submit(() -> {
            try {
                if (recipientEmail == null || recipientEmail.trim().isEmpty() || !recipientEmail.contains("@")) {
                    LOGGER.info("Bệnh nhân không có Email hợp lệ, bỏ qua gửi mail.");
                    return;
                }

                Properties props = new Properties();
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL, "PRJ301 Clinic & Spa Notification"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("🏥 XÁC NHẬN ĐẶT LỊCH HẸN KHÁM KHÁCH HÀNG — PRJ301 CLINIC");

                String htmlContent = "<div style=\"font-family: Arial, sans-serif; background-color: #0f172a; padding: 30px; color: #ffffff;\">"
                        + "  <div style=\"max-width: 600px; margin: 0 auto; background: #1e293b; border-radius: 16px; padding: 25px; border: 1px solid #334155;\">"
                        + "    <h2 style=\"color: #38bdf8; text-align: center;\">PRJ301 CLINIC &amp; SPA</h2>"
                        + "    <h3 style=\"color: #4ade80; text-align: center;\">XÁC NHẬN ĐẶT LỊCH HẸN THÀNH CÔNG</h3>"
                        + "    <p>Xin chào <strong>" + patientName + "</strong>,</p>"
                        + "    <p>Lịch hẹn khám bệnh của bạn đã được hệ thống ghi nhận với thông tin chi tiết như sau:</p>"
                        + "    <table style=\"width: 100%; color: #ffffff; border-collapse: collapse; margin-top: 15px;\">"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Dịch vụ:</td><td style=\"padding: 8px; font-weight: bold; color: #fbbf24;\">" + serviceName + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Bác sĩ phụ trách:</td><td style=\"padding: 8px; font-weight: bold;\">" + doctorName + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Ngày khám:</td><td style=\"padding: 8px; font-weight: bold;\">" + appointmentDate.toString() + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Khung giờ:</td><td style=\"padding: 8px; font-weight: bold; color: #38bdf8;\">" + (startTime != null ? startTime.toString() : "Ca 60 phút") + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Tổng chi phí:</td><td style=\"padding: 8px; font-weight: bold; color: #4ade80;\">" + totalPrice.toString() + " VNĐ</td></tr>"
                        + "    </table>"
                        + "    <p style=\"margin-top: 20px; font-size: 0.9em; color: #94a3b8; text-align: center;\">Cảm ơn bạn đã lựa chọn dịch vụ của PRJ301 Clinic &amp; Spa!</p>"
                        + "  </div>"
                        + "</div>";

                message.setContent(htmlContent, "text/html; charset=UTF-8");
                Transport.send(message);
                LOGGER.info("Async Email Booking Confirmation sent successfully to " + recipientEmail);

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Không thể gửi email xác nhận đặt lịch (Async Worker Notice): " + e.getMessage());
            }
        });
    }

    /**
     * Gửi Email HTML Xác Nhận Thanh Toán VietQR SePay Thành Công (Async).
     */
    public static void sendPaymentSuccessAsync(String recipientEmail, String patientName, String transactionCode, BigDecimal amount) {
        executor.submit(() -> {
            try {
                if (recipientEmail == null || recipientEmail.trim().isEmpty() || !recipientEmail.contains("@")) {
                    return;
                }

                Properties props = new Properties();
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL, "PRJ301 Clinic Payment"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("💳 HÓA ĐƠN THANH TOÁN VIETQR SEPAY THÀNH CÔNG — PRJ301 CLINIC");

                String htmlContent = "<div style=\"font-family: Arial, sans-serif; background-color: #0f172a; padding: 30px; color: #ffffff;\">"
                        + "  <div style=\"max-width: 600px; margin: 0 auto; background: #1e293b; border-radius: 16px; padding: 25px; border: 1px solid #334155;\">"
                        + "    <h2 style=\"color: #38bdf8; text-align: center;\">PRJ301 CLINIC &amp; SPA</h2>"
                        + "    <h3 style=\"color: #4ade80; text-align: center;\">XÁC NHẬN THANH TOÁN VIETQR THÀNH CÔNG</h3>"
                        + "    <p>Xin chào <strong>" + patientName + "</strong>,</p>"
                        + "    <p>Giao dịch thanh toán Chuyển khoản VietQR của bạn đã được xác thực tự động thành công:</p>"
                        + "    <table style=\"width: 100%; color: #ffffff; border-collapse: collapse; margin-top: 15px;\">"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Mã giao dịch SePay:</td><td style=\"padding: 8px; font-weight: bold; color: #fbbf24;\">" + transactionCode + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Số tiền đã nhận:</td><td style=\"padding: 8px; font-weight: bold; color: #4ade80;\">" + amount.toString() + " VNĐ</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Trạng thái:</td><td style=\"padding: 8px; font-weight: bold; color: #38bdf8;\">ĐÃ THANH TOÁN (PAID)</td></tr>"
                        + "    </table>"
                        + "    <p style=\"margin-top: 20px; font-size: 0.9em; color: #94a3b8; text-align: center;\">Cảm ơn quý khách đã hoàn tất thanh toán!</p>"
                        + "  </div>"
                        + "</div>";

                message.setContent(htmlContent, "text/html; charset=UTF-8");
                Transport.send(message);
                LOGGER.info("Async Payment Email Confirmation sent to " + recipientEmail);

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Lỗi gửi email xác nhận thanh toán (Async): " + e.getMessage());
            }
        });
    }
}
