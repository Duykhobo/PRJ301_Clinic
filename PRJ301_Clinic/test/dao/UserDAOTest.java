package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.junit.runner.RunWith;
import org.junit.platform.runner.JUnitPlatform;

import model.User;

/**
 * Bộ kiểm thử tự động JUnit 5 cho UserDAO.
 */
@RunWith(JUnitPlatform.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTest {

    private static UserDAO userDAO;
    private static String testUsername;
    private static String testPassword;

    @BeforeAll

    public static void setUp() {
        userDAO = new UserDAO();
        testUsername = "patient_" + (System.currentTimeMillis() % 10000);
        testPassword = "Pasword123";
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Đăng ký Bệnh nhân mới thành công")
    public void testRegisterSuccess() {
        // 1. Arrange
        User newUser = new User();
        newUser.setUsername(testUsername);
        newUser.setPassword(testPassword);
        newUser.setFullname("Bệnh Nhân Kiểm Thử");
        newUser.setPhone("0901234567");
        newUser.setEmail(testUsername + "@gmail.com");
        newUser.setRole("PATIENT");
        newUser.setStatus(true);

        // 2. Act
        boolean result = userDAO.register(newUser);

        // 3. Assert
        assertTrue(result, "Đăng ký bệnh nhân mới phải trả về null");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Kiểm tra tồn tại User vừa đăng ký")
    public void testExistsByUsername() {
        boolean exists = userDAO.existsByUsername(testUsername);
        assertTrue(exists, "Username vừa đăng ký đã tồn tại trên hệ thống");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Đăng nhập thành công với thông tin đúng")
    public void testLoginSuccess() {
        User user = userDAO.login(testUsername, testPassword);
        assertNotNull(user, "Đăng nhập đúng tài khoản/mật khẩu phải trả về đối tượng User!");
        assertEquals(testUsername, user.getUsername(), "Username trả về phải khớp!");
        assertEquals("PATIENT", user.getRole(), "Role mặc định phải là PATIENT!");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Đăng nhập thất bại khi nhập sai mật khẩu")
    public void testLoginWrongPassword() {
        User user = userDAO.login(testUsername, "WrongPassword999");
        assertNull(user, "Đăng nhập sai mật khẩu bắt buộc phải trả về null!");
    }
}
