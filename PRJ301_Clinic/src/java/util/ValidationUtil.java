package util;

import java.util.Map;
import java.util.regex.Pattern;
import exception.ValidationException;

/**
 * ValidationUtil - Tiện ích Kiểm tra & Tách lỗi Từng Field (Field-Level Validation).
 * Viết gọn, tối ưu logic với toán tử 3 ngôi (Ternary Operator).
 */
public class ValidationUtil {

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{4,20}$";
    private static final String PHONE_REGEX = "^(03|05|07|08|09)\\d{8}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static boolean isValidUsername(String u) {
        return (u != null && !u.trim().isEmpty()) ? Pattern.matches(USERNAME_REGEX, u.trim()) : false;
    }

    public static boolean isValidPassword(String p) {
        return (p != null) ? p.length() >= 6 && p.length() <= 32 : false;
    }

    public static boolean isValidFullname(String f) {
        return (f != null && !f.trim().isEmpty()) ? f.trim().length() >= 2 && f.trim().length() <= 100 : false;
    }

    public static boolean isValidPhone(String p) {
        return (p != null && !p.trim().isEmpty()) ? Pattern.matches(PHONE_REGEX, p.trim()) : false;
    }

    public static boolean isValidEmail(String e) {
        return (e != null && !e.trim().isEmpty()) ? Pattern.matches(EMAIL_REGEX, e.trim()) : false;
    }

    /**
     * Gán lỗi cho từng field cụ thể nếu điều kiện không thỏa mãn
     */
    public static void validateField(Map<String, String> errors, String field, boolean isValid, String message) {
        if (!isValid && errors != null) {
            errors.put(field, message);
        }
    }

    public static void validateBookingParams(String doctorIdStr, String serviceIdStr, String scheduleIdStr, String appointmentDateStr) {
        boolean missing = (doctorIdStr == null || doctorIdStr.trim().isEmpty()) ||
                          (serviceIdStr == null || serviceIdStr.trim().isEmpty()) ||
                          (scheduleIdStr == null || scheduleIdStr.trim().isEmpty()) ||
                          (appointmentDateStr == null || appointmentDateStr.trim().isEmpty());
        if (missing) {
            throw new ValidationException("Thông tin đặt lịch không đầy đủ. Vui lòng chọn đầy đủ Dịch vụ, Bác sĩ và Ca giờ!");
        }

        try {
            Integer.parseInt(doctorIdStr);
            Integer.parseInt(serviceIdStr);
            Integer.parseInt(scheduleIdStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("Mã Bác sĩ, Dịch vụ hoặc Ca làm việc không hợp lệ!");
        }
    }
}
