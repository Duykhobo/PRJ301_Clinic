package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.RouterConstant;

/**
 * MainController - Mô hình Front Controller Chuẩn môn PRJ301 (FPT University).
 * Điểm điều hướng trung tâm: Tất cả Request từ Form/URL sẽ truyền tham số ?action=...
 * qua MainController để phân phối tới các Servlet/JSP tương ứng.
 */
@WebServlet(name = "MainController", urlPatterns = {"/MainController", "/main", "/home"})
public class MainController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String url = RouterConstant.HOME_JSP;

        if (action == null || action.trim().isEmpty()) {
            action = "home";
        }

        try {
            switch (action) {
                case "login-page":
                    url = RouterConstant.LOGIN_JSP;
                    break;
                case "login":
                    url = RouterConstant.ROUTE_LOGIN;
                    break;
                case "register-page":
                    url = RouterConstant.REGISTER_JSP;
                    break;
                case "register":
                    url = RouterConstant.ROUTE_REGISTER;
                    break;
                case "logout":
                    url = RouterConstant.ROUTE_LOGOUT;
                    break;
                case "booking-page":
                case "booking":
                    url = RouterConstant.ROUTE_BOOKING;
                    break;
                case "history":
                    url = RouterConstant.HISTORY_JSP;
                    break;
                default:
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
