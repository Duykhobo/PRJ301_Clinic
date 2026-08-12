package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    @DisplayName("Test isValidUsername with valid and invalid inputs")
    public void testIsValidUsername() {
        assertTrue(ValidationUtil.isValidUsername("drminh"));
        assertTrue(ValidationUtil.isValidUsername("patient123"));
        assertFalse(ValidationUtil.isValidUsername("ab")); // Too short (<4)
        assertFalse(ValidationUtil.isValidUsername("user@name")); // Contains invalid char
        assertFalse(ValidationUtil.isValidUsername(null));
    }

    @Test
    @DisplayName("Test isValidPhone with valid Vietnamese phone numbers")
    public void testIsValidPhone() {
        assertTrue(ValidationUtil.isValidPhone("0987654321"));
        assertTrue(ValidationUtil.isValidPhone("0701485200"));
        assertFalse(ValidationUtil.isValidPhone("1234567890")); // Doesn't start with 03,05,07,08,09
        assertFalse(ValidationUtil.isValidPhone("09876543")); // Less than 10 digits
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    @DisplayName("Test isValidEmail with valid emails and null/empty fallback")
    public void testIsValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("user@gmail.com"));
        assertTrue(ValidationUtil.isValidEmail("")); // Optional empty email allowed
        assertTrue(ValidationUtil.isValidEmail(null)); // Optional null allowed
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
    }
}
