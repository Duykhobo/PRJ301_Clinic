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
import service.ClinicService;

/**
 * MainController - Mô hình Front Controller Chuẩn môn PRJ301 (FPT University).
 * Điểm điều hướng trung tâm: Tất cả Request từ Form/URL sẽ truyền tham số ?action=...
 * qua MainController để phân phối tới các Servlet/JSP tương ứng.
 */
@WebServlet(name = "MainController", urlPatterns = {"/MainController", "/main", "/home"})
public class MainController extends HttpServlet {

    private ClinicService clinicService;

    @Override
    public void init() throws ServletException {
        this.clinicService = new ClinicService();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = RouterConstant.HOME_JSP;

        try {
            if (action == null || action.trim().isEmpty()) {
                action = "home";
            }

            switch (action) {
                case "login-page":
                case "login":
                    url = RouterConstant.ROUTE_LOGIN;
                    break;
                case "register-page":
                case "register":
                    url = RouterConstant.ROUTE_REGISTER;
                    break;
                case "forgot-password-page":
                case "forgot-password":
                    url = RouterConstant.ROUTE_FORGOT_PASSWORD;
                    break;
                case "logout":
                    url = RouterConstant.ROUTE_LOGOUT;
                    break;
                case "booking-page":
                case "booking":
                    url = RouterConstant.ROUTE_BOOKING;
                    break;
                case "history-page":
                case "history":
                    url = RouterConstant.ROUTE_HISTORY;
                    break;
                case "profile-page":
                case "profile":
                    url = RouterConstant.ROUTE_PROFILE;
                    break;
                default:
                    request.setAttribute("services", clinicService.getActiveServices());
                    request.setAttribute("doctors", clinicService.getAllDoctors());
                    url = RouterConstant.HOME_JSP;
                    break;
            }
            request.getRequestDispatcher(url).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            log("Lỗi tại MainController: " + e.getMessage(), e);
            request.setAttribute("exception", e);
            request.getRequestDispatcher(RouterConstant.ERROR_500_JSP).forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
