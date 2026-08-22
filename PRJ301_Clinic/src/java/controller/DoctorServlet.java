package controller;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.RoleConstant;
import constant.RouterConstant;
import constant.SystemConstant;
import dao.AppointmentDAO;
import dao.DoctorProfileDAO;
import dao.DoctorScheduleDAO;
import dao.MedicalRecordDAO;
import dao.NotificationDAO;
import dao.TreatmentPackageDAO;
import dao.UserDAO;
import model.*;
import util.EmailUtil;
import util.PaginationUtil;

@WebServlet(name = "DoctorServlet", urlPatterns = { "/doctor/dashboard" })
public class DoctorServlet extends BaseRoleServlet {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private final DoctorProfileDAO doctorProfileDAO = new DoctorProfileDAO();
    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();
    private final UserDAO userDAO = new UserDAO();
    private final TreatmentPackageDAO treatmentPackageDAO = new TreatmentPackageDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Dùng requireRole thay vì check session thủ công
        User loginUser = requireRole(request, response, RoleConstant.DOCTOR);
        if (loginUser == null)
            return;

        String action = request.getParameter("action");
        String dateParam = request.getParameter("date");
        boolean filterByDate = (dateParam != null && !dateParam.trim().isEmpty() && !"all".equalsIgnoreCase(dateParam));
        String targetDateStr = filterByDate ? dateParam : LocalDate.now().toString();

        DoctorProfile doctorProfile = doctorProfileDAO.findByUserId(loginUser.getId());

        // 2. Tối ưu trả về JSON cho AJAX
        if ("get-doctor-slots".equals(action)) {
            if (doctorProfile != null) {
                try {
                    Date workDate = Date.valueOf(targetDateStr);
                    writeSlotsJson(response, doctorProfile.getId(), workDate, "Tải danh sách ca khám thành công", true);
                    return;
                } catch (Exception ignored) {
                }
            }
            writeJson(response, "{\"success\":false,\"message\":\"Không tìm thấy lịch khám\",\"slots\":[]}");
            return;
        }

        List<DoctorSchedule> doctorSchedules = null;
        if (doctorProfile != null) {
            try {
                Date workDate = Date.valueOf(targetDateStr);
                doctorSchedules = doctorScheduleDAO.findSchedulesByDoctorAndDate(doctorProfile.getId(), workDate);
            } catch (Exception ignored) {
            }
        }

        // 3. Sử dụng PaginationUtil để gom gọn logic phân trang
        int page = PaginationUtil.parsePage(request, "page");
        int pageSize = 5;
        int totalRecords = filterByDate
                ? appointmentDAO.countAppointmentsByDoctorUserAndDate(loginUser.getId(), dateParam)
                : appointmentDAO.countAppointmentsByDoctorUser(loginUser.getId());

        int totalPages = PaginationUtil.totalPages(totalRecords, pageSize);
        page = Math.min(page, totalPages);
        int offset = PaginationUtil.offset(page, pageSize);

        List<Appointment> appointments = filterByDate
                ? appointmentDAO.findAppointmentsByDoctorUserAndDatePaginated(loginUser.getId(), dateParam, offset,
                        pageSize)
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

        request.getRequestDispatcher(RouterConstant.DOCTOR_DASHBOARD_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Áp dụng requireRole
        User loginUser = requireRole(request, response, RoleConstant.DOCTOR);
        if (loginUser == null)
            return;

        String action = request.getParameter("action");
        String dateParam = request.getParameter("date");
        boolean isAjaxReq = isAjax(request);

        if (dateParam == null || dateParam.trim().isEmpty()) {
            dateParam = LocalDate.now().toString();
        }

        DoctorProfile doctorProfile = doctorProfileDAO.findByUserId(loginUser.getId());
        Date workDate = null;
        try {
            workDate = Date.valueOf(dateParam);
        } catch (Exception ignored) {
        }

        if ("save-diagnosis".equals(action)) {
            handleSaveDiagnosis(request, response, loginUser);
            return;
        }

        String msg = "";
        boolean success = false;

        if ("get-slots".equals(action)) {
            success = true;
            msg = "Lấy danh sách ca khám thành công";
        } else if ("generate-schedule".equals(action)) {
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
                    if (startTimeStr.length() == 5)
                        startTimeStr += ":00";
                    if (endTimeStr.length() == 5)
                        endTimeStr += ":00";

                    java.sql.Time startTime = java.sql.Time.valueOf(startTimeStr);
                    java.sql.Time endTime = java.sql.Time.valueOf(endTimeStr);

                    success = doctorScheduleDAO.insertSlot(doctorProfile.getId(), workDate, startTime, endTime);
                    msg = success
                            ? ("Đã đăng ký thêm ca khám " + startTimeStr.substring(0, 5) + " - "
                                    + endTimeStr.substring(0, 5))
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
                    msg = success ? "Đã xóa ca khám #" + slotId
                            : "Không thể xóa ca khám này (ca đã có bệnh nhân đặt lịch)!";
                } catch (Exception e) {
                    msg = "Lỗi xóa ca khám: " + e.getMessage();
                }
            }
        } else if ("register-weekly-schedule".equals(action)) {
            if (doctorProfile != null) {
                try {
                    String startDateStr = request.getParameter("startDate");
                    String endDateStr = request.getParameter("endDate");
                    String[] daysArr = request.getParameterValues("daysOfWeek");
                    String[] slotsArr = request.getParameterValues("timeSlots");

                    if (startDateStr == null || endDateStr == null) {
                        throw new IllegalArgumentException("Vui lòng chọn ngày bắt đầu và kết thúc!");
                    }

                    LocalDate startDate = LocalDate.parse(startDateStr.trim());
                    LocalDate endDate = LocalDate.parse(endDateStr.trim());

                    List<Integer> daysOfWeek = new ArrayList<>();
                    if (daysArr != null) {
                        for (String d : daysArr) {
                            for (String part : d.split(",")) {
                                if (!part.trim().isEmpty()) {
                                    daysOfWeek.add(Integer.parseInt(part.trim()));
                                }
                            }
                        }
                    }

                    List<String> timeSlots = new ArrayList<>();
                    if (slotsArr != null) {
                        for (String s : slotsArr) {
                            for (String part : s.split(",")) {
                                if (!part.trim().isEmpty()) {
                                    timeSlots.add(part.trim());
                                }
                            }
                        }
                    }

                    int createdCount = doctorScheduleDAO.registerWeeklyScheduleBatch(doctorProfile.getId(), startDate, endDate, daysOfWeek, timeSlots);
                    success = true;
                    msg = "Đã đăng ký thành công " + createdCount + " ca khám mới cho khoảng ngày " + startDateStr + " đến " + endDateStr + "!";
                    NotificationDAO.pushNotification(loginUser.getId(), "Đăng ký lịch làm việc thành công",
                            "Hệ thống đã lưu thành công " + createdCount + " ca làm việc mới cho khoảng ngày " + startDateStr + " đến " + endDateStr + ".",
                            "SCHEDULE", "doctor/dashboard?tab=schedules");
                    if (isAjaxReq) {
                        writeJson(response, String.format("{\"success\":true,\"message\":\"%s\",\"count\":%d}", msg.replace("\"", "\\\""), createdCount));
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    msg = e.getMessage();
                    if (isAjaxReq) {
                        writeJson(response, String.format("{\"success\":false,\"message\":\"%s\"}", msg.replace("\"", "\\\"")));
                        return;
                    }
                } catch (Exception e) {
                    msg = "Lỗi khi đăng ký lịch theo tuần: " + e.getMessage();
                    if (isAjaxReq) {
                        writeJson(response, String.format("{\"success\":false,\"message\":\"%s\"}", msg.replace("\"", "\\\"")));
                        return;
                    }
                }
            }
        } else if ("clear-weekly-schedule".equals(action)) {
            if (doctorProfile != null) {
                try {
                    String startDateStr = request.getParameter("startDate");
                    String endDateStr = request.getParameter("endDate");
                    String[] daysArr = request.getParameterValues("daysOfWeek");

                    if (startDateStr == null || endDateStr == null) {
                        throw new IllegalArgumentException("Vui lòng chọn ngày bắt đầu và kết thúc!");
                    }

                    LocalDate startDate = LocalDate.parse(startDateStr.trim());
                    LocalDate endDate = LocalDate.parse(endDateStr.trim());

                    List<Integer> daysOfWeek = new ArrayList<>();
                    if (daysArr != null) {
                        for (String d : daysArr) {
                            for (String part : d.split(",")) {
                                if (!part.trim().isEmpty()) {
                                    daysOfWeek.add(Integer.parseInt(part.trim()));
                                }
                            }
                        }
                    }

                    int clearedCount = doctorScheduleDAO.clearAvailableWeeklySchedules(doctorProfile.getId(), startDate, endDate, daysOfWeek);
                    success = true;
                    msg = "Đã dọn dẹp thành công " + clearedCount + " ca khám trống chưa có người đặt!";
                    if (isAjaxReq) {
                        writeJson(response, String.format("{\"success\":true,\"message\":\"%s\",\"count\":%d}", msg.replace("\"", "\\\""), clearedCount));
                        return;
                    }
                } catch (Exception e) {
                    msg = "Lỗi khi dọn dẹp ca khám: " + e.getMessage();
                    if (isAjaxReq) {
                        writeJson(response, String.format("{\"success\":false,\"message\":\"%s\"}", msg.replace("\"", "\\\"")));
                        return;
                    }
                }
            }
        }

        if (isAjaxReq && doctorProfile != null && workDate != null) {
            writeSlotsJson(response, doctorProfile.getId(), workDate, msg, success);
            return;
        }

        // 4. Áp dụng hàm Helper hiển thị thông báo
        if (success) {
            setSuccess(request, msg);
        } else {
            setError(request, msg);
        }
        response.sendRedirect(request.getContextPath() + RouterConstant.DASHBOARD_DOCTOR + "?tab=schedules&date=" + dateParam);
    }

    private void writeSlotsJson(HttpServletResponse response, int doctorId, Date workDate, String message,
            boolean success) throws IOException {

        List<DoctorSchedule> slots = doctorScheduleDAO.findSchedulesByDoctorAndDate(doctorId, workDate);
        StringBuilder json = new StringBuilder();

        // 5. Build JSON array
        json.append("{\"success\":").append(success)
                .append(",\"message\":\"").append(message != null ? message.replace("\"", "\\\"") : "").append("\"")
                .append(",\"slots\":[");
        for (int i = 0; i < slots.size(); i++) {
            DoctorSchedule s = slots.get(i);
            String startTimeStr = s.getStartTime() != null ? s.getStartTime().toString().substring(0, 5) : "";
            String endTimeStr = s.getEndTime() != null ? s.getEndTime().toString().substring(0, 5) : "";
            json.append(String.format("{\"id\":%d,\"startTime\":\"%s\",\"endTime\":\"%s\",\"isAvailable\":%b}",
                    s.getId(), startTimeStr, endTimeStr, s.isIsAvailable()));
            if (i < slots.size() - 1)
                json.append(",");
        }
        json.append("]}");

        writeJson(response, json.toString());
    }

    private void handleSaveDiagnosis(HttpServletRequest request, HttpServletResponse response, User doctorUser)
            throws IOException {
        String date = request.getParameter("date");
        try {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
            String diagnosis = request.getParameter("diagnosis");
            String prescription = request.getParameter("prescription");
            String revisitDateStr = request.getParameter("revisitDate");

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

                    User patient = userDAO.findById(app.getPatientId());
                    String patientEmail = (patient != null) ? patient.getEmail() : null;
                    String patientName = (patient != null) ? patient.getFullname() : "Quý khách";
                    String doctorName = doctorUser.getFullname();
                    String serviceName = (app.getServiceName() != null) ? app.getServiceName() : "Dịch vụ khám & điều trị";

                    // 1. Gửi Notification & Email Bệnh Án Điện Tử
                    NotificationDAO.pushNotification(app.getPatientId(), "Bệnh án & Toa thuốc đã sẵn sàng",
                            "Bác sĩ " + doctorName + " đã hoàn tất ca khám #" + appointmentId + " và cập nhật bệnh án điện tử của bạn.",
                            "MEDICAL", "history");

                    if (patientEmail != null && !patientEmail.trim().isEmpty()) {
                        EmailUtil.sendMedicalRecordAsync(patientEmail, patientName, doctorName, serviceName,
                                record.getDiagnosis(), record.getPrescriptionOrResult(), app.getAppointmentDate());
                    }

                    // 2. Xử lý Lịch Hẹn Tái Khám (Nếu Bác sĩ có chỉ định ngày tái khám)
                    if (revisitDateStr != null && !revisitDateStr.trim().isEmpty()) {
                        String cleanRevisitDate = revisitDateStr.trim();
                        NotificationDAO.pushNotification(app.getPatientId(), "📅 Lịch Hẹn Tái Khám Ngày " + cleanRevisitDate,
                                "Bác sĩ " + doctorName + " chỉ định bạn tái khám vào ngày " + cleanRevisitDate + " cho dịch vụ " + serviceName + ".",
                                "SCHEDULE", "booking?doctorId=" + app.getDoctorId());

                        if (patientEmail != null && !patientEmail.trim().isEmpty()) {
                            EmailUtil.sendRevisitReminderAsync(patientEmail, patientName, doctorName, serviceName,
                                    cleanRevisitDate, record.getPrescriptionOrResult());
                        }
                    }

                    // 3. Xử lý Cập nhật Tiến độ Gói Liệu Trình Spa (Nếu bệnh nhân đang theo liệu trình)
                    List<TreatmentPackage> packages = treatmentPackageDAO.findActivePackagesByPatient(app.getPatientId());
                    if (packages != null && !packages.isEmpty()) {
                        for (TreatmentPackage pkg : packages) {
                            if (pkg.getServiceName() != null && pkg.getServiceName().equalsIgnoreCase(serviceName)) {
                                if (patientEmail != null && !patientEmail.trim().isEmpty()) {
                                    EmailUtil.sendTreatmentProgressAsync(patientEmail, patientName, pkg.getPackageName(),
                                            serviceName, pkg.getCompletedSessions(), pkg.getTotalSessions(), pkg.getRemainingSessions());
                                }
                                break;
                            }
                        }
                    }

                    setSuccess(request,
                            "Lưu đơn thuốc, chẩn đoán y khoa và gửi thông báo điện tử cho ca khám #" + appointmentId + " thành công!");
                } else {
                    setError(request, "Không thể lưu hồ sơ bệnh án. Vui lòng thử lại!");
                }
            }
        } catch (Exception e) {
            setError(request, "Lỗi dữ liệu: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + RouterConstant.DASHBOARD_DOCTOR + "?date="
                + (date != null ? date : LocalDate.now().toString()));
    }
}