package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.BookingService;

/**
 * SepayWebhookServlet - Servlet Tiếp nhận Webhook Tự động từ Cổng Thanh Toán SePay VietQR.
 * Nhận HTTP POST chứa payload JSON thanh toán từ SePay -> Giải mã mã lịch hẹn "CLINIC<ID>"
 * -> Cập nhật CSDL trạng thái thanh toán = PAID và trạng thái lịch hẹn = CONFIRMED.
 */
@WebServlet(name = "SepayWebhookServlet", urlPatterns = {"/sepay-webhook"})
public class SepayWebhookServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SepayWebhookServlet.class.getName());
    private final BookingService bookingService = new BookingService();

    /**
     * Xử lý Webhook POST chính thức do SePay gửi về hệ thống.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        try {
            // Read JSON body payload from SePay Webhook
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String payload = sb.toString();
            LOGGER.info("Nhan Payload Webhook SePay: " + payload);

            String transferType = parseJsonField(payload, "transferType");
            String content = parseJsonField(payload, "content");
            if (content == null || content.isEmpty()) {
                content = parseJsonField(payload, "description");
            }
            String transactionCode = parseJsonField(payload, "referenceCode");
            if (transactionCode == null || transactionCode.isEmpty()) {
                transactionCode = parseJsonField(payload, "id");
            }

            // Chỉ xử lý giao dịch tiền vào (transferType = "in") theo chuẩn SePay Developer Docs
            if (transferType != null && !"in".equalsIgnoreCase(transferType)) {
                LOGGER.info("Bo qua giao dich tien ra (transferType = " + transferType + ")");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\": 200, \"message\": \"Ignored non-incoming transaction\"}");
                return;
            }

            int appointmentId = extractAppointmentId(content, request.getParameter("appointmentId"));

            if (appointmentId > 0) {
                boolean updated = bookingService.updatePaymentSuccess(appointmentId, transactionCode != null ? transactionCode : "SEPAY_AUTO");
                if (updated) {
                    LOGGER.info("Xac thuc thanh toan VietQR SePay thanh cong cho cuoc hen #" + appointmentId);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\": 200, \"message\": \"Payment processed successfully for appointment #" + appointmentId + "\"}");
                    return;
                }
            }

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\": 400, \"message\": \"Could not parse appointment ID from payload\"}");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Loi khi xu ly Webhook SePay", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": 500, \"message\": \"Internal Server Error\"}");
        }
    }

    /**
     * Hỗ trợ mô phỏng Thanh toán SePay trực tiếp qua GET (Dành cho chạy thử Localhost / Demo).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String appParam = request.getParameter("appointmentId");
            if (appParam != null && !appParam.trim().isEmpty()) {
                int appointmentId = Integer.parseInt(appParam);
                String txCode = "TEST_SEPAY_" + System.currentTimeMillis();
                boolean updated = bookingService.updatePaymentSuccess(appointmentId, txCode);
                if (updated) {
                    response.getWriter().write("{\"status\": 200, \"message\": \"Simulated SePay payment successful for #" + appointmentId + "\"}");
                    return;
                }
            }
            response.getWriter().write("{\"status\": 400, \"message\": \"Missing appointmentId parameter\"}");
        } catch (Exception e) {
            response.getWriter().write("{\"status\": 500, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Helper Regex giải mã Mã Lịch hẹn từ Nội dung chuyển khoản SePay (vd: "CLINIC25").
     */
    private int extractAppointmentId(String content, String fallbackParam) {
        if (content != null) {
            Pattern pattern = Pattern.compile("CLINIC(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        if (fallbackParam != null && !fallbackParam.trim().isEmpty()) {
            try {
                return Integer.parseInt(fallbackParam);
            } catch (NumberFormatException ignored) {
            }
        }
        return -1;
    }

    /**
     * Helper Parser đọc trường JSON đơn giản không phụ thuộc thư viện ngoài.
     */
    private String parseJsonField(String json, String key) {
        if (json == null) return null;
        String patternStr = "\"" + key + "\":\\s*\"?([^\",\\}]+)\"?";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}
