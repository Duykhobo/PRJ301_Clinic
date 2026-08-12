package controller;

import java.io.IOException;
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
import model.Appointment;
import model.User;

/**
 * ReceptionistServlet - Servlet Quản lý Không gian Làm việc của Lễ Tân.
 * Theo dõi ca khám sảnh, tiếp nhận Bệnh nhân check-in, thu tiền mặt & hủy ca khi cần.
 */
@WebServlet(name = "ReceptionistServlet", urlPatterns = {"/receptionist/dashboard"})
public class ReceptionistServlet extends HttpServlet {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SystemConstant.SESSION_USER) : null;

        if (loginUser == null || !"RECEPTIONIST".equalsIgnoreCase(loginUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=/receptionist/dashboard");
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
        int totalRecords = appointmentDAO.countAllAppointmentsByDate(dateParam);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
        if (page > totalPages) page = totalPages;
        int offset = (page - 1) * pageSize;

        List<Appointment> appointments = appointmentDAO.findAllAppointmentsByDatePaginated(dateParam, offset, pageSize);
        List<Appointment> allDayApps = appointmentDAO.findAllAppointmentsByDate(dateParam);

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
                appointmentDAO.updateStatus(appointmentId, SystemConstant.STATUS_CONFIRMED);
                session.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, "Xác nhận thu tiền mặt thành công cho ca #" + appointmentId + "!");
            } else if ("cancel-appointment".equals(action)) {
                appointmentDAO.updateStatus(appointmentId, SystemConstant.STATUS_CANCELLED);
                session.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, "Đã hủy cuộc hẹn ca #" + appointmentId + "!");
            }

        } catch (Exception e) {
            session.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Lỗi xử lý: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/receptionist/dashboard?date=" + date);
    }
}
