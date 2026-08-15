package util;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * BCryptUtil - Tiện ích Mã hóa Mật khẩu người dùng linh hoạt (Reflection + SHA-256 Fallback).
 * Tự động nhận diện BCrypt khi có jbcrypt-0.4.jar trong WEB-INF/lib mà KHÔNG bị lỗi biên dịch NetBeans.
 */
public class BCryptUtil {

    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            return null;
        }
        try {
            Class<?> bcryptClass = Class.forName("org.mindrot.jbcrypt.BCrypt");
            Method gensaltMethod = bcryptClass.getMethod("gensalt", int.class);
            Method hashpwMethod = bcryptClass.getMethod("hashpw", String.class, String.class);

            Object salt = gensaltMethod.invoke(null, 10);
            return (String) hashpwMethod.invoke(null, rawPassword, salt);
        } catch (Throwable t) {
            // Fallback SHA-256 nếu không nạp được jbcrypt ở Compile-time
            return hashSha256(rawPassword);
        }
    }

    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null || hashedPassword.trim().isEmpty()) {
            return false;
        }

        // Nếu chuỗi hash theo định dạng BCrypt ($2a$, $2b$, $2y$)
        if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$")) {
            try {
                Class<?> bcryptClass = Class.forName("org.mindrot.jbcrypt.BCrypt");
                Method checkpwMethod = bcryptClass.getMethod("checkpw", String.class, String.class);
                return (Boolean) checkpwMethod.invoke(null, rawPassword, hashedPassword);
            } catch (Throwable t) {
                return false;
            }
        }

        // Kiểm tra khớp mật khẩu thô hoặc SHA-256
        if (rawPassword.equals(hashedPassword)) {
            return true;
        }
        return hashSha256(rawPassword).equalsIgnoreCase(hashedPassword);
    }

    private static String hashSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
