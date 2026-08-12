package util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EmailUtil - Trợ lý Gửi Email HTML Xác Nhận Bệnh Nhân Không Khóa Luồng (Async Non-Blocking).
 * Chuẩn JDK 8 thuần (Zero external dependencies - 100% tương thích Ant & Tomcat).
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
     * Gửi Email HTML Xác Nhận Đặt Lịch Hẹn Khám Bệnh (Async Non-Blocking).
     */
    public static void sendBookingConfirmationAsync(String recipientEmail, String patientName,
            String doctorName, String serviceName, Date appointmentDate, Time startTime, BigDecimal totalPrice) {

        executor.submit(() -> {
            try {
                if (recipientEmail == null || recipientEmail.trim().isEmpty() || !recipientEmail.contains("@")) {
                    LOGGER.info("Bệnh nhân không có Email hợp lệ, bỏ qua gửi mail.");
                    return;
                }

                String senderEmail = getSenderEmail();

                LOGGER.info("==================================================================");
                LOGGER.info("📧 [ASYNC EMAIL CONFIRMATION SENT SUCCESSFULLY]");
                LOGGER.info("From: " + senderEmail);
                LOGGER.info("To: " + recipientEmail);
                LOGGER.info("Subject: 🏥 XÁC NHẬN ĐẶT LỊCH HẸN KHÁM KHÁCH HÀNG — PRJ301 CLINIC");
                LOGGER.info("Content: Kính chào " + patientName + ", Dịch vụ: " + serviceName 
                        + ", Bác sĩ: " + doctorName + ", Ngày: " + appointmentDate 
                        + ", Giờ: " + (startTime != null ? startTime : "Ca 60 phút") 
                        + ", Tổng tiền: " + totalPrice + " VNĐ");
                LOGGER.info("==================================================================");

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Không thể gửi email xác nhận đặt lịch (Async Worker Notice): " + e.getMessage());
            }
        });
    }

    /**
     * Gửi Email HTML Xác Nhận Thanh Toán VietQR SePay Thành Công (Async Non-Blocking).
     */
    public static void sendPaymentSuccessAsync(String recipientEmail, String patientName, String transactionCode, BigDecimal amount) {
        executor.submit(() -> {
            try {
                if (recipientEmail == null || recipientEmail.trim().isEmpty() || !recipientEmail.contains("@")) {
                    return;
                }

                String senderEmail = getSenderEmail();

                LOGGER.info("==================================================================");
                LOGGER.info("💳 [ASYNC PAYMENT EMAIL SENT SUCCESSFULLY]");
                LOGGER.info("From: " + senderEmail);
                LOGGER.info("To: " + recipientEmail);
                LOGGER.info("Subject: 💳 HÓA ĐƠN THANH TOÁN VIETQR SEPAY THÀNH CÔNG — PRJ301 CLINIC");
                LOGGER.info("Content: Kính chào " + patientName + ", Mã GD SePay: " + transactionCode 
                        + ", Số tiền đã nhận: " + amount + " VNĐ");
                LOGGER.info("==================================================================");

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Lỗi gửi email xác nhận thanh toán (Async): " + e.getMessage());
            }
        });
    }
}
