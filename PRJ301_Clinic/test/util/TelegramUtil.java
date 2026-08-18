package util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TelegramUtil - Tiện ích Gửi Thông Báo Tự Động tới Telegram Bot (Real-time Webhook Notification).
 */
public class TelegramUtil {

    private static final Logger LOGGER = Logger.getLogger(TelegramUtil.class.getName());

    // Cấu hình linh hoạt từ Biến Môi Trường (System.getenv)
    private static String getBotToken() {
        String token = System.getenv("TELEGRAM_BOT_TOKEN");
        if (token == null || token.trim().isEmpty()) {
            token = System.getProperty("TELEGRAM_BOT_TOKEN", "");
        }
        return token;
    }

    private static String getChatId() {
        String chatId = System.getenv("TELEGRAM_CHAT_ID");
        if (chatId == null || chatId.trim().isEmpty()) {
            chatId = System.getProperty("TELEGRAM_CHAT_ID", "");
        }
        return chatId;
    }

    /**
     * Gửi thông báo Telegram Bất Đồng Bộ (Async) để không làm chậm Latency của Webhook.
     */
    public static void sendTelegramNotificationAsync(String messageText) {
        CompletableFuture.runAsync(() -> {
            sendTelegramNotificationSync(messageText);
        });
    }

    /**
     * Gửi thông báo Telegram Đồng Bộ qua Telegram Bot API (sendMessage).
     */
    public static boolean sendTelegramNotificationSync(String messageText) {
        String botToken = getBotToken();
        String chatId = getChatId();

        if (botToken.isEmpty() || chatId.isEmpty()) {
            LOGGER.info("TELEGRAM_BOT_TOKEN hoac TELEGRAM_CHAT_ID chua duoc cau hinh. Bo qua gui Telegram.");
            return false;
        }

        try {
            String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            String postData = "chat_id=" + URLEncoder.encode(chatId, "UTF-8")
                    + "&text=" + URLEncoder.encode(messageText, "UTF-8")
                    + "&parse_mode=HTML";

            byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postDataBytes);
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                LOGGER.info("Gui thong bao Telegram THANH CONG!");
                return true;
            } else {
                LOGGER.warning("Gui thong bao Telegram THAT BAI! HTTP Code: " + responseCode);
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Loi khi gui thong bao Telegram", e);
            return false;
        }
    }

    /**
     * Helper Soạn Thông Báo Chuyển Khoản Ngân Hàng SePay Thành Công.
     */
    public static String buildPaymentSuccessMessage(int appointmentId, String transactionCode, double amount, String patientName) {
        return "<b>🎉 NHẬN THÔNG BÁO THANH TOÁN TỰ ĐỘNG VIETQR SEPAY</b>\n"
                + "━━━━━━━━━━━━━━━━━━━\n"
                + "📋 <b>Mã Đơn Lịch Hẹn</b>: #" + appointmentId + "\n"
                + "👤 <b>Bệnh Nhân</b>: " + (patientName != null ? patientName : "Khách hàng") + "\n"
                + "💵 <b>Số Tiền Đã Thu</b>: <code>" + String.format("%,.0f", amount) + " VNĐ</code>\n"
                + "🏛️ <b>Mã Giao Dịch Ngân Hàng</b>: <code>" + transactionCode + "</code>\n"
                + "⏰ <b>Trạng Thái</b>: ✅ <b>ĐÃ THANH TOÁN (PAID)</b>\n"
                + "━━━━━━━━━━━━━━━━━━━\n"
                + "🌐 <i>PRJ301 Clinic & Spa Automation Bot</i>";
    }
}
