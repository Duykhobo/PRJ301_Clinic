package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.RoleConstant;
import constant.RouterConstant;
import constant.SystemConstant;
import dao.*;
import exception.SlotAlreadyBookedException;
import model.Appointment;
import model.DoctorSchedule;
import model.Service;
import model.User;
import util.EmailUtil;
import util.PaginationUtil;

/**
 * ReceptionistServlet - Điều hướng sảnh chờ và Live Check-in tiếp đón bệnh nhân.
 * Hỗ trợ Phân trang, Live Search và Quản lý Doanh thu Tiền mặt / SePay.
 */
@WebServlet(name = "ReceptionistServlet", urlPatterns = { "/receptionist/dashboard" })
public class ReceptionistServlet extends BaseRoleServlet {

    private static final Logger LOGGER = Logger.getLogger(ReceptionistServlet.class.getName());
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final DoctorProfileDAO doctorProfileDAO = new DoctorProfileDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User loginUser = requireRole(request, response, RoleConstant.RECEPTIONIST);
        if (loginUser == null) {
            return;
        }

        String action = request.getParameter("action");

        // ---- AJAX: Lấy danh sách ca khám cho Walk-in Booking ----
        if ("get-slots".equals(action)) {
            try {
                int doctorId = Integer.parseInt(request.getParameter("doctorId"));
                String dateStr = request.getParameter("date");
                Date workDate = Date.valueOf(dateStr);

                List<DoctorSchedule> slots = doctorScheduleDAO.findSchedulesByDoctorAndDate(doctorId,
                        workDate);

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < slots.size(); i++) {
                    DoctorSchedule s = slots.get(i);
                    if (i > 0)
                        json.append(",");
                    json.append("{\"id\":").append(s.getId())
                            .append(",\"startTime\":\"").append(s.getStartTime()).append("\"")
                            .append(",\"endTime\":\"").append(s.getEndTime()).append("\"")
                            .append(",\"isAvailable\":").append(s.isAvailable()).append("}");
                }
                json.append("]");

                // 2. Dùng hàm writeJson từ lớp cha
                writeJson(response, "{\"success\":true,\"slots\":" + json + "}");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi AJAX get-slots tại ReceptionistServlet", e);
                writeJson(response, "{\"success\":false,\"message\":\"" + e.getMessage() + "\",\"slots\":[]}");
            }
            return;
        }

        String dateParam = request.getParameter("date");
        boolean filterByDate = (dateParam != null && !dateParam.trim().isEmpty() && !"all".equalsIgnoreCase(dateParam));

        // 3. Sử dụng PaginationUtil để dọn dẹp logic phân trang
        int page = PaginationUtil.parsePage(request, "page");
        int pageSize = 5;
        int totalRecords = filterByDate
                ? appointmentDAO.countAllAppointmentsByDate(dateParam)
                : appointmentDAO.countAllAppointments();

        int totalPages = PaginationUtil.totalPages(totalRecords, pageSize);
        page = Math.min(page, totalPages);
        int offset = PaginationUtil.offset(page, pageSize);

        List<Appointment> appointments = filterByDate
                ? appointmentDAO.findAllAppointmentsByDatePaginated(dateParam, offset, pageSize)
                : appointmentDAO.findAllAppointmentsPaginated(offset, pageSize);

        List<Appointment> allDayApps = filterByDate
                ? appointmentDAO.findAllAppointmentsByDate(dateParam)
                : appointmentDAO.findAllAppointments();

        int totalCount = allDayApps.size();
        int paidCount = 0;
        int cashUnpaidCount = 0;
        int completedCount = 0;

        for (Appointment app : allDayApps) {
            if (SystemConstant.PAYMENT_PAID.equalsIgnoreCase(app.getPaymentStatus())) {
                paidCount++;
            } else {
                cashUnpaidCount++;
            }
            if (SystemConstant.STATUS_COMPLETED.equalsIgnoreCase(app.getStatus())) {
                completedCount++;
            }
        }

        request.setAttribute("appointments", appointments);
        request.setAttribute("selectedDate", dateParam);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("paidCount", paidCount);
        request.setAttribute("cashUnpaidCount", cashUnpaidCount);
        request.setAttribute("completedCount", completedCount);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        // Walk-in booking: load danh sách bác sĩ và dịch vụ
        request.setAttribute("allDoctors", doctorProfileDAO.findAllActiveDoctors());
        request.setAttribute("allServices", serviceDAO.findAllActive());

        request.getRequestDispatcher(RouterConstant.RECEPTIONIST_DASHBOARD_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Phân quyền doPost bằng 1 dòng
        User loginUser = requireRole(request, response, RoleConstant.RECEPTIONIST);
        if (loginUser == null)
            return;

        String action = request.getParameter("action");
        String date = request.getParameter("date");
        if (date == null || date.trim().isEmpty()) {
            date = LocalDate.now().toString();
        }

        try {
            int appointmentId = request.getParameter("appointmentId") != null
                    ? Integer.parseInt(request.getParameter("appointmentId"))
                    : 0;

            if ("confirm-checkin".equals(action)) {
                appointmentDAO.updateStatus(appointmentId, SystemConstant.STATUS_CONFIRMED);
                Appointment app = appointmentDAO.findById(appointmentId);
                if (app != null) {
                    NotificationDAO.pushNotification(app.getPatientId(), "Đã tiếp nhận vào sảnh",
                            "Bạn đã được tiếp nhận vào sảnh chờ khám cho ca #" + appointmentId + ". Bác sĩ sẽ gọi tên bạn sớm.",
                            "APPOINTMENT", "history");
                    try {
                        model.DoctorProfile dp = doctorProfileDAO.findById(app.getDoctorId());
                        if (dp != null && dp.getUserId() > 0) {
                            NotificationDAO.pushNotification(dp.getUserId(), "Bệnh nhân đã vào sảnh",
                                    "Bệnh nhân ca #" + appointmentId + " (" + (app.getStartTime() != null ? app.getStartTime() : "") + ") đã check-in vào sảnh chờ khám.",
                                    "APPOINTMENT", "doctor/dashboard?tab=appointments");
                        }
                    } catch (Exception ignored) {}
                }
                setSuccess(request, "Đã tiếp nhận bệnh nhân ca #" + appointmentId + " vào sảnh chờ khám!");

            } else if ("collect-cash".equals(action)) {
                appointmentDAO.updatePayment(appointmentId, SystemConstant.PAYMENT_PAID, SystemConstant.METHOD_CASH);
                Appointment currentApp = appointmentDAO.findById(appointmentId);
                if (currentApp != null && SystemConstant.STATUS_PENDING.equalsIgnoreCase(currentApp.getStatus())) {
                    appointmentDAO.updateStatus(appointmentId, SystemConstant.STATUS_CONFIRMED);
                    NotificationDAO.pushNotification(currentApp.getPatientId(), "Đã tiếp nhận vào sảnh & thu tiền",
                            "Đã thu tiền mặt & tiếp nhận bạn vào sảnh chờ khám cho ca #" + appointmentId + ".",
                            "APPOINTMENT", "history");
                    try {
                        model.DoctorProfile dp = doctorProfileDAO.findById(currentApp.getDoctorId());
                        if (dp != null && dp.getUserId() > 0) {
                            NotificationDAO.pushNotification(dp.getUserId(), "Bệnh nhân đã vào sảnh",
                                    "Bệnh nhân ca #" + appointmentId + " đã hoàn tất thủ tục và vào sảnh chờ khám.",
                                    "APPOINTMENT", "doctor/dashboard?tab=appointments");
                        }
                    } catch (Exception ignored) {}
                    setSuccess(request, "✅ Đã thu tiền mặt & tiếp nhận bệnh nhân ca #" + appointmentId + " vào sảnh chờ khám!");
                } else {
                    setSuccess(request, "✅ Đã thu tiền mặt & cập nhật thanh toán ca #" + appointmentId + " thành công!");
                }

            } else if ("cancel-appointment".equals(action)) {
                Appointment app = appointmentDAO.findById(appointmentId);
                appointmentDAO.updateStatus(appointmentId, SystemConstant.STATUS_CANCELLED);

                if (app != null && SystemConstant.PAYMENT_PAID.equalsIgnoreCase(app.getPaymentStatus())) {
                    appointmentDAO.updatePayment(appointmentId, SystemConstant.PAYMENT_REFUND_PENDING,
                            app.getPaymentMethod());

                    // Gửi Email thông báo hoàn tiền tới bệnh nhân
                    User patient = userDAO.findById(app.getPatientId());
                    if (patient != null && patient.getEmail() != null) {
                        EmailUtil.sendRefundNotificationAsync(
                                patient.getEmail(),
                                patient.getFullname(),
                                app.getId(),
                                app.getDoctorName(),
                                app.getServiceName(),
                                app.getAppointmentDate() != null ? app.getAppointmentDate().toString() : "",
                                app.getStartTime() != null ? app.getStartTime().toString() : "",
                                app.getTotalPrice());
                    }
                    setSuccess(request, "Đã hủy cuộc hẹn #" + appointmentId
                            + ". Số tiền đã được đưa vào hàng đợi [CHỜ HOÀN TIỀN] & đã gửi Email thông báo!");
                } else {
                    setSuccess(request, "Đã hủy cuộc hẹn ca #" + appointmentId + "!");
                }

            } else if ("confirm-refund".equals(action)) {
                Appointment app = appointmentDAO.findById(appointmentId);
                if (app != null) {
                    appointmentDAO.updatePayment(appointmentId, SystemConstant.PAYMENT_REFUNDED,
                            app.getPaymentMethod());
                    setSuccess(request, "Xác nhận đã hoàn trả 100% tiền thành công cho ca #" + appointmentId + "!");
                }

            } else if ("walk-in-booking".equals(action)) {
                // ---- Đặt lịch tại quầy (Walk-in) ----
                int doctorId = Integer.parseInt(request.getParameter("wi_doctorId"));
                int serviceId = Integer.parseInt(request.getParameter("wi_serviceId"));
                int scheduleId = Integer.parseInt(request.getParameter("wi_scheduleId"));
                String wiDate = request.getParameter("wi_date");
                String patientName = request.getParameter("wi_patientName").trim();
                String patientPhone = request.getParameter("wi_patientPhone").trim();
                String notes = request.getParameter("wi_notes");

                User walkInUser = userDAO.findByPhone(patientPhone);
                if (walkInUser == null) {
                    walkInUser = new User();
                    walkInUser.setFullname(patientName);
                    walkInUser.setPhone(patientPhone);
                    walkInUser.setEmail(patientPhone + "@walkin.clinic");
                    walkInUser.setRole("PATIENT");
                    walkInUser.setStatus(true);
                    walkInUser.setPassword("WALKIN_" + System.currentTimeMillis());

                    int newUserId = userDAO.insertAndGetId(walkInUser);
                    walkInUser.setId(newUserId);
                }

                Service svc = serviceDAO.findById(serviceId);
                BigDecimal price = (svc != null) ? svc.getPrice() : BigDecimal.ZERO;

                Appointment walkin = new Appointment();
                walkin.setPatientId(walkInUser.getId());
                walkin.setDoctorId(doctorId);
                walkin.setServiceId(serviceId);
                walkin.setScheduleId(scheduleId);
                walkin.setAppointmentDate(Date.valueOf(LocalDate.parse(wiDate)));
                walkin.setTotalPrice(price);
                walkin.setStatus(SystemConstant.STATUS_CONFIRMED);
                walkin.setPaymentStatus(SystemConstant.PAYMENT_PAID);
                walkin.setPaymentMethod(SystemConstant.METHOD_CASH);
                walkin.setNotes(notes != null ? notes : "[Đặt tại quầy - Lễ Tân]");

                try {
                    // Tận dụng hoàn hảo hàm Atomic ở Giai đoạn 2
                    appointmentDAO.createBookingAtomic(walkin);
                    setSuccess(request,
                            "✅ Đặt lịch tại quầy thành công cho bệnh nhân " + patientName + "! Đã thu tiền mặt.");
                } catch (SlotAlreadyBookedException ex) {
                    setError(request, "❌ Khung giờ này đã có người đặt. Vui lòng chọn ca khác!");
                }
                date = wiDate;
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi xử lý tại ReceptionistServlet: ", e);
            setError(request, "Đã xảy ra lỗi trong quá trình xử lý. Vui lòng thử lại!");
        }

        response.sendRedirect(request.getContextPath() + RouterConstant.DASHBOARD_RECEPTIONIST + "?date=" + date);
    }
}