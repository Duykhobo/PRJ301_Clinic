package controller;

import java.io.IOException;
import java.math.BigDecimal;
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
import dao.DoctorScheduleDAO;
import exception.SlotAlreadyBookedException;
import model.Appointment;
import model.DoctorProfile;
import model.DoctorSchedule;
import model.Service;
import model.User;
import service.BookingService;
import service.ClinicService;
import util.EmailUtil;

/**
 * BookingServlet - Điều hướng & Xử lý Đặt Lịch Hẹn Khám Bệnh Nhân (/booking).
 * Chuẩn mô hình Enterprise 3-Tier (Servlet -> Service -> DAO).
 */
@WebServlet(name = "BookingServlet", urlPatterns = { "/booking" })
public class BookingServlet extends HttpServlet {

    private ClinicService clinicService;
    private BookingService bookingService;
    private DoctorScheduleDAO scheduleDAO;

    @Override
    public void init() throws ServletException {
        this.clinicService = new ClinicService();
        this.bookingService = new BookingService();
        this.scheduleDAO = new DoctorScheduleDAO();
    }

    /**
     * Nạp Giao diện Đặt Lịch Hẹn & Xử lý các action phụ (GET).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

        List<Service> services = clinicService.getActiveServices();
        List<DoctorProfile> doctors = clinicService.getAllDoctors();
        request.setAttribute("services", services);
        request.setAttribute("doctors", doctors);
        request.getRequestDispatcher(RouterConstant.BOOKING_JSP).forward(request, response);
    }

    /**
     * Xử lý Đặt Lịch Hẹn Nguyên Tử chống trùng Slot khi Submit Form (POST).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

            // ✅ REFACTOR CLEAN CODE: Gọi 1 dòng ValidationUtil duy nhất!
            util.ValidationUtil.validateBookingParams(doctorIdStr, serviceIdStr, scheduleIdStr, appointmentDateStr);

            int serviceId = Integer.parseInt(serviceIdStr);
            int doctorId = Integer.parseInt(doctorIdStr);
            int scheduleId = Integer.parseInt(scheduleIdStr);
            Date appointmentDate = Date.valueOf(appointmentDateStr);

            List<Service> services = clinicService.getActiveServices();
            Service selectedService = null;
            for (Service s : services) {
                if (s.getId() == serviceId) {
                    selectedService = s;
                    break;
                }
            }

            DoctorSchedule selectedSchedule = scheduleDAO.findById(scheduleId);

            Appointment app = new Appointment();
            app.setPatientId(user.getId());
            app.setDoctorId(doctorId);
            app.setServiceId(serviceId);
            app.setScheduleId(scheduleId);
            app.setAppointmentDate(appointmentDate);
            app.setStartTime(selectedSchedule != null ? selectedSchedule.getStartTime() : null);
            app.setTotalPrice(selectedService != null ? selectedService.getPrice() : BigDecimal.ZERO);
            app.setStatus(SystemConstant.STATUS_PENDING);
            app.setPaymentStatus(SystemConstant.PAYMENT_UNPAID);
            app.setPaymentMethod(SystemConstant.METHOD_SEPAY_QR);
            app.setNotes(notes);

            // =========================================================================
            // TODO: BÀI TẬP CỘT MỐC 4 - TRIỂN KHAI CONTROLLER LAYER (BOOKING SERVLET)
            //
            // 1. Parse các parameter: serviceId, doctorId, scheduleId, appointmentDate,
            // notes
            // 2. Validate dữ liệu đầu vào. Nếu thiếu -> nạp message lỗi và return
            // 3. Khởi tạo đối tượng Appointment và set thông tin
            // 4. Gọi bookingService.createBookingAtomic(app)
            // 5. Nếu thành công -> gửi mail async và chuyển hướng tới trang thanh toán
            // 6. Bắt lỗi SlotAlreadyBookedException -> set message lỗi và trả về view
            // =========================================================================

            boolean success = bookingService.createBookingAtomic(app);

            if (success) {
                EmailUtil.sendBookingConfirmationAsync(user.getEmail(), user.getFullname(),
                        app.getDoctorName() != null ? app.getDoctorName() : "Bác sĩ chuyên khoa",
                        selectedService != null ? selectedService.getServiceName() : "Dịch vụ khám",
                        app.getAppointmentDate(), app.getStartTime(), app.getTotalPrice());

                response.sendRedirect(request.getContextPath() + "/booking?action=payment&id=" + app.getId());
            } else {
                request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR,
                        "Khung giờ này vừa được đăng ký thành công bởi bệnh nhân khác. Vui lòng chọn ca rảnh khác!");
                doGet(request, response);
            }
        } catch (SlotAlreadyBookedException e) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR,
                    "Thông tin nhập vào không hợp lệ. Vui lòng kiểm tra lại ngày và ca khám!");
            doGet(request, response);
        }
    }

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

    private void handleGetSlots(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int doctorId = Integer.parseInt(request.getParameter("doctorId"));
            String dateStr = request.getParameter("date");
            Date date = null;
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                dateStr = dateStr.trim();
                if (dateStr.contains("/")) {
                    String[] parts = dateStr.split("/");
                    if (parts.length == 3) {
                        if (parts[0].length() == 4) { // yyyy/mm/dd
                            dateStr = parts[0] + "-" + String.format("%02d", Integer.parseInt(parts[1])) + "-" + String.format("%02d", Integer.parseInt(parts[2]));
                        } else { // dd/mm/yyyy
                            dateStr = parts[2] + "-" + String.format("%02d", Integer.parseInt(parts[1])) + "-" + String.format("%02d", Integer.parseInt(parts[0]));
                        }
                    }
                }
                date = Date.valueOf(dateStr);
            }

            List<DoctorSchedule> slots = (date != null && doctorId > 0)
                    ? clinicService.getSchedules(doctorId, date)
                    : new java.util.ArrayList<>();

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < slots.size(); i++) {
                DoctorSchedule s = slots.get(i);
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
