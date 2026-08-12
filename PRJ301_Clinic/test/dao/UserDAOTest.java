package dao;

import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử tự động JUnit 5 cho UserDAO có in Log trực quan.
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
        testPassword = "Password123";
        System.out.println("\n========================================================");
        System.out.println("   [START] BAT DAU CHAY BO UNIT TEST USERDAO");
        System.out.println("========================================================");
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Đăng ký Bệnh nhân mới thành công")
    public void testRegisterSuccess() {
        System.out.println("\n--- [TEST 1] Testing register() Benh nhan moi ---");
        User newUser = new User();
        newUser.setUsername(testUsername);
        newUser.setPassword(testPassword);
        newUser.setFullname("Benh Nhan Kiem Thu");
        newUser.setPhone("0901234567");
        newUser.setEmail(testUsername + "@gmail.com");
        newUser.setRole("PATIENT");
        newUser.setStatus(true);

        System.out.println("-> Dang khoi tao Benh nhan moi:");
        System.out.println("   + Username: " + testUsername);
        System.out.println("   + Fullname: Benh Nhan Kiem Thu");
        System.out.println("   + Phone: 0901234567");
        System.out.println("   + Email: " + testUsername + "@gmail.com");

        boolean result = userDAO.register(newUser);
        System.out.println("-> Ket qua Dang ky CSDL: " + (result ? "SUCCESS (Thanh cong)" : "FAILED (That bai)"));

        assertTrue(result, "Đăng ký Bệnh nhân mới phải trả về true!");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Kiểm tra tồn tại User vừa đăng ký")
    public void testExistsByUsername() {
        System.out.println("\n--- [TEST 2] Testing existsByUsername(" + testUsername + ") ---");
        boolean exists = userDAO.existsByUsername(testUsername);
        System.out.println("-> Username [" + testUsername + "] ton tai trong CSDL: " + exists);

        assertTrue(exists, "Username vừa đăng ký phải tồn tại trong CSDL!");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Đăng nhập thành công với thông tin đúng")
    public void testLoginSuccess() {
        System.out.println("\n--- [TEST 3] Testing login() voi Username & Mat khau DUNG ---");
        System.out.println("-> Dang thu dang nhap | Username: " + testUsername + " | Password: " + testPassword);
        User user = userDAO.login(testUsername, testPassword);

        if (user != null) {
            System.out.println("-> Xac thuc BCrypt THANH CONG:");
            System.out.println("   + User ID: " + user.getId());
            System.out.println("   + Username: " + user.getUsername());
            System.out.println("   + Role: " + user.getRole());
            System.out.println("   + Trang thai: " + (user.isStatus() ? "Active (Hoat dong)" : "Banned"));
        }

        assertNotNull(user, "Đăng nhập đúng phải trả về đối tượng User!");
        assertEquals(testUsername, user.getUsername(), "Username trả về phải khớp!");
        assertEquals("PATIENT", user.getRole(), "Role mặc định phải là PATIENT!");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Đăng nhập thất bại khi nhập sai mật khẩu")
    public void testLoginWrongPassword() {
        System.out.println("\n--- [TEST 4] Testing login() voi Mat khau SAI ---");
        System.out.println("-> Dang thu nghiem dang nhap mat khau sai: WrongPassword999");
        User user = userDAO.login(testUsername, "WrongPassword999");
        System.out.println("-> Ket qua tra ve: " + (user == null ? "NULL (Dung ky vong - Chan dang nhap)" : "LOI"));

        assertNull(user, "Đăng nhập sai mật khẩu bắt buộc phải trả về null!");
        System.out.println("\n========================================================");
        System.out.println("   [SUCCESS] KET THUC BO UNIT TEST USERDAO - PASSED 100%");
        System.out.println("========================================================\n");
    }
}
