package controller;

import java.io.IOException;
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
import dao.MedicalRecordDAO;
import model.Appointment;
import model.MedicalRecord;
import model.User;

/**
/doctor/dashboard - Servlet Quản lý Không gian Làm việc của Bác Sĩ.
 * Cho phép Bác sĩ xem lịch khám trong ngày, thực hiện khám bệnh, chẩn đoán & kê đơn thuốc.
 */
@WebServlet(name = "DoctorServlet", urlPatterns = {"/doctor/dashboard"})
public class DoctorServlet extends HttpServlet {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SystemConstant.SESSION_USER) : null;

        if (loginUser == null || !"DOCTOR".equalsIgnoreCase(loginUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=/doctor/dashboard");
            return;
        }

        String dateParam = request.getParameter("date");
        if (dateParam == null || dateParam.trim().isEmpty()) {
            dateParam = LocalDate.now().toString();
        }

        int page = 1;
        try {
            if (request.getParameter("page") != null) {
                page = Math.max(1, Integer.parseInt(request.getParameter("page")));
            }
        } catch (NumberFormatException ignored) {}

        int pageSize = 5;
        int totalRecords = appointmentDAO.countAppointmentsByDoctorUserAndDate(loginUser.getId(), dateParam);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
        if (page > totalPages) page = totalPages;
        int offset = (page - 1) * pageSize;

        List<Appointment> appointments = appointmentDAO.findAppointmentsByDoctorUserAndDatePaginated(loginUser.getId(), dateParam, offset, pageSize);
        List<Appointment> allDayApps = appointmentDAO.findAppointmentsByDoctorUserAndDate(loginUser.getId(), dateParam);

        // Map lưu Hồ sơ bệnh án theo mã Cuộc hẹn
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

        request.setAttribute("appointments", appointments);
        request.setAttribute("recordsMap", recordsMap);
        request.setAttribute("selectedDate", dateParam);
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

        if ("save-diagnosis".equals(action)) {
            handleSaveDiagnosis(request, response, loginUser);
        } else {
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
        }
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
                    // Tự động chuyển trạng thái cuộc hẹn sang COMPLETED (Đã khám xong)
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
