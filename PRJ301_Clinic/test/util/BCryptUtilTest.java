package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BCryptUtilTest {

    @Test
    @DisplayName("Test BCrypt hashing produces valid non-empty hash string")
    public void testHashPassword() {
        String plainPassword = "password123";
        String hash = BCryptUtil.hashPassword(plainPassword);

        assertNotNull(hash, "Hash should not be null");
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"), "Hash should follow BCrypt format");
        assertNotEquals(plainPassword, hash, "Hash must not equal plain password");
    }

    @Test
    @DisplayName("Test BCrypt checkPassword returns true for correct password")
    public void testCheckPasswordSuccess() {
        String plainPassword = "mySecretPassword";
        String hash = BCryptUtil.hashPassword(plainPassword);

        assertTrue(BCryptUtil.checkPassword(plainPassword, hash), "Password matching should succeed");
    }

    @Test
    @DisplayName("Test BCrypt checkPassword returns false for wrong password")
    public void testCheckPasswordFailure() {
        String plainPassword = "mySecretPassword";
        String hash = BCryptUtil.hashPassword(plainPassword);

        assertFalse(BCryptUtil.checkPassword("wrongPassword", hash), "Password matching should fail for wrong password");
    }
}
