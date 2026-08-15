package controller;

import constant.SystemConstant;
import model.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * BaseRoleServlet — Lớp trừu tượng cung cấp các Helper tái sử dụng cho mọi Servlet.
 *
 * <p><b>📖 Design Pattern: Template Method</b><br>
 * Lớp này định nghĩa các "phương thức tiện ích" (utility methods) chung.
 * Các Servlet con kế thừa và gọi lại các method này thay vì viết lại từ đầu.</p>
 *
 * <p><b>📖 Nguyên tắc DRY (Don't Repeat Yourself):</b><br>
 * Hiện tại {@code DoctorServlet} và {@code ReceptionistServlet} đều có đoạn code:</p>
 * <pre>
 *   HttpSession session = request.getSession(false);
 *   User loginUser = (session != null) ? (User) session.getAttribute(SystemConstant.SESSION_USER) : null;
 *   if (loginUser == null || !"DOCTOR".equalsIgnoreCase(loginUser.getRole())) {
 *       response.sendRedirect(request.getContextPath() + "/login");
 *       return;
 *   }
 * </pre>
 * <p>Thay vì lặp lại đoạn trên ở 4 Servlet, ta đưa vào {@code BaseRoleServlet} 1 lần.</p>
 *
 * <p><b>Cách sử dụng sau khi implement:</b></p>
 * <pre>
 * public class DoctorServlet extends BaseRoleServlet {
 *     {@literal @}Override
 *     protected void doGet(HttpServletRequest req, HttpServletResponse res) {
 *         User user = requireRole(req, res, "DOCTOR");
 *         if (user == null) return;  // đã redirect, dừng xử lý
 *         // ... tiếp tục logic
 *     }
 * }
 * </pre>
 */
public abstract class BaseRoleServlet extends HttpServlet {

    /**
     * Lấy User từ session và kiểm tra role.
     * Nếu chưa đăng nhập hoặc sai role → tự động redirect về trang login.
     *
     * <p><b>📖 Logic cần implement:</b></p>
     * <ol>
     *   <li>Lấy {@code HttpSession} bằng {@code request.getSession(false)}
     *       (false = không tạo session mới nếu chưa có).</li>
     *   <li>Lấy {@code User} từ session attribute {@link SystemConstant#SESSION_USER}.</li>
     *   <li>Nếu {@code user == null} → redirect về {@code contextPath + "/login"} → return null.</li>
     *   <li>Kiểm tra role: dùng vòng lặp qua varargs {@code roles} để so sánh
     *       {@code user.getRole()} với từng role cho phép (case-insensitive).</li>
     *   <li>Nếu không khớp role nào → redirect về login → return null.</li>
     *   <li>Nếu hợp lệ → return user.</li>
     * </ol>
     *
     * <p><b>❓ Tại sao dùng varargs {@code String... roles}?</b><br>
     * Vì một số trang cho phép nhiều role truy cập. Ví dụ:
     * {@code requireRole(req, res, "ADMIN", "RECEPTIONIST")}</p>
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param roles Danh sách role được phép truy cập (ít nhất 1 role)
     * @return User đã đăng nhập nếu hợp lệ, {@code null} nếu đã redirect
     * @throws IOException nếu redirect thất bại
     */
    protected User requireRole(HttpServletRequest request, HttpServletResponse response,
                               String... roles) throws IOException {
        // TODO [BƯỚC 4a]: Lấy session bằng getSession(false) — tại sao không dùng getSession()?
        // HttpSession session = ???

        // TODO [BƯỚC 4b]: Lấy User từ session attribute SystemConstant.SESSION_USER
        // User user = ???

        // TODO [BƯỚC 4c]: Nếu user == null → redirect + return null
        // Gợi ý URL: request.getContextPath() + "/login"

        // TODO [BƯỚC 4d]: Duyệt qua mảng roles, kiểm tra user.getRole() khớp không
        // for (String role : roles) {
        //     if (role.equalsIgnoreCase(user.getRole())) return user;
        // }

        // TODO [BƯỚC 4e]: Không khớp role nào → redirect + return null

        throw new UnsupportedOperationException("TODO: Implement requireRole()");
    }

    /**
     * Kiểm tra request hiện tại có phải AJAX request không.
     *
     * <p><b>📖 Hai cách frontend gửi AJAX request:</b></p>
     * <ul>
     *   <li>Header {@code X-Requested-With: XMLHttpRequest} (jQuery, Axios mặc định)</li>
     *   <li>Form field {@code ajax=true} (dự án này dùng cách này)</li>
     * </ul>
     *
     * @param request HTTP request
     * @return true nếu là AJAX request
     */
    protected boolean isAjax(HttpServletRequest request) {
        // TODO [BƯỚC 4f]: Kiểm tra header "X-Requested-With" hoặc param "ajax"
        // Gợi ý:
        //   "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
        //   || "true".equalsIgnoreCase(request.getParameter("ajax"))
        throw new UnsupportedOperationException("TODO: Implement isAjax()");
    }

    /**
     * Ghi JSON response với Content-Type chuẩn.
     *
     * <p><b>📖 Cần set trước khi getWriter():</b><br>
     * {@code response.setContentType("application/json;charset=UTF-8")}</p>
     *
     * @param response HTTP response
     * @param json     Chuỗi JSON cần ghi
     * @throws IOException nếu ghi thất bại
     */
    protected void writeJson(HttpServletResponse response, String json) throws IOException {
        // TODO [BƯỚC 4g]: Set content type + ghi json ra response writer
        throw new UnsupportedOperationException("TODO: Implement writeJson()");
    }

    /**
     * Lưu thông báo thành công vào Session (flash message).
     *
     * <p><b>📖 Flash message pattern:</b><br>
     * Lưu message vào session, sau khi redirect JSP đọc và hiển thị rồi xóa.
     * Dùng {@link SystemConstant#SUCCESS_MESSAGE_ATTR} làm key.</p>
     *
     * @param request HTTP request
     * @param message Nội dung thông báo thành công
     */
    protected void setSuccess(HttpServletRequest request, String message) {
        // TODO [BƯỚC 4h]: request.getSession().setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, message)
        throw new UnsupportedOperationException("TODO: Implement setSuccess()");
    }

    /**
     * Lưu thông báo lỗi vào Session (flash message).
     *
     * <p>Dùng {@link SystemConstant#ERROR_MESSAGE_ATTR} làm key.</p>
     *
     * @param request HTTP request
     * @param message Nội dung thông báo lỗi
     */
    protected void setError(HttpServletRequest request, String message) {
        // TODO [BƯỚC 4i]: Tương tự setSuccess nhưng dùng ERROR_MESSAGE_ATTR
        throw new UnsupportedOperationException("TODO: Implement setError()");
    }

    /**
     * Parse tham số phân trang từ request, trả về giá trị mặc định nếu không hợp lệ.
     *
     * <p><b>📖 Fail-safe parsing:</b><br>
     * Người dùng có thể nhập URL bất kỳ, ví dụ {@code ?page=abc} → phải xử lý NumberFormatException.</p>
     *
     * @param request   HTTP request
     * @param paramName Tên query parameter (ví dụ: "page", "pageUser")
     * @return Số trang hợp lệ (tối thiểu là 1)
     */
    protected int parsePage(HttpServletRequest request, String paramName) {
        // TODO [BƯỚC 4j]: Parse tham số paramName từ request
        // try {
        //     String val = request.getParameter(paramName);
        //     return (val != null) ? Math.max(1, Integer.parseInt(val)) : 1;
        // } catch (NumberFormatException e) {
        //     return 1;
        // }
        throw new UnsupportedOperationException("TODO: Implement parsePage()");
    }
}
