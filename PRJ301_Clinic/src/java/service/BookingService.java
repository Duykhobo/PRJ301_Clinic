package service;

import java.sql.Date;
import java.util.List;
import dao.AppointmentDAO;
import exception.SlotAlreadyBookedException;
import model.Appointment;

/**
 * BookingService - Tầng Service Quản lý Đặt lịch hẹn & Thanh toán (Business Logic).
 */
public class BookingService {

    private final AppointmentDAO appointmentDAO;

    public BookingService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    public BookingService(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    /**
     * Đặt Lịch Hẹn Nguyên Tử (Chống trùng slot giờ khám).
     */
    public boolean createBookingAtomic(Appointment app) throws SlotAlreadyBookedException {
        return appointmentDAO.createBookingAtomic(app);
    }

    /**
     * Lấy chi tiết Đặt Lịch Hẹn theo ID.
     */
    public Appointment getAppointmentById(int id) {
        return appointmentDAO.findById(id);
    }

    /**
     * Lấy danh sách Lịch Sử Đặt Hẹn của Bệnh Nhân.
     */
    public List<Appointment> getPatientAppointmentHistory(int patientId) {
        return appointmentDAO.findByPatientId(patientId);
    }

    /**
     * Cập nhật Thanh Toán Thành Công (Tự động VietQR SePay).
     */
    public boolean updatePaymentSuccess(int appointmentId, String transactionCode) {
        return appointmentDAO.updatePaymentSuccess(appointmentId, transactionCode);
    }
}
