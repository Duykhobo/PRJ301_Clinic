package dao;

import model.Service;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử tự động JUnit 5 cho ServiceDAO có in Log trực quan.
 */
@RunWith(JUnitPlatform.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceDAOTest {

    private static ServiceDAO serviceDAO;
    private static String testServiceName;

    @BeforeAll
    public static void setUp() {
        serviceDAO = new ServiceDAO();
        testServiceName = "Dich Vu Test Spa " + (System.currentTimeMillis() % 10000);
        System.out.println("\n========================================================");
        System.out.println("   [START] BAT DAU CHAY BO UNIT TEST SERVICEDAO");
        System.out.println("========================================================");
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Lấy danh sách tất cả dịch vụ Active")
    public void testFindAllActive() {
        System.out.println("\n--- [TEST 1] Testing findAllActive() ---");
        List<Service> list = serviceDAO.findAllActive();

        System.out.println("-> Tim thay tong cong: " + list.size() + " dich vu Active trong CSDL:");
        for (Service s : list) {
            System.out.println("   + [ID: " + s.getId() + "] " + s.getServiceName()
                    + " | Gia: " + s.getPrice() + " VND | Thoi luong: " + s.getDurationMinutes() + " phut");
        }

        assertNotNull(list, "Danh sách dịch vụ không được null!");
        assertFalse(list.isEmpty(), "CSDL khởi tạo phải có ít nhất 1 dịch vụ!");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Thêm Dịch vụ mới thành công (Admin Insert)")
    public void testInsertService() {
        System.out.println("\n--- [TEST 2] Testing insert() ---");
        Service service = new Service();
        service.setServiceName(testServiceName);
        service.setPrice(new BigDecimal("500000.00"));
        service.setDurationMinutes(60);
        service.setDescription("Mo ta dich vu thu nghiem Spa");
        service.setImageUrl("https://clinic.com/service.jpg");
        service.setStatus(true);

        System.out.println("-> Dang them dich vu moi: " + testServiceName + " | Gia: 500,000 VND");
        boolean inserted = serviceDAO.insert(service);
        System.out.println("-> Ket qua Insert: " + (inserted ? "SUCCESS (Thanh cong)" : "FAILED (That bai)"));

        assertTrue(inserted, "Thêm Dịch vụ mới phải thành công!");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Tìm thông tin dịch vụ theo ID = 1")
    public void testFindById() {
        System.out.println("\n--- [TEST 3] Testing findById(1) ---");
        Service service = serviceDAO.findById(1);

        if (service != null) {
            System.out.println("-> Lay thong tin thanh cong:");
            System.out.println("   + ID: " + service.getId());
            System.out.println("   + Ten Dich Vu: " + service.getServiceName());
            System.out.println("   + Don gia: " + service.getPrice() + " VND");
            System.out.println("   + Trang thai: " + (service.isStatus() ? "Active (Hoat dong)" : "Hidden (An)"));
        }

        assertNotNull(service, "Dịch vụ ID = 1 phải tồn tại!");
        assertEquals(1, service.getId(), "ID trả về phải bằng 1!");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Cập nhật trạng thái Hiển thị/Ẩn Dịch vụ")
    public void testUpdateStatus() {
        System.out.println("\n--- [TEST 4] Testing updateStatus(1, true) ---");
        boolean updated = serviceDAO.updateStatus(1, true);
        System.out.println("-> Ket qua Cap nhat trang thai: " + (updated ? "SUCCESS (Thanh cong)" : "FAILED"));
        assertTrue(updated, "Cập nhật trạng thái Dịch vụ phải trả về true!");
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Edge Case - Tìm Dịch vụ không tồn tại (ID = 999999)")
    public void testFindNonExistentService() {
        System.out.println("\n--- [TEST 5] Testing findById(999999) ---");
        Service service = serviceDAO.findById(999999);
        System.out.println("-> Ket qua tim ID khong ton tai: " + (service == null ? "NULL (Dung ky vong)" : "LOI"));
        assertNull(service, "Dịch vụ không tồn tại bắt buộc phải trả về null!");
        System.out.println("\n========================================================");
        System.out.println("   [SUCCESS] KET THUC BO UNIT TEST SERVICEDAO - PASSED 100%");
        System.out.println("========================================================\n");
    }
}
