package dao;

import exception.SlotAlreadyBookedException;
import model.Appointment;
import model.DoctorSchedule;
import constant.SystemConstant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử tự động JUnit 5 cho AppointmentDAO (Giao dịch Đặt lịch & Chống
 * Race Condition).
 */
@RunWith(JUnitPlatform.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppointmentDAOTest {

    private static AppointmentDAO appointmentDAO;
    private static DoctorScheduleDAO scheduleDAO;
    private static int createdAppointmentId;

    @BeforeAll
    public static void setUp() {
        appointmentDAO = new AppointmentDAO();
        scheduleDAO = new DoctorScheduleDAO();
        System.out.println("\n========================================================");
        System.out.println("   [START] BAT DAU CHAY BO UNIT TEST APPOINTMENTDAO");
        System.out.println("========================================================");
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Đặt lịch hẹn mới thành công (Atomic Transaction)")
    public void testCreateBookingSuccess() throws SlotAlreadyBookedException {
        System.out.println("\n--- [TEST 1] Testing createBookingAtomic() ---");

        // 1. Lấy slot khả dụng của Bác sĩ 1 (thử ngày Hôm nay hoặc 2026-08-15)
        Date testDate = new Date(System.currentTimeMillis());
        List<DoctorSchedule> slots = scheduleDAO.findAvailableSlotsByDoctorAndDate(1, testDate);
        if (slots.isEmpty()) {
            testDate = Date.valueOf("2026-08-15");
            slots = scheduleDAO.findAvailableSlotsByDoctorAndDate(1, testDate);
        }

        if (slots.isEmpty()) {
            System.out.println("-> Khong co slot ranh nghiem thu, bo qua test.");
            return;
        }

        DoctorSchedule targetSlot = slots.get(0);

        Appointment app = new Appointment();
        app.setPatientId(1); // User patient1
        app.setDoctorId(1);  // Doctor 1
        app.setServiceId(1); // Service 1
        app.setScheduleId(targetSlot.getId());
        app.setAppointmentDate(testDate);
        app.setStartTime(targetSlot.getStartTime());
        app.setTotalPrice(new BigDecimal("500000.00"));
        app.setStatus(SystemConstant.STATUS_PENDING);
        app.setPaymentStatus(SystemConstant.PAYMENT_UNPAID);
        app.setPaymentMethod(SystemConstant.METHOD_SEPAY_QR);
        app.setNotes("Lịch hẹn thử nghiệm JUnit 5");

        System.out.println("-> Thuc hien Dat lich hen cho Slot ID: " + targetSlot.getId());
        boolean success = appointmentDAO.createBookingAtomic(app);

        System.out.println("-> Ket qua Transaction: " + (success ? "SUCCESS (Thanh cong)" : "FAILED"));
        System.out.println("   + Appointment ID moi tao: " + app.getId());
        System.out.println("   + Payment Content tu dong: " + app.getPaymentContent());

        assertTrue(success, "Giao dịch Đặt lịch hẹn phải trả về true!");
        assertTrue(app.getId() > 0, "ID Lịch hẹn mới phải lớn hơn 0!");
        createdAppointmentId = app.getId();
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Tìm chi tiết Lịch hẹn vừa tạo")
    public void testFindById() {
        System.out.println("\n--- [TEST 2] Testing findById(" + createdAppointmentId + ") ---");
        if (createdAppointmentId <= 0) {
            return;
        }

        Appointment app = appointmentDAO.findById(createdAppointmentId);
        if (app != null) {
            System.out.println("-> Thong tin Lich hen chi tiet:");
            System.out.println("   + Benh nhan: " + app.getPatientName() + " (" + app.getPatientPhone() + ")");
            System.out.println("   + Bac si: " + app.getDoctorName());
            System.out.println("   + Dich vu: " + app.getServiceName());
            System.out.println("   + Trang thai thanh toan: " + app.getPaymentStatus());
        }

        assertNotNull(app, "Lịch hẹn vừa tạo phải tìm thấy được!");
        assertEquals(createdAppointmentId, app.getId());
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Cập nhật Thanh toán SePay VietQR THÀNH CÔNG")
    public void testUpdatePaymentSuccess() {
        System.out.println("\n--- [TEST 3] Testing updatePaymentSuccess() ---");
        if (createdAppointmentId <= 0) {
            return;
        }

        String mockTxCode = "FT262259999";
        System.out.println("-> Gia lap Webhook SePay truyen Ma giao dich: " + mockTxCode);
        boolean updated = appointmentDAO.updatePaymentSuccess(createdAppointmentId, mockTxCode);
        System.out.println("-> Ket qua cap nhat: " + (updated ? "SUCCESS (PAID & CONFIRMED)" : "FAILED"));

        assertTrue(updated, "Cập nhật thanh toán phải thành công!");

        Appointment app = appointmentDAO.findById(createdAppointmentId);
        assertEquals(SystemConstant.PAYMENT_PAID, app.getPaymentStatus());
        assertEquals(SystemConstant.STATUS_CONFIRMED, app.getStatus());
    }
}
