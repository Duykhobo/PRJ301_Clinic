package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.RouterConstant;
import constant.SystemConstant;
import dao.AppointmentDAO;
import dao.LoyaltyDAO;
import dao.MedicalRecordDAO;
import dao.TreatmentPackageDAO;
import model.Appointment;
import model.MedicalRecord;
import model.User;
import util.PaginationUtil;

/**
 * HistoryServlet - Servlet Quản lý Lịch sử Khám & Thanh Toán của Bệnh Nhân (/history).
 * Tích hợp Phân Trang chuyên nghiệp & Xem Đơn Thuốc / Kết Quả Khám Bệnh.
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {"/history"})
public class HistoryServlet extends BaseRoleServlet {

    private AppointmentDAO appointmentDAO;
    private MedicalRecordDAO medicalRecordDAO;
    private TreatmentPackageDAO treatmentPackageDAO;
    private LoyaltyDAO loyaltyDAO;

    private static final int PAGE_SIZE = 5; // Số ca khám trên mỗi trang

    @Override
    public void init() throws ServletException {
        this.appointmentDAO = new AppointmentDAO();
        this.medicalRecordDAO = new MedicalRecordDAO();
        this.treatmentPackageDAO = new TreatmentPackageDAO();
        this.loyaltyDAO = new LoyaltyDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SystemConstant.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN + "?redirect=" + RouterConstant.ROUTE_HISTORY);
            return;
        }

        User user = (User) session.getAttribute(SystemConstant.SESSION_USER);

        // 1. Phân trang an toàn với PaginationUtil
        int page = PaginationUtil.parsePage(request, "page");
        int totalItems = appointmentDAO.countPatientAppointments(user.getId());
        int totalPages = PaginationUtil.totalPages(totalItems, PAGE_SIZE);
        page = Math.min(page, totalPages);
        int offset = PaginationUtil.offset(page, PAGE_SIZE);

        // 2. Nạp danh sách lịch hẹn có phân trang
        List<Appointment> historyList = appointmentDAO.findPatientAppointmentsPaginated(user.getId(), offset, PAGE_SIZE);

        // 3. Nạp Map Hồ sơ bệnh án & đơn thuốc
        Map<Integer, MedicalRecord> recordsMap = new HashMap<>();
        for (Appointment app : historyList) {
            MedicalRecord record = medicalRecordDAO.getRecordByAppointmentId(app.getId());
            if (record != null) {
                recordsMap.put(app.getId(), record);
            }
        }

        // 4. Nạp Gói Liệu Trình thật & Hạng Hội Viên thật từ CSDL
        request.setAttribute("activePackages", treatmentPackageDAO.findActivePackagesByPatient(user.getId()));
        request.setAttribute("loyaltyProfile", loyaltyDAO.getLoyaltyProfileByPatient(user.getId()));

        request.setAttribute("historyList", historyList);
        request.setAttribute("recordsMap", recordsMap);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);

        request.getRequestDispatcher(RouterConstant.HISTORY_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
