package controller;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import dao.MedicalRecordDAO;
import model.Appointment;
import model.DoctorProfile;
import model.DoctorSchedule;
import model.MedicalRecord;
import model.User;

/**
 * /doctor/dashboard - Servlet Quản lý Không gian Làm việc của Bác Sĩ.
 * Tích hợp AJAX 100% chống load lại trang khi thao tác Quản lý Ca Khám.
 *
 * <p><b>TODO [BƯỚC 8a]: Chuyển sang extends BaseRoleServlet</b></p>
 * <p>Thay đổi: {@code extends HttpServlet} → {@code extends BaseRoleServlet}</p>
 *
 * <p><b>Lợi ích:</b></p>
 * <ul>
 *   <li>Xóa đoạn check session 8 dòng ở doGet() và doPost() — thay bằng 1 dòng:
 *       {@code User loginUser = requireRole(request, response, "DOCTOR");}</li>
 *   <li>Thay {@code session.setAttribute(SUCCESS_MESSAGE_ATTR, msg)} bằng
 *       {@code setSuccess(request, msg);}</li>
 *   <li>Thay {@code "XMLHttpRequest".equals(...) || "true".equals(...)} bằng
 *       {@code isAjax(request)}</li>
 *   <li>Thay logic parse page thủ công bằng {@code parsePage(request, "page")}</li>
 * </ul>
 *
 * <p><b>❓ Câu hỏi tự kiểm tra:</b><br>
 * Sau khi DoctorServlet extends BaseRoleServlet, AuthenticationFilter có còn cần
 * kiểm tra role DOCTOR nữa không? Hai cơ chế này có conflict nhau không?</p>
 */
// TODO [BƯỚC 8b]: Đổi "extends HttpServlet" thành "extends BaseRoleServlet"
@WebServlet(name = "DoctorServlet", urlPatterns = {"/doctor/dashboard"})
public class DoctorServlet extends HttpServlet {


    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private final DoctorProfileDAO doctorProfileDAO = new DoctorProfileDAO();
    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SystemConstant.SESSION_USER) : null;

        if (loginUser == null || !"DOCTOR".equalsIgnoreCase(loginUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=/doctor/dashboard");
            return;
        }

        String action = request.getParameter("action");
        String dateParam = request.getParameter("date");
        boolean filterByDate = (dateParam != null && !dateParam.trim().isEmpty() && !"all".equalsIgnoreCase(dateParam));
        String targetDateStr = filterByDate ? dateParam : LocalDate.now().toString();

        DoctorProfile doctorProfile = doctorProfileDAO.findByUserId(loginUser.getId());

        // Nếu là AJAX GET lấy danh sách slot
        if ("get-doctor-slots".equals(action)) {
            response.setContentType("application/json;charset=UTF-8");
            if (doctorProfile != null) {
                try {
                    Date workDate = Date.valueOf(targetDateStr);
                    writeSlotsJson(response, doctorProfile.getId(), workDate, "Tải danh sách ca khám thành công", true);
                    return;
                } catch (Exception ignored) {}
            }
            response.getWriter().write("{\"success\":false,\"message\":\"Không tìm thấy lịch khám\",\"slots\":[]}");
            return;
        }

        List<DoctorSchedule> doctorSchedules = null;
        if (doctorProfile != null) {
            try {
                Date workDate = Date.valueOf(targetDateStr);
                doctorSchedules = doctorScheduleDAO.findSchedulesByDoctorAndDate(doctorProfile.getId(), workDate);
            } catch (Exception ignored) {}
        }

        int page = 1;
        try {
            if (request.getParameter("page") != null) {
                page = Math.max(1, Integer.parseInt(request.getParameter("page")));
            }
        } catch (NumberFormatException ignored) {}

        int pageSize = 5;
        int totalRecords = filterByDate 
                ? appointmentDAO.countAppointmentsByDoctorUserAndDate(loginUser.getId(), dateParam)
                : appointmentDAO.countAppointmentsByDoctorUser(loginUser.getId());

        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
        if (page > totalPages) page = totalPages;
        int offset = (page - 1) * pageSize;

        List<Appointment> appointments = filterByDate
                ? appointmentDAO.findAppointmentsByDoctorUserAndDatePaginated(loginUser.getId(), dateParam, offset, pageSize)
                : appointmentDAO.findAppointmentsByDoctorUserPaginated(loginUser.getId(), offset, pageSize);

        List<Appointment> allDayApps = filterByDate
                ? appointmentDAO.findAppointmentsByDoctorUserAndDate(loginUser.getId(), dateParam)
                : appointmentDAO.findAppointmentsByDoctorUser(loginUser.getId());

        Map<Integer, MedicalRecord> recordsMap = new HashMap<>();
        int completedCount = 0;
        int pendingCount = 0;
        int cancelledCount = 0;

        for (Appointment app : allDayApps) {
            if (SystemConstant.STATUS_COMPLETED.equalsIgnoreCase(app.getStatus())) {
                completedCount++;
            } else if (SystemConstant.STATUS_CANCELLED.equalsIgnoreCase(app.getStatus())) {
                cancelledCount++;
            } else {
                pendingCount++;
            }
        }

        for (Appointment app : appointments) {
            MedicalRecord record = medicalRecordDAO.getRecordByAppointmentId(app.getId());
            if (record != null) {
                recordsMap.put(app.getId(), record);
            }
        }

        request.setAttribute("doctorProfile", doctorProfile);
        request.setAttribute("doctorSchedules", doctorSchedules);
        request.setAttribute("appointments", appointments);
        request.setAttribute("recordsMap", recordsMap);
        request.setAttribute("selectedDate", targetDateStr);
        request.setAttribute("totalCount", allDayApps.size());
        request.setAttribute("completedCount", completedCount);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("cancelledCount", cancelledCount);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/WEB-INF/views/doctor/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SystemConstant.SESSION_USER) : null;

        if (loginUser == null || !"DOCTOR".equalsIgnoreCase(loginUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String dateParam = request.getParameter("date");
        boolean isAjax = "true".equalsIgnoreCase(request.getParameter("ajax")) || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (dateParam == null || dateParam.trim().isEmpty()) {
            dateParam = LocalDate.now().toString();
        }

        DoctorProfile doctorProfile = doctorProfileDAO.findByUserId(loginUser.getId());
        Date workDate = null;
        try {
            workDate = Date.valueOf(dateParam);
        } catch (Exception ignored) {}

        if ("save-diagnosis".equals(action)) {
            handleSaveDiagnosis(request, response, loginUser);
            return;
        }

        String msg = "";
        boolean success = false;

        if ("generate-schedule".equals(action)) {
            if (doctorProfile != null && workDate != null) {
                doctorScheduleDAO.ensureSchedulesExist(doctorProfile.getId(), workDate);
                msg = "Đồng bộ tự động các ca khám thành công cho ngày " + dateParam;
                success = true;
            } else {
                msg = "Không tìm thấy thông tin Bác sĩ hoặc Ngày khám";
            }
        } else if ("toggle-slot".equals(action)) {
            try {
                int slotId = Integer.parseInt(request.getParameter("slotId"));
                boolean newStatus = Boolean.parseBoolean(request.getParameter("status"));
                success = doctorScheduleDAO.updateSlotAvailability(slotId, newStatus);
                msg = success ? "Đã cập nhật trạng thái ca khám #" + slotId : "Không thể cập nhật trạng thái ca khám";
            } catch (Exception e) {
                msg = "Lỗi cập nhật ca khám: " + e.getMessage();
            }
        } else if ("add-slot".equals(action)) {
            if (doctorProfile != null && workDate != null) {
                try {
                    String startTimeStr = request.getParameter("startTime");
                    String endTimeStr = request.getParameter("endTime");
                    if (startTimeStr.length() == 5) startTimeStr += ":00";
                    if (endTimeStr.length() == 5) endTimeStr += ":00";

                    java.sql.Time startTime = java.sql.Time.valueOf(startTimeStr);
                    java.sql.Time endTime = java.sql.Time.valueOf(endTimeStr);

                    success = doctorScheduleDAO.insertSlot(doctorProfile.getId(), workDate, startTime, endTime);
                    msg = success ? ("Đã đăng ký thêm ca khám " + startTimeStr.substring(0, 5) + " - " + endTimeStr.substring(0, 5))
                                  : "Ca khám này đã tồn tại hoặc không thể thêm!";
                } catch (Exception e) {
                    msg = "Lỗi thêm ca khám: " + e.getMessage();
                }
            }
        } else if ("delete-slot".equals(action)) {
            if (doctorProfile != null) {
                try {
                    int slotId = Integer.parseInt(request.getParameter("slotId"));
                    success = doctorScheduleDAO.deleteSlot(slotId, doctorProfile.getId());
                    msg = success ? "Đã xóa ca khám #" + slotId : "Không thể xóa ca khám này (ca đã có bệnh nhân đặt lịch)!";
                } catch (Exception e) {
                    msg = "Lỗi xóa ca khám: " + e.getMessage();
                }
            }
        }

        if (isAjax && doctorProfile != null && workDate != null) {
            writeSlotsJson(response, doctorProfile.getId(), workDate, msg, success);
            return;
        }

        if (success) {
            session.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, msg);
        } else {
            session.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, msg);
        }
        response.sendRedirect(request.getContextPath() + "/doctor/dashboard?tab=schedules&date=" + dateParam);
    }

    private void writeSlotsJson(HttpServletResponse response, int doctorId, Date workDate, String message, boolean success) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        List<DoctorSchedule> slots = doctorScheduleDAO.findSchedulesByDoctorAndDate(doctorId, workDate);
        StringBuilder json = new StringBuilder();
        json.append("{\"success\":").append(success)
            .append(",\"message\":\"").append(message != null ? message.replace("\"", "\\\"") : "").append("\"")
            .append(",\"slots\":[");
        for (int i = 0; i < slots.size(); i++) {
            DoctorSchedule s = slots.get(i);
            String startTimeStr = s.getStartTime() != null ? s.getStartTime().toString().substring(0, 5) : "";
            String endTimeStr = s.getEndTime() != null ? s.getEndTime().toString().substring(0, 5) : "";
            json.append(String.format("{\"id\":%d,\"startTime\":\"%s\",\"endTime\":\"%s\",\"isAvailable\":%b}",
                    s.getId(), startTimeStr, endTimeStr, s.isIsAvailable()));
            if (i < slots.size() - 1) json.append(",");
        }
        json.append("]}");
        response.getWriter().write(json.toString());
    }

    private void handleSaveDiagnosis(HttpServletRequest request, HttpServletResponse response, User doctorUser)
            throws IOException {
        String date = request.getParameter("date");
        try {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
            String diagnosis = request.getParameter("diagnosis");
            String prescription = request.getParameter("prescription");

            Appointment app = appointmentDAO.findById(appointmentId);
            if (app != null) {
                MedicalRecord record = new MedicalRecord();
                record.setAppointmentId(appointmentId);
                record.setPatientId(app.getPatientId());
                record.setDoctorId(app.getDoctorId());
                record.setDiagnosis(diagnosis != null ? diagnosis.trim() : "");
                record.setPrescriptionOrResult(prescription != null ? prescription.trim() : "");

                boolean saved = medicalRecordDAO.saveOrUpdateRecord(record);
                if (saved) {
                    appointmentDAO.updateStatus(appointmentId, SystemConstant.STATUS_COMPLETED);
                    request.getSession().setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR,
                            "Lưu đơn thuốc và chẩn đoán y khoa cho ca khám #" + appointmentId + " thành công!");
                } else {
                    request.getSession().setAttribute(SystemConstant.ERROR_MESSAGE_ATTR,
                            "Không thể lưu hồ sơ bệnh án. Vui lòng thử lại!");
                }
            }
        } catch (Exception e) {
            request.getSession().setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Lỗi dữ liệu: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/doctor/dashboard?date=" + (date != null ? date : LocalDate.now().toString()));
    }
}
