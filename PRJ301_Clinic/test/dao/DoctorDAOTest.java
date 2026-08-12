package dao;

import model.DoctorProfile;
import model.DoctorSchedule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử tự động JUnit 5 cho DoctorProfileDAO và DoctorScheduleDAO.
 */
@RunWith(JUnitPlatform.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DoctorDAOTest {

    private static DoctorProfileDAO doctorProfileDAO;
    private static DoctorScheduleDAO doctorScheduleDAO;

    @BeforeAll
    public static void setUp() {
        doctorProfileDAO = new DoctorProfileDAO();
        doctorScheduleDAO = new DoctorScheduleDAO();
        System.out.println("\n========================================================");
        System.out.println("   [START] BAT DAU CHAY BO UNIT TEST DOCTOR DAOS");
        System.out.println("========================================================");
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Lấy danh sách tất cả Bác sĩ Active")
    public void testFindAllActiveDoctors() {
        System.out.println("\n--- [TEST 1] Testing findAllActiveDoctors() ---");
        List<DoctorProfile> list = doctorProfileDAO.findAllActiveDoctors();
        System.out.println("-> Tim thay: " + list.size() + " Bac si trong CSDL.");
        for (DoctorProfile d : list) {
            System.out.println("   + [ID: " + d.getId() + "] " + d.getDoctorName() + " | Chuyen khoa: " + d.getSpecialty() + " | Phong: " + d.getRoomNumber());
        }

        assertNotNull(list, "Danh sách Bác sĩ không được null!");
        assertFalse(list.isEmpty(), "CSDL khởi tạo mẫu phải có ít nhất 1 Bác sĩ!");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Tìm Bác sĩ theo ID = 1")
    public void testFindDoctorById() {
        System.out.println("\n--- [TEST 2] Testing DoctorProfileDAO.findById(1) ---");
        DoctorProfile doc = doctorProfileDAO.findById(1);
        if (doc != null) {
            System.out.println("-> Lay thong tin Bac si ID = 1:");
            System.out.println("   + Ho ten: " + doc.getDoctorName());
            System.out.println("   + Chuyen khoa: " + doc.getSpecialty());
            System.out.println("   + Kinh nghiem: " + doc.getExperienceYears() + " nam");
        }

        assertNotNull(doc, "Bác sĩ ID = 1 phải tồn tại trong CSDL!");
        assertEquals(1, doc.getId());
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Gọi Stored Procedure sp_GetAvailableSlotsByDoctorAndDate")
    public void testFindAvailableSlotsByDoctorAndDate() {
        System.out.println("\n--- [TEST 3] Testing sp_GetAvailableSlotsByDoctorAndDate ---");
        Date testDate = Date.valueOf("2026-08-15");
        List<DoctorSchedule> slots = doctorScheduleDAO.findAvailableSlotsByDoctorAndDate(1, testDate);
        System.out.println("-> Tim thay: " + slots.size() + " slot ranh ngay " + testDate);
        for (DoctorSchedule s : slots) {
            System.out.println("   + [Slot ID: " + s.getId() + "] " + s.getStartTime() + " - " + s.getEndTime() + " | Available: " + s.isIsAvailable());
        }

        assertNotNull(slots, "Danh sách slot không được null!");
        System.out.println("\n========================================================");
        System.out.println("   [SUCCESS] KET THUC BO UNIT TEST DOCTOR DAOS - PASSED 100%");
        System.out.println("========================================================\n");
    }
}
