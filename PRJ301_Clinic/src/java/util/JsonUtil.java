package util;

/**
 * Tiện ích hỗ trợ xử lý và tạo chuỗi JSON nhanh chóng cho các API/AJAX.
 */
public class JsonUtil {

    /**
     * Wrap nhanh response thành chuẩn {"success":bool, "message":"..."}
     */
    public static String simpleResult(boolean success, String message) {
        return String.format("{\"success\":%b,\"message\":\"%s\"}",
                success, escapeJson(message));
    }

    /**
     * Escape ký tự đặc biệt trong JSON string để tránh lỗi cú pháp
     */
    public static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        // Thay thế dấu backslash và dấu ngoặc kép để JSON format không bị gãy
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
