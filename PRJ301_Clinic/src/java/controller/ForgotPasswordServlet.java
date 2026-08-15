package controller;

import java.io.IOException;
import java.security.SecureRandom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.MessageConstant;
import constant.RouterConstant;
import constant.SystemConstant;
import model.User;
import service.UserService;
import util.EmailUtil;
import util.ValidationUtil;

/**
 * ForgotPasswordServlet - Xử lý Quên Mật Khẩu qua Email (/forgot-password).
 * Kiểm tra sự tồn tại của Email trong CSDL, cập nhật mật khẩu tạm thời và gửi Mail SMTP.
 */
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = { "/forgot-password" })
public class ForgotPasswordServlet extends HttpServlet {

    private UserService userService;
    private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(RouterConstant.FORGOT_PASSWORD_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        request.setAttribute("email", email);

        // 1. Validation kiểm tra định dạng Email hợp lệ
        if (email == null || email.trim().isEmpty() || !ValidationUtil.isValidEmail(email.trim())) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_INVALID_EMAIL);
            request.getRequestDispatcher(RouterConstant.FORGOT_PASSWORD_JSP).forward(request, response);
            return;
        }

        String cleanEmail = email.trim();

        // 2. Kiểm tra sự tồn tại của Email trong CSDL
        User user = userService.findByEmail(cleanEmail);
        if (user == null) {
            // THÔNG BÁO LỖI: Không tìm thấy Email trong hệ thống
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_EMAIL_NOT_FOUND);
            request.getRequestDispatcher(RouterConstant.FORGOT_PASSWORD_JSP).forward(request, response);
            return;
        }

        // 3. Tạo mật khẩu tạm thời mới
        String tempPassword = generateTempPassword();

        // 4. Cập nhật mật khẩu băm mới vào CSDL
        boolean updated = userService.resetPasswordByEmail(user.getId(), tempPassword);
        if (!updated) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR,
                    "Lỗi hệ thống khi cập nhật mật khẩu mới. Vui lòng thử lại sau!");
            request.getRequestDispatcher(RouterConstant.FORGOT_PASSWORD_JSP).forward(request, response);
            return;
        }

        // 5. Gửi Email thông báo Mật khẩu tạm qua máy chủ Mail SMTP Real
        boolean emailSent = EmailUtil.sendPasswordResetSync(user.getEmail(), user.getFullname(), tempPassword);

        if (!emailSent) {
            // THÔNG BÁO LỖI: Gửi email thất bại (Lỗi kết nối SMTP Mail Server)
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_SEND_EMAIL_FAILED);
        } else {
            // THÔNG BÁO THÀNH CÔNG: Đã gửi email thành công
            request.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, MessageConstant.MSG_SEND_EMAIL_SUCCESS);
        }

        request.getRequestDispatcher(RouterConstant.FORGOT_PASSWORD_JSP).forward(request, response);
    }

    /**
     * Helper tạo chuỗi mật khẩu ngẫu nhiên 8 ký tự.
     */
    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder("Clinic#");
        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(ALPHA_NUMERIC.length());
            sb.append(ALPHA_NUMERIC.charAt(index));
        }
        return sb.toString();
    }
}
