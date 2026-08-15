package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.SystemConstant;
import dao.AppointmentDAO;
import dao.DoctorProfileDAO;
import dao.DoctorScheduleDAO;
import dao.ServiceDAO;
import dao.UserDAO;
import model.Appointment;
import model.DoctorProfile;
import model.DoctorSchedule;
import model.Service;
import model.User;
import util.EmailUtil;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * ReceptionistServlet - Servlet Quản lý Không gian Làm việc của Lễ Tân.
 * Theo dõi ca khám sảnh, tiếp nhận Bệnh nhân check-in, thu tiền mặt & hủy ca khi cần.
 */
@WebServlet(name = "ReceptionistServlet", urlPatterns = {"/receptionist/dashboard"})
public class ReceptionistServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ReceptionistServlet.class.getName());
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final UserDAO userDAO = new UserDAO();
    private final DoctorProfileDAO doctorProfileDAO = new DoctorProfileDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SystemConstant.SESSION_USER) : null;

        if (loginUser == null || !"RECEPTIONIST".equalsIgnoreCase(loginUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=/receptionist/dashboard");
            return;
        }

        String action = request.getParameter("action");

        // ---- AJAX: Lấy danh sách ca khám cho Walk-in Booking ----
        if ("get-slots".equals(action)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            try {
                int doctorId = Integer.parseInt(request.getParameter("doctorId"));
                String dateStr = request.getParameter("date");
                Date workDate = Date.valueOf(dateStr);

                // Dùng findSchedulesByDoctorAndDate — tự động sinh ca nếu chưa có
                // và trả về TẤT CẢ slot (kể cả đã đặt) để lễ tân chủ động xem
                java.util.List<model.DoctorSchedule> slots =
                        doctorScheduleDAO.findSchedulesByDoctorAndDate(doctorId, workDate);

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < slots.size(); i++) {
                    model.DoctorSchedule s = slots.get(i);
                    if (i > 0) json.append(",");
                    json.append("{\"id\":").append(s.getId())
                        .append(",\"startTime\":\"").append(s.getStartTime()).append("\"")
                        .append(",\"endTime\":\"").append(s.getEndTime()).append("\"")
                        .append(",\"isAvailable\":").append(s.isAvailable()).append("}");
                }
                json.append("]");
                response.getWriter().write("{\"success\":true,\"slots\":" + json + "}");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi AJAX get-slots tại ReceptionistServlet", e);
                response.getWriter().write("{\"success\":false,\"message\":\"" + e.getMessage() + "\",\"slots\":[]}");
            }
            return;
        }


        String dateParam = request.getParameter("date");

        boolean filterByDate = (dateParam != null && !dateParam.trim().isEmpty() && !"all".equalsIgnoreCase(dateParam));

        int page = 1;
        try {
            if (request.getParameter("page") != null) {
                page = Math.max(1, Integer.parseInt(request.getParameter("page")));
            }
        } catch (NumberFormatException ignored) {}

        int pageSize = 5;
        int totalRecords = filterByDate 
                ? appointmentDAO.countAllAppointmentsByDate(dateParam)
                : appointmentDAO.countAllAppointments();

        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
        if (page > totalPages) page = totalPages;
        int offset = (page - 1) * pageSize;

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

        request.getRequestDispatcher("/WEB-INF/views/receptionist/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SystemConstant.SESSION_USER) : null;

        if (loginUser == null || !"RECEPTIONIST".equalsIgnoreCase(loginUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String date = request.getParameter("date");
        if (date == null || date.trim().isEmpty()) {
            date = LocalDate.now().toString();
        }

        try {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));

            if ("confirm-checkin".equals(action)) {
                appointmentDAO.updateStatus(appointmentId, SystemConstant.STATUS_CONFIRMED);
                session.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, "Đã tiếp nhận bệnh nhân ca #" + appointmentId + " vào sảnh chờ khám!");
            } else if ("collect-cash".equals(action)) {
                appointmentDAO.updatePayment(appointmentId, SystemConstant.PAYMENT_PAID, SystemConstant.METHOD_CASH);
                appointmentDAO.updateStatus(appointmentId, SystemConstant.STATUS_COMPLETED);
                session.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, "✅ Thu tiền mặt & hoàn tất ca khám #" + appointmentId + " thành công!");
            } else if ("cancel-appointment".equals(action)) {
                Appointment app = appointmentDAO.findById(appointmentId);
                appointmentDAO.updateStatus(appointmentId, SystemConstant.STATUS_CANCELLED);

                if (app != null && SystemConstant.PAYMENT_PAID.equalsIgnoreCase(app.getPaymentStatus())) {
                    appointmentDAO.updatePayment(appointmentId, SystemConstant.PAYMENT_REFUND_PENDING, app.getPaymentMethod());
                    
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
                            app.getTotalPrice()
                        );
                    }
                    session.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, 
                        "Đã hủy cuộc hẹn #" + appointmentId + ". Số tiền đã được đưa vào hàng đợi [CHỜ HOÀN TIỀN] & đã tự động gửi Email cam kết hoàn tiền cho bệnh nhân!");
                } else {
                    session.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, "Đã hủy cuộc hẹn ca #" + appointmentId + "!");
                }
            } else if ("confirm-refund".equals(action)) {
                Appointment app = appointmentDAO.findById(appointmentId);
                if (app != null) {
                    appointmentDAO.updatePayment(appointmentId, SystemConstant.PAYMENT_REFUNDED, app.getPaymentMethod());
                    session.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, "Xác nhận đã hoàn trả 100% tiền thành công cho ca #" + appointmentId + "!");
                }
            } else if ("walk-in-booking".equals(action)) {
                // ---- Đặt lịch tại quầy (Walk-in) ----
                int doctorId       = Integer.parseInt(request.getParameter("wi_doctorId"));
                int serviceId      = Integer.parseInt(request.getParameter("wi_serviceId"));
                int scheduleId     = Integer.parseInt(request.getParameter("wi_scheduleId"));
                String wiDate      = request.getParameter("wi_date");
                String patientName = request.getParameter("wi_patientName").trim();
                String patientPhone= request.getParameter("wi_patientPhone").trim();
                String notes       = request.getParameter("wi_notes");

                // Lấy hoặc tạo tài khoản bệnh nhân walk-in (guest)
                User walkInUser = userDAO.findByPhone(patientPhone);
                if (walkInUser == null) {
                    // Tạo tài khoản tạm thời cho bệnh nhân mới đến quầy
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

                // Lấy giá dịch vụ
                Service svc = serviceDAO.findById(serviceId);
                BigDecimal price = (svc != null) ? svc.getPrice() : BigDecimal.ZERO;

                // Tạo Appointment với status COMPLETED + payment PAID/CASH ngay lập tức
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
                    appointmentDAO.createBookingAtomic(walkin);
                    session.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR,
                        "✅ Đặt lịch tại quầy thành công cho bệnh nhân " + patientName + "! Đã thu tiền mặt.");
                } catch (exception.SlotAlreadyBookedException ex) {
                    session.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "❌ Khung giờ này đã có người đặt. Vui lòng chọn ca khác!");
                }
                date = wiDate;
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi xử lý tại ReceptionistServlet: ", e);
            session.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Đã xảy ra lỗi trong quá trình xử lý. Vui lòng thử lại!");
        }

        response.sendRedirect(request.getContextPath() + "/receptionist/dashboard?date=" + date);
    }
}
