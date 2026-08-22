package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.RoleConstant;
import constant.RouterConstant;
import constant.SystemConstant;
import dao.UserDAO;
import model.User;
import util.BCryptUtil;
import util.ValidationUtil;

/**
 * ProfileServlet - Quản lý Chỉnh sửa Hồ sơ Cá nhân và Đổi mật khẩu cho Người Dùng.
 * Tách biệt lỗi chi tiết cho từng field, dùng toán tử 3 ngôi tinh gọn.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends BaseRoleServlet {

    private UserDAO userDAO;
    private dao.LoyaltyDAO loyaltyDAO;
    private dao.DoctorProfileDAO doctorProfileDAO;

    @Override
    public void init() throws ServletException {
        this.userDAO = new UserDAO();
        this.loyaltyDAO = new dao.LoyaltyDAO();
        this.doctorProfileDAO = new dao.DoctorProfileDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loginUser = (User) request.getSession().getAttribute(SystemConstant.SESSION_USER);
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN + "?redirect=" + RouterConstant.ROUTE_PROFILE);
            return;
        }

        User freshUser = userDAO.findById(loginUser.getId());
        User effectiveUser = freshUser != null ? freshUser : loginUser;
        request.setAttribute("user", effectiveUser);
        request.getSession().setAttribute(SystemConstant.SESSION_USER, effectiveUser);

        if (RoleConstant.PATIENT.equalsIgnoreCase(effectiveUser.getRole())) {
            request.setAttribute("loyaltyProfile", loyaltyDAO.getLoyaltyProfileByPatient(effectiveUser.getId()));
        } else if (RoleConstant.DOCTOR.equalsIgnoreCase(effectiveUser.getRole())) {
            request.setAttribute("doctorProfile", doctorProfileDAO.findByUserId(effectiveUser.getId()));
        }

        request.getRequestDispatcher(RouterConstant.PROFILE_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User loginUser = (User) request.getSession().getAttribute(SystemConstant.SESSION_USER);
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN + "?redirect=" + RouterConstant.ROUTE_PROFILE);
            return;
        }

        String action = request.getParameter("action") != null ? request.getParameter("action") : "update-profile";
        if ("change-password".equals(action)) {
            handleChangePassword(request, response, loginUser);
        } else {
            handleUpdateProfile(request, response, loginUser);
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, User loginUser)
            throws ServletException, IOException {
        String fullname = request.getParameter("fullname") != null ? request.getParameter("fullname").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String phone = request.getParameter("phone") != null ? request.getParameter("phone").trim().replaceAll("[\\s.\\-\\(\\)]", "") : "";

        // Doctor specific fields
        String specialty = request.getParameter("specialty") != null ? request.getParameter("specialty").trim() : "";
        String expYearsStr = request.getParameter("experienceYears") != null ? request.getParameter("experienceYears").trim() : "1";
        String roomNumber = request.getParameter("roomNumber") != null ? request.getParameter("roomNumber").trim() : "";
        String bio = request.getParameter("bio") != null ? request.getParameter("bio").trim() : "";

        Map<String, String> errors = new HashMap<>();
        ValidationUtil.validateField(errors, "fullname", ValidationUtil.isValidFullname(fullname), "Họ và tên không hợp lệ (độ dài 2-100 ký tự)!");
        ValidationUtil.validateField(errors, "email", ValidationUtil.isValidEmail(email), "Địa chỉ Email không đúng định dạng!");
        ValidationUtil.validateField(errors, "phone", ValidationUtil.isValidPhone(phone), "Số điện thoại không hợp lệ (10 chữ số)!");

        // Check duplicate email
        if (email != null && !email.isEmpty() && ValidationUtil.isValidEmail(email)) {
            User existingEmailUser = userDAO.findByEmail(email);
            if (existingEmailUser != null && existingEmailUser.getId() != loginUser.getId()) {
                ValidationUtil.validateField(errors, "email", false, "Địa chỉ Email này đã thuộc tài khoản khác!");
            }
        }

        // Check duplicate phone
        if (phone != null && !phone.isEmpty() && ValidationUtil.isValidPhone(phone)) {
            User existingPhoneUser = userDAO.findByPhone(phone);
            if (existingPhoneUser != null && existingPhoneUser.getId() != loginUser.getId()) {
                ValidationUtil.validateField(errors, "phone", false, "Số điện thoại này đã thuộc tài khoản khác!");
            }
        }

        // Validate Doctor fields if applicable
        int expYears = 1;
        if (RoleConstant.DOCTOR.equalsIgnoreCase(loginUser.getRole())) {
            ValidationUtil.validateField(errors, "specialty", !specialty.isEmpty(), "Vui lòng nhập chuyên khoa điều trị!");
            try {
                expYears = Integer.parseInt(expYearsStr);
                if (expYears < 0 || expYears > 60) {
                    ValidationUtil.validateField(errors, "experienceYears", false, "Số năm kinh nghiệm không hợp lệ (0-60 năm)!");
                }
            } catch (NumberFormatException e) {
                ValidationUtil.validateField(errors, "experienceYears", false, "Số năm kinh nghiệm phải là chữ số!");
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng kiểm tra lại các trường thông tin bị lỗi bên dưới!");
            doGet(request, response);
            return;
        }

        boolean updated = userDAO.updateProfile(loginUser.getId(), fullname, email, phone);
        if (RoleConstant.DOCTOR.equalsIgnoreCase(loginUser.getRole()) && updated) {
            doctorProfileDAO.updateDoctorProfile(loginUser.getId(), specialty, expYears, roomNumber, bio);
        }

        if (updated) {
            User fresh = userDAO.findById(loginUser.getId());
            if (fresh != null) {
                request.getSession().setAttribute(SystemConstant.SESSION_USER, fresh);
                request.setAttribute("user", fresh);
            }
            request.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, "Cập nhật hồ sơ cá nhân thành công!");
        } else {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Cập nhật hồ sơ thất bại, vui lòng kiểm tra lại thông tin!");
        }
        doGet(request, response);
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, User loginUser)
            throws ServletException, IOException {
        String oldPassword = request.getParameter("oldPassword") != null ? request.getParameter("oldPassword") : "";
        String newPassword = request.getParameter("newPassword") != null ? request.getParameter("newPassword") : "";
        String confirmPassword = request.getParameter("confirmPassword") != null ? request.getParameter("confirmPassword") : "";

        Map<String, String> errors = new HashMap<>();
        User freshUser = userDAO.findById(loginUser.getId());

        boolean isOldPassValid = freshUser != null && BCryptUtil.checkPassword(oldPassword, freshUser.getPassword());
        ValidationUtil.validateField(errors, "oldPassword", isOldPassValid, "Mật khẩu hiện tại không chính xác!");
        ValidationUtil.validateField(errors, "newPassword", ValidationUtil.isValidPassword(newPassword), "Mật khẩu mới phải có ít nhất 6 ký tự!");
        ValidationUtil.validateField(errors, "confirmPassword", newPassword.equals(confirmPassword) && !confirmPassword.isEmpty(), "Mật khẩu xác nhận không trùng khớp!");

        request.setAttribute("activeTab", "password");
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng kiểm tra lại thông tin đổi mật khẩu!");
            doGet(request, response);
            return;
        }

        boolean updated = userDAO.updatePassword(loginUser.getId(), newPassword.trim());
        request.setAttribute(updated ? SystemConstant.SUCCESS_MESSAGE_ATTR : SystemConstant.ERROR_MESSAGE_ATTR,
                             updated ? "Đổi mật khẩu thành công! Vui lòng sử dụng mật khẩu mới cho các lần đăng nhập tiếp theo." : "Đổi mật khẩu thất bại!");
        doGet(request, response);
    }
}
