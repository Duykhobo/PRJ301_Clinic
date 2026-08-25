package controller;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
 * Tách biệt lỗi chi tiết cho từng field, dùng toán tử 3 ngôi tinh gọn.
 */
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = { "/forgot-password" })
public class ForgotPasswordServlet extends BaseRoleServlet {

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
        forward(request, response, RouterConstant.FORGOT_PASSWORD_JSP);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        request.setAttribute("email", email);

        Map<String, String> errors = new HashMap<>();
        ValidationUtil.validateField(errors, "email", ValidationUtil.isValidEmail(email), MessageConstant.ERR_INVALID_EMAIL);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_INVALID_EMAIL);
            forward(request, response, RouterConstant.FORGOT_PASSWORD_JSP);
            return;
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            errors.put("email", MessageConstant.ERR_EMAIL_NOT_FOUND);
            request.setAttribute("errors", errors);
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_EMAIL_NOT_FOUND);
            forward(request, response, RouterConstant.FORGOT_PASSWORD_JSP);
            return;
        }

        String tempPassword = generateTempPassword();
        boolean updated = userService.resetPasswordByEmail(user.getId(), tempPassword);
        if (!updated) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Lỗi hệ thống khi cập nhật mật khẩu mới. Vui lòng thử lại sau!");
            forward(request, response, RouterConstant.FORGOT_PASSWORD_JSP);
            return;
        }

        boolean emailSent = EmailUtil.sendPasswordResetSync(user.getEmail(), user.getFullname(), tempPassword);
        request.setAttribute(emailSent ? SystemConstant.SUCCESS_MESSAGE_ATTR : SystemConstant.ERROR_MESSAGE_ATTR,
                             emailSent ? MessageConstant.MSG_SEND_EMAIL_SUCCESS : MessageConstant.ERR_SEND_EMAIL_FAILED);

        forward(request, response, RouterConstant.FORGOT_PASSWORD_JSP);
    }

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder("Clinic#");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHA_NUMERIC.charAt(RANDOM.nextInt(ALPHA_NUMERIC.length())));
        }
        return sb.toString();
    }
}
