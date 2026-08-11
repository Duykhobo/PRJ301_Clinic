package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * BCryptUtil - Tiện ích Mã hóa Mật khẩu an toàn với BCrypt & Salt ngẫu nhiên.
 */
public class BCryptUtil {

    private static final int BCRYPT_WORK_FACTOR = 10;

    /**
     * Mã hóa chuỗi mật khẩu thô thành chuỗi Hash BCrypt 60 ký tự.
     *
     * @param plainPassword Mật khẩu thô
     * @return BCrypt Hash String
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống!");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
    }

    /**
     * Kiểm tra tính khớp giữa Mật khẩu thô và Chuỗi Hash BCrypt trong DB.
     *
     * @param plainPassword Mật khẩu thô do người dùng nhập
     * @param hashedPassword Chuỗi hash BCrypt lưu trong CSDL
     * @return boolean
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
