package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.MessageConstant;
import constant.RoleConstant;
import constant.RouterConstant;
import constant.SystemConstant;
import model.User;
import service.UserService;
import util.EmailUtil;
import util.ValidationUtil;

/**
 * RegisterServlet - Xử lý Đăng ký Tài khoản Bệnh nhân (/register).
 * Tách biệt lỗi chi tiết cho từng field, dùng toán tử 3 ngôi tinh gọn.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = { "/register" })
public class RegisterServlet extends BaseRoleServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response, RouterConstant.REGISTER_JSP);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username") != null ? request.getParameter("username").trim() : "";
        String password = request.getParameter("password") != null ? request.getParameter("password") : "";
        String confirmPassword = request.getParameter("confirmPassword") != null ? request.getParameter("confirmPassword") : "";
        String fullname = request.getParameter("fullname") != null ? request.getParameter("fullname").trim() : "";
        String phone = request.getParameter("phone") != null ? request.getParameter("phone").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";

        // Giữ lại form inputs
        request.setAttribute("username", username);
        request.setAttribute("fullname", fullname);
        request.setAttribute("phone", phone);
        request.setAttribute("email", email);

        Map<String, String> errors = new HashMap<>();

        // 1. Tách lỗi chi tiết cho từng field
        ValidationUtil.validateField(errors, "username", ValidationUtil.isValidUsername(username), MessageConstant.ERR_INVALID_USERNAME);
        ValidationUtil.validateField(errors, "fullname", ValidationUtil.isValidFullname(fullname), MessageConstant.ERR_INVALID_FULLNAME);
        ValidationUtil.validateField(errors, "phone", ValidationUtil.isValidPhone(phone), MessageConstant.ERR_INVALID_PHONE);
        ValidationUtil.validateField(errors, "password", ValidationUtil.isValidPassword(password), MessageConstant.ERR_INVALID_PASSWORD);
        ValidationUtil.validateField(errors, "confirmPassword", password.equals(confirmPassword) && !confirmPassword.isEmpty(), MessageConstant.ERR_PASSWORD_MISMATCH);

        if (!email.isEmpty()) {
            ValidationUtil.validateField(errors, "email", ValidationUtil.isValidEmail(email), MessageConstant.ERR_INVALID_EMAIL);
        }

        // 2. Check trùng lặp DB khi field hợp lệ
        if (!errors.containsKey("username") && userService.existsByUsername(username)) {
            errors.put("username", MessageConstant.ERR_USERNAME_EXISTS);
        }
        if (!email.isEmpty() && !errors.containsKey("email") && userService.existsByEmail(email)) {
            errors.put("email", MessageConstant.ERR_EMAIL_EXISTS);
        }

        // 3. Nếu có lỗi -> Truyền errors map về JSP
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng kiểm tra và hoàn thiện các trường dữ liệu bên dưới!");
            forward(request, response, RouterConstant.REGISTER_JSP);
            return;
        }

        // 4. Khởi tạo & Lưu User mới
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullname(fullname);
        newUser.setPhone(phone);
        newUser.setEmail(!email.isEmpty() ? email : null);
        newUser.setRole(RoleConstant.PATIENT);
        newUser.setStatus(true);

        boolean created = userService.registerPatient(newUser);
        if (created) {
            if (email != null && !email.trim().isEmpty()) {
                EmailUtil.sendWelcomeEmailAsync(email, fullname, username);
            }
            redirect(request, response, RouterConstant.ROUTE_LOGIN + "?registered=success");
        } else {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Đã xảy ra lỗi trong quá trình tạo tài khoản. Vui lòng thử lại!");
            forward(request, response, RouterConstant.REGISTER_JSP);
        }
    }
}