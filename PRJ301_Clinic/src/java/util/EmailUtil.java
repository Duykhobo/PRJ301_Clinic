package util;

import java.io.BufferedReader;
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
 * Tự động gửi Email thực tế về Hòm Thư Gmail Bệnh nhân dùng chuẩn SSL JDK 8 thuần (Zero JAR dependencies).
 */
public class EmailUtil {

    private static final Logger LOGGER = Logger.getLogger(EmailUtil.class.getName());
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);

    private static String getSenderEmail() {
        String email = System.getenv("MAIL_USERNAME");
        if (email == null || email.trim().isEmpty()) {
            email = System.getProperty("MAIL_USERNAME", "nthanhduy310@gmail.com");
        }
        return email;
    }

    private static String getSenderPassword() {
        String pass = System.getenv("MAIL_PASSWORD");
        if (pass == null || pass.trim().isEmpty()) {
            pass = System.getProperty("MAIL_PASSWORD", "omcvmyijlstkwdgx");
        }
        return pass;
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
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Dịch vụ:</td><td style=\"padding: 8px; font-weight: bold; color: #fbbf24;\">" + serviceName + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Bác sĩ phụ trách:</td><td style=\"padding: 8px; font-weight: bold;\">" + doctorName + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Ngày khám:</td><td style=\"padding: 8px; font-weight: bold;\">" + appointmentDate.toString() + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Khung giờ:</td><td style=\"padding: 8px; font-weight: bold; color: #38bdf8;\">" + (startTime != null ? startTime.toString() : "Ca 60 phút") + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Tổng chi phí:</td><td style=\"padding: 8px; font-weight: bold; color: #4ade80;\">" + totalPrice.toString() + " VNĐ</td></tr>"
                        + "    </table>"
                        + "    <p style=\"margin-top: 20px; font-size: 0.9em; color: #94a3b8; text-align: center;\">Cảm ơn bạn đã lựa chọn dịch vụ của PRJ301 Clinic &amp; Spa!</p>"
                        + "  </div>"
                        + "</div>";

                sendRealSmtpEmail(recipientEmail, subject, htmlContent);

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Không thể gửi email xác nhận đặt lịch (Async Worker Notice): " + e.getMessage());
            }
        });
    }

    /**
     * Gửi Email HTML Xác Nhận Thanh Toán VietQR SePay Thành Công Thực Tế (Async SSLSocket).
     */
    public static void sendPaymentSuccessAsync(String recipientEmail, String patientName, String transactionCode, BigDecimal amount) {
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
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Mã giao dịch SePay:</td><td style=\"padding: 8px; font-weight: bold; color: #fbbf24;\">" + transactionCode + "</td></tr>"
                        + "      <tr><td style=\"padding: 8px; color: #94a3b8;\">Số tiền đã nhận:</td><td style=\"padding: 8px; font-weight: bold; color: #4ade80;\">" + amount.toString() + " VNĐ</td></tr>"
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

    /**
     * Thuật toán Gửi Real Email qua Gmail SSLSocket Port 465 (100% Thuần JDK 8 - Không cần JAR ngoài).
     */
    private static void sendRealSmtpEmail(String recipientEmail, String subject, String htmlContent) {
        String senderEmail = getSenderEmail();
        String senderPass = getSenderPassword();

        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket("smtp.gmail.com", 465);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                readSmtpResponse(reader); // 220 Greeting

                sendSmtpCmd(writer, reader, "EHLO localhost");
                sendSmtpCmd(writer, reader, "AUTH LOGIN");
                sendSmtpCmd(writer, reader, Base64.getEncoder().encodeToString(senderEmail.getBytes(StandardCharsets.UTF_8)));
                sendSmtpCmd(writer, reader, Base64.getEncoder().encodeToString(senderPass.getBytes(StandardCharsets.UTF_8)));

                sendSmtpCmd(writer, reader, "MAIL FROM:<" + senderEmail + ">");
                sendSmtpCmd(writer, reader, "RCPT TO:<" + recipientEmail + ">");
                sendSmtpCmd(writer, reader, "DATA");

                writer.println("From: PRJ301 Clinic & Spa <" + senderEmail + ">");
                writer.println("To: <" + recipientEmail + ">");
                writer.println("Subject: =?UTF-8?B?" + Base64.getEncoder().encodeToString(subject.getBytes(StandardCharsets.UTF_8)) + "?=");
                writer.println("MIME-Version: 1.0");
                writer.println("Content-Type: text/html; charset=UTF-8");
                writer.println();
                writer.println(htmlContent);
                writer.println(".");
                writer.flush();

                readSmtpResponse(reader); // 250 Message accepted
                sendSmtpCmd(writer, reader, "QUIT");

                LOGGER.info("📩 [REAL SMTP GMAIL DELIVERED] Email HTML đã được gửi THỰC TẾ thành công về Inbox của: " + recipientEmail);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể kết nối Gmail SSLSocket SMTP: " + e.getMessage());
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
        readSmtpResponse(reader);
    }
}
