package util;

import java.util.regex.Pattern;

/**
 * ValidationUtil - Tiện ích Kiểm tra Định dạng Dữ liệu Đầu vào (Server-side
 * Regex).
 */
public class ValidationUtil {

    // Regular Expression Definitions
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{4,20}$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,32}$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    public static final String PHONE_REGEX = "^(03|05|07|08|09)\\d{8}$";
    public static final String FULLNAME_REGEX = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầnẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s]{2,100}$";
    public static final String TRANSACTION_CODE_REGEX = "^CLINIC\\d+$";

    public static boolean isValidUsername(String username) {
        return username != null && Pattern.matches(USERNAME_REGEX, username);
    }

    public static boolean isValidPassword(String password) {
        return password != null && Pattern.matches(PASSWORD_REGEX, password);
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Cho phép NULL / Rỗng cho khách hàng đăng ký tại quầy chỉ dùng SĐT
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && Pattern.matches(PHONE_REGEX, phone);
    }

    public static boolean isValidFullname(String fullname) {
        return fullname != null && Pattern.matches(FULLNAME_REGEX, fullname);
    }

    public static boolean isValidTransactionCode(String code) {
        return code != null && Pattern.matches(TRANSACTION_CODE_REGEX, code);
    }

    /**
     * Kiểm tra một chuỗi có phải là Số Nguyên Dương > 0 hay không.
     */
    public static boolean isPositiveInteger(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(str.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Kiểm tra chuỗi có không null và không rỗng sau khi trim.
     */
    public static boolean isNotNullOrEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Helper kiểm tra tổng hợp các tham số Request cho Đặt Lịch Hẹn (Booking).
     * Ném IllegalArgumentException kèm thông điệp rõ ràng nếu vi phạm.
     */
    public static void validateBookingParams(String doctorIdStr, String serviceIdStr, String scheduleIdStr, String dateStr) {
        if (!isPositiveInteger(serviceIdStr)) {
            throw new IllegalArgumentException("Vui lòng chọn Dịch vụ khám / Spa!");
        }
        if (!isPositiveInteger(doctorIdStr)) {
            throw new IllegalArgumentException("Vui lòng chọn Bác sĩ phụ trách!");
        }
        if (!isNotNullOrEmpty(dateStr)) {
            throw new IllegalArgumentException("Vui lòng chọn Ngày khám mong muốn!");
        }
        try {
            java.sql.Date bookingDate = java.sql.Date.valueOf(dateStr.trim());
            java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());
            if (bookingDate.before(today)) {
                throw new IllegalArgumentException("Không thể chọn ngày khám trong quá khứ!");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Ngày khám không đúng định dạng YYYY-MM-DD!");
        }
        if (!isPositiveInteger(scheduleIdStr)) {
            throw new IllegalArgumentException("Vui lòng bấm chọn một Ca khám 60 phút khả dụng!");
        }
    }
}
