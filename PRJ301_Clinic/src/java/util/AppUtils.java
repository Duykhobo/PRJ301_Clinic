package util;

import javax.servlet.http.HttpServletRequest;

/**
 * AppUtils - Tiện ích hỗ trợ xử lý tham số HTTP và thao tác dữ liệu an toàn.
 * Triệt tiêu hoàn toàn nguy cơ phát sinh NullPointerException và NumberFormatException (DRY Principle).
 * Tuân thủ quy chuẩn Clean Code và sử dụng toán tử 3 ngôi (Ternary Operator).
 */
public class AppUtils {

    private AppUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Lấy tham số kiểu chuỗi từ HttpServletRequest với giá trị mặc định.
     * Tự động trim() và kiểm tra null-safety.
     *
     * @param request    HttpServletRequest
     * @param paramName  Tên tham số
     * @param defaultVal Giá trị trả về nếu tham số null hoặc rỗng
     * @return Chuỗi đã chuẩn hóa hoặc defaultVal
     */
    public static String getParam(HttpServletRequest request, String paramName, String defaultVal) {
        if (request == null || paramName == null) {
            return defaultVal;
        }
        String val = request.getParameter(paramName);
        return (val != null && !val.trim().isEmpty()) ? val.trim() : defaultVal;
    }

    /**
     * Lấy tham số kiểu số nguyên (int) từ HttpServletRequest.
     * Tự động bắt NumberFormatException và trả về defaultVal nếu parse lỗi.
     *
     * @param request    HttpServletRequest
     * @param paramName  Tên tham số
     * @param defaultVal Giá trị số nguyên mặc định
     * @return Giá trị int đã parse hoặc defaultVal
     */
    public static int getInt(HttpServletRequest request, String paramName, int defaultVal) {
        String val = getParam(request, paramName, null);
        if (val == null) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Lấy tham số kiểu số thực (double) từ HttpServletRequest.
     * Tự động bắt NumberFormatException và trả về defaultVal nếu parse lỗi.
     *
     * @param request    HttpServletRequest
     * @param paramName  Tên tham số
     * @param defaultVal Giá trị double mặc định
     * @return Giá trị double đã parse hoặc defaultVal
     */
    public static double getDouble(HttpServletRequest request, String paramName, double defaultVal) {
        String val = getParam(request, paramName, null);
        if (val == null) {
            return defaultVal;
        }
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Lấy tham số kiểu boolean từ HttpServletRequest.
     *
     * @param request    HttpServletRequest
     * @param paramName  Tên tham số
     * @param defaultVal Giá trị boolean mặc định
     * @return true nếu giá trị là "true", "1", "on"; ngược lại false hoặc defaultVal
     */
    public static boolean getBoolean(HttpServletRequest request, String paramName, boolean defaultVal) {
        String val = getParam(request, paramName, null);
        if (val == null) {
            return defaultVal;
        }
        return "true".equalsIgnoreCase(val) || "1".equals(val) || "on".equalsIgnoreCase(val);
    }

    /**
     * Kiểm tra chuỗi có null hoặc rỗng hay không.
     *
     * @param str Chuỗi cần kiểm tra
     * @return true nếu null hoặc sau khi trim() có độ dài = 0
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
