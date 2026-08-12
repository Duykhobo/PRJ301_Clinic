package constant;

/**
 * MessageConstant - Quản lý tập trung các Thông báo Lỗi & Thành công hiển thị cho Người dùng.
 */
public class MessageConstant {

    // Login & Auth Errors
    public static final String ERR_INVALID_USERNAME = "Tên đăng nhập từ 4-20 ký tự (chữ cái, chữ số, không chứa ký tự đặc biệt)!";
    public static final String ERR_INVALID_PASSWORD = "Mật khẩu từ 6-32 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 chữ số!";
    public static final String ERR_PASSWORD_MISMATCH = "Xác nhận mật khẩu không trùng khớp!";
    public static final String ERR_INVALID_FULLNAME = "Họ và tên chứa từ 2-100 ký tự hợp lệ!";
    public static final String ERR_INVALID_PHONE = "Số điện thoại không hợp lệ (10 số Việt Nam, bắt đầu bằng 03, 05, 07, 08, 09)!";
    public static final String ERR_INVALID_EMAIL = "Địa chỉ Email không đúng định dạng!";
    public static final String ERR_USERNAME_EXISTS = "Tên đăng nhập này đã được đăng ký bởi người khác!";
    public static final String ERR_EMAIL_EXISTS = "Địa chỉ Email này đã được đăng ký bởi người khác!";
    public static final String ERR_LOGIN_FAILED = "Tên đăng nhập hoặc mật khẩu không chính xác, hoặc tài khoản đã bị khóa!";

    // Success Messages
    public static final String MSG_REGISTER_SUCCESS = "Đăng ký tài khoản thành công! Vui lòng đăng nhập.";
    public static final String MSG_LOGOUT_SUCCESS = "Bạn đã đăng xuất tài khoản an toàn.";
}
