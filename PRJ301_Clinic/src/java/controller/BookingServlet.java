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

import constant.RouterConstant;
import constant.SystemConstant;
import exception.SlotAlreadyBookedException;
import model.Appointment;
import model.DoctorProfile;
import model.Service;
import model.User;
import service.BookingService;
import service.ClinicService;

/**
 * BookingServlet - Điều hướng & Xử lý Đặt Lịch Hẹn Khám Bệnh Nhân (/booking).
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
     * Nạp Giao diện Đặt Lịch Hẹn & Xử lý các action phụ (GET).
     * 1. Nạp trang thanh toán SePay VietQR (nếu action == "payment").
     * 2. Nạp danh sách ca khám dạng JSON (nếu action == "get-slots").
     * 3. Nạp danh sách Dịch vụ và Bác sĩ cho form booking.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Bắt buộc Đăng nhập ngay từ đầu khi vào trang đặt lịch (Cách 2)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SystemConstant.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN + "?redirect=/booking");
            return;
        }

        String action = request.getParameter("action");
        if ("payment".equals(action)) {
            handlePaymentPage(request, response);
            return;
        }
        if ("get-slots".equals(action)) {
            handleGetSlots(request, response);
            return;
        }
        if ("check-payment-status".equals(action)) {
            handleCheckPaymentStatus(request, response);
            return;
        }
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
     * Xử lý Đặt Lịch Hẹn Nguyên Tử chống trùng Slot khi Submit Form (POST).
     * 1. Check Session User (nếu null -> redirect /login)
     * 2. Lấy form data (serviceId, doctorId, scheduleId, appointmentDate, notes)
     * 3. Đóng gói đối tượng Appointment (gán status PENDING, paymentStatus UNPAID)
     * 4. Gọi bookingService.createBookingAtomic(app)
     * 5. Nếu thành công -> redirect sang /booking?action=payment&id=...
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
        User user = (User) session.getAttribute(SystemConstant.SESSION_USER);

        try {
            String serviceIdStr = request.getParameter("serviceId");
            String doctorIdStr = request.getParameter("doctorId");
            String scheduleIdStr = request.getParameter("scheduleId");
            String appointmentDateStr = request.getParameter("appointmentDate");
            String notes = request.getParameter("notes");

            // 2. Fail-fast Validation báo lý do lỗi chi tiết 100%
            if (serviceIdStr == null || serviceIdStr.trim().isEmpty()) {
                request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng chọn Dịch vụ khám / Spa!");
                doGet(request, response);
                return;
            }
            if (doctorIdStr == null || doctorIdStr.trim().isEmpty()) {
                request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng chọn Bác sĩ phụ trách!");
                doGet(request, response);
                return;
            }
            if (appointmentDateStr == null || appointmentDateStr.trim().isEmpty()) {
                request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng chọn Ngày khám mong muốn!");
                doGet(request, response);
                return;
            }
            if (scheduleIdStr == null || scheduleIdStr.trim().isEmpty()) {
                request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng bấm chọn một Ca khám 60 phút khả dụng (nút màu xanh)!");
                doGet(request, response);
                return;
            }

            int serviceId = Integer.parseInt(serviceIdStr);
            int doctorId = Integer.parseInt(doctorIdStr);
            int scheduleId = Integer.parseInt(scheduleIdStr);
            Date appointmentDate = Date.valueOf(appointmentDateStr);

            // Tìm dịch vụ lấy đơn giá
            List<Service> services = clinicService.getActiveServices();
            Service selectedService = null;
            for (Service service : services) {
                if (service.getId() == serviceId) {
                    selectedService = service;
                    break;
                }
            }

            // Nạp thông tin Slot để lấy start_time chính xác
            dao.DoctorScheduleDAO scheduleDAO = new dao.DoctorScheduleDAO();
            model.DoctorSchedule selectedSchedule = scheduleDAO.findById(scheduleId);

            // 3. Lưu thông tin cuộc hẹn
            Appointment app = new Appointment();
            app.setPatientId(user.getId());
            app.setDoctorId(doctorId);
            app.setServiceId(serviceId);
            app.setScheduleId(scheduleId);
            app.setAppointmentDate(appointmentDate);
            app.setStartTime(selectedSchedule != null ? selectedSchedule.getStartTime() : null);
            app.setTotalPrice(selectedService != null ? selectedService.getPrice() : java.math.BigDecimal.ZERO);
            app.setStatus(SystemConstant.STATUS_PENDING);
            app.setPaymentStatus(SystemConstant.PAYMENT_UNPAID);
            app.setPaymentMethod(SystemConstant.METHOD_SEPAY_QR);
            app.setNotes(notes);

            // 4. Gọi service đặt lịch đã kiểm tra chống trùng slot
            boolean success = bookingService.createBookingAtomic(app);

            // 5. Điều hướng sang trang thanh toán Sepay nếu thành công
            if (success) {
                response.sendRedirect(request.getContextPath() + "/booking?action=payment&id=" + app.getId());
            } else {
                request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Khung giờ này vừa được đăng ký thành công bởi bệnh nhân khác. Vui lòng chọn ca rảnh khác!");
                doGet(request, response);
            }
        } catch (SlotAlreadyBookedException e) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Thông tin nhập vào không hợp lệ. Vui lòng kiểm tra lại ngày và ca khám!");
            doGet(request, response);
        }
    }

    /**
     * Hàm hỗ trợ nạp lịch hẹn cho trang thanh toán SePay VietQR.
     */
    private void handlePaymentPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int appointmentId = Integer.parseInt(request.getParameter("id"));
            Appointment appointment = bookingService.getAppointmentById(appointmentId);
            request.setAttribute("appointment", appointment);
            request.getRequestDispatcher(RouterConstant.PAYMENT_JSP).forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_HOME);
        }
    }

    /**
     * Hàm AJAX nạp danh sách slot động dạng JSON.
     */
    private void handleGetSlots(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int doctorId = Integer.parseInt(request.getParameter("doctorId"));
            Date date = Date.valueOf(request.getParameter("date"));
            List<model.DoctorSchedule> slots = clinicService.getSchedules(doctorId, date);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < slots.size(); i++) {
                model.DoctorSchedule s = slots.get(i);
                json.append(String.format("{\"id\":%d,\"startTime\":\"%s\",\"endTime\":\"%s\",\"isAvailable\":%b}",
                        s.getId(), s.getStartTime().toString(), s.getEndTime().toString(), s.isIsAvailable()));
                if (i < slots.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            response.getWriter().write("[]");
        }
    }

    /**
     * Hàm AJAX kiểm tra trạng thái thanh toán VietQR theo thời gian thực.
     */
    private void handleCheckPaymentStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int appointmentId = Integer.parseInt(request.getParameter("id"));
            Appointment app = bookingService.getAppointmentById(appointmentId);
            if (app != null) {
                response.getWriter().write(String.format("{\"id\":%d,\"paymentStatus\":\"%s\",\"status\":\"%s\"}",
                        app.getId(), app.getPaymentStatus(), app.getStatus()));
                return;
            }
        } catch (Exception ignored) {
        }
        response.getWriter().write("{\"id\":0,\"paymentStatus\":\"UNPAID\",\"status\":\"PENDING\"}");
    }
}
