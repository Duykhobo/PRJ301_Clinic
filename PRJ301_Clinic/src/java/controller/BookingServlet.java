package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.RouterConstant;
import constant.SystemConstant;
import model.DoctorProfile;
import model.Service;
import service.BookingService;
import service.ClinicService;

/**
 * TODO: BookingServlet - Điều hướng & Xử lý Đặt Lịch Hẹn Khám Bệnh Nhân
 * (/booking).
 * Chuẩn mô hình Enterprise 3-Tier (Servlet -> Service -> DAO).
 */
@WebServlet(name = "BookingServlet", urlPatterns = { "/booking" })
public class BookingServlet extends HttpServlet {

    private ClinicService clinicService;
    private BookingService bookingService;

    @Override
    public void init() throws ServletException {
        this.clinicService = new ClinicService();
        this.bookingService = new BookingService();
    }

    /**
     * TODO 1: Nạp Giao diện Đặt Lịch Hẹn (GET)
     * Gợi ý Flow 3 Tầng:
     * 1. Check param action=="payment" -> chuyển hướng xử lý trang VietQR.
     * 2. Gọi clinicService.getActiveServices() lấy danh sách Dịch vụ.
     * 3. Gọi clinicService.getAllDoctors() lấy danh sách Bác sĩ.
     * 4. Gán request.setAttribute("services", services) và ("doctors", doctors).
     * 5. Forward sang RouterConstant.BOOKING_JSP.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Lấy danh sách Dịch vụ Nha khoa & Spa hoạt động từ ClinicService
        List<Service> services = clinicService.getActiveServices();
        // 2. Lấy danh sách Bác sĩ từ ClinicService
        List<DoctorProfile> doctors = clinicService.getAllDoctors();
        // 3. Đẩy 2 danh sách vào Request Attributes
        request.setAttribute("services", services);
        request.setAttribute("doctors", doctors);
        // 4. Chuyển hướng sang giao diện booking.jsp
        request.getRequestDispatcher(RouterConstant.BOOKING_JSP).forward(request, response);
    }

    /**
     * TODO 2: Xử lý Đặt Lịch Hẹn Nguyên Tử chống trùng Slot khi Submit Form (POST)
     * Quy trình 5 bước:
     * Bước 1: Check Session User (nếu null -> redirect /login)
     * Bước 2: Lấy form data (serviceId, doctorId, scheduleId, appointmentDate,
     * notes)
     * Bước 3: Đóng gói đối tượng Appointment (gán status PENDING, paymentStatus
     * UNPAID)
     * Bước 4: Gọi bookingService.createBookingAtomic(app)
     * Bước 5: Nếu thành công -> redirect sang /booking?action=payment&id=...
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Check xem bệnh nhân đã đăng nhập chưa
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SystemConstant.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN + "?redirect=/booking");
            return;
        }

    }
}
