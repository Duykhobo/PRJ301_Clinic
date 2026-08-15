package util;

import java.util.regex.Pattern;
import exception.ValidationException;

/**
 * ValidationUtil - Tiện ích Kiểm tra Định dạng Dữ liệu Đầu Vào (Fail-Fast Validation).
 */
public class ValidationUtil {

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{4,20}$";
    private static final String PHONE_REGEX = "^(03|05|07|08|09)\\d{8}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(USERNAME_REGEX, username.trim());
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 32) {
            return false;
        }
        return true;
    }

    public static boolean isValidFullname(String fullname) {
        if (fullname == null || fullname.trim().isEmpty()) {
            return false;
        }
        int len = fullname.trim().length();
        return len >= 2 && len <= 100;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(PHONE_REGEX, phone.trim());
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email.trim());
    }

    public static void validateBookingParams(String doctorIdStr, String serviceIdStr, String scheduleIdStr, String appointmentDateStr) {
        if (doctorIdStr == null || doctorIdStr.trim().isEmpty() ||
            serviceIdStr == null || serviceIdStr.trim().isEmpty() ||
            scheduleIdStr == null || scheduleIdStr.trim().isEmpty() ||
            appointmentDateStr == null || appointmentDateStr.trim().isEmpty()) {
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
