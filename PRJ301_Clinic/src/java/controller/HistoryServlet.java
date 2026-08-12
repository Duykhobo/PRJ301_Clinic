package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.RouterConstant;
import constant.SystemConstant;
import model.Appointment;
import model.User;
import service.BookingService;

/**
 * HistoryServlet - Xử lý Nạp Lịch Sử Đặt Lịch Khám & Thanh Toán Bệnh Nhân (/history).
 * Chuẩn mô hình Enterprise 3-Tier (Servlet -> Service -> DAO).
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {"/history"})
public class HistoryServlet extends HttpServlet {

    private BookingService bookingService;

    @Override
    public void init() throws ServletException {
        this.bookingService = new BookingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Kiểm tra Session Guard bệnh nhân
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SystemConstant.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN + "?redirect=/history");
            return;
        }

        User user = (User) session.getAttribute(SystemConstant.SESSION_USER);

        // 2. Gọi Service nạp danh sách Lịch sử cuộc hẹn
        List<Appointment> historyList = bookingService.getPatientAppointmentHistory(user.getId());
        request.setAttribute("historyList", historyList);

        // 3. Forward sang HISTORY_JSP
        request.getRequestDispatcher(RouterConstant.HISTORY_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
