package controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.MessageConstant;
import constant.RouterConstant;
import constant.SystemConstant;
import model.Appointment;
import model.DoctorProfile;
import model.Service;
import model.User;
import service.BookingService;
import service.ClinicService;

/**
 * TODO: BookingServlet - Điều hướng & Xử lý Đặt Lịch Hẹn Khám Bệnh Nhân (/booking).
 * Chuẩn mô hình Enterprise 3-Tier (Servlet -> Service -> DAO).
 */
@WebServlet(name = "BookingServlet", urlPatterns = {"/booking"})
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
     * 1. List<Service> services = clinicService.getActiveServices();
     * 2. List<DoctorProfile> doctors = clinicService.getAllDoctors();
     * 3. request.setAttribute("services", services);
     * 4. request.setAttribute("doctors", doctors);
     * 5. request.getRequestDispatcher(RouterConstant.BOOKING_JSP).forward(request, response);
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // =====================================================================
        // TODO: BẠN TỰ THỰC HÀNH GÕ CODE NẠP DỮ LIỆU ĐẶT LỊCH (GET) TẠI ĐÂY!
        // =====================================================================

        request.getRequestDispatcher(RouterConstant.BOOKING_JSP).forward(request, response);
    }

    /**
     * TODO 2: Xử lý Đặt Lịch Hẹn Nguyên Tử chống trùng Slot khi Submit Form (POST)
     * Quy trình 5 bước:
     * Bước 1: Check Session User (nếu null -> redirect /login)
     * Bước 2: Lấy form data (serviceId, doctorProfileId, appointmentDate, scheduleId, notes)
     * Bước 3: Validate dữ liệu (check null, parse int/date)
     * Bước 4: Gọi bookingService.createBookingAtomic(patientId, doctorProfileId, serviceId, scheduleId, appointmentDate, notes)
     * Bước 5: Nếu thành công -> redirect sang trang thanh toán /booking?action=payment&id=...
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // =====================================================================
        // TODO: BẠN TỰ THỰC HÀNH GÕ CODE XỬ LÝ ĐẶT LỊCH (POST) TẠI ĐÂY!
        // =====================================================================
    }
}
