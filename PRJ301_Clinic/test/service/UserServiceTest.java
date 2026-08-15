package service;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService();
    }

    @Test
    @DisplayName("Test Login with pre-seeded Admin account (admin/123456)")
    public void testLoginAdminSuccess() {
        User user = userService.login("admin", "123456");
        assertNotNull(user, "Admin login should succeed with correct credentials");
        assertEquals("ADMIN", user.getRole(), "User role should be ADMIN");
    }

    @Test
    @DisplayName("Test Login fails with wrong password")
    public void testLoginWrongPassword() {
        User user = userService.login("admin", "wrongpass");
        assertNull(user, "Login should return null for incorrect password");
    }

    @Test
    @DisplayName("Test Login fails with non-existent username")
    public void testLoginNonExistentUser() {
        User user = userService.login("non_existent_user_999", "123456");
        assertNull(user, "Login should return null for non-existent username");
    }
}
