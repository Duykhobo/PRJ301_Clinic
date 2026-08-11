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
}
