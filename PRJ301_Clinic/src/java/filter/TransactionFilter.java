package filter;

import config.DBContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;

/**
 * TransactionFilter — Bộ lọc Quản lý Vòng đời Transaction Nguyên Tử.
 *
 * <p><b>📖 Nguyên lý hoạt động (Filter Chain Pattern):</b></p>
 * <pre>
 * [POST Request]
 *      ↓
 * TransactionFilter.doFilter() {
 *     conn = DBContext.getConnection()
 *     conn.setAutoCommit(false)     ← BẮT ĐẦU TRANSACTION
 *          ↓
 *     chain.doFilter()              ← CHẠY SERVLET + TẤT CẢ DAO
 *          ↓
 *     conn.commit()                 ← THÀNH CÔNG → COMMIT
 * } catch (Exception e) {
 *     conn.rollback()               ← CÓ LỖI → ROLLBACK
 * } finally {
 *     DBContext.clearConnection()   ← LUÔN LUÔN DỌN DẸP
 * }
 * </pre>
 *
 * <p><b>❓ Tại sao chỉ áp dụng cho POST?</b><br>
 * GET request chỉ đọc dữ liệu (read-only), không cần transaction.
 * POST/PUT/DELETE thay đổi dữ liệu, cần atomicity.</p>
 *
 * <p><b>⚠️ Thứ tự filter quan trọng:</b><br>
 * TransactionFilter phải chạy TRƯỚC AuthenticationFilter để connection
 * được tạo sẵn trước khi Servlet xử lý.</p>
 *
 * @see DBContext#getConnection()
 * @see DBContext#clearConnection()
 */
@WebFilter(filterName = "TransactionFilter", urlPatterns = {"/*"})
public class TransactionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Không cần khởi tạo gì — DBContext đã dùng static method
    }

    /**
     * Xử lý request theo phương thức HTTP:
     * <ul>
     *   <li>POST → wrap trong transaction (setAutoCommit=false → commit/rollback → clearConnection)</li>
     *   <li>GET/khác → chỉ cần clearConnection trong finally (không cần transaction)</li>
     * </ul>
     *
     * <p><b>📖 Cấu trúc cần implement cho POST:</b></p>
     * <pre>
     * try {
     *     Connection conn = DBContext.getConnection();
     *     conn.setAutoCommit(???);       // false để bắt đầu transaction
     *
     *     chain.doFilter(req, res);      // chạy servlet
     *
     *     conn.commit();                 // thành công → commit
     * } catch (Exception e) {
     *     try {
     *         DBContext.getConnection().rollback();  // lỗi → rollback
     *     } catch (Exception ignored) {}
     *     throw new ServletException(e); // ném lại để container xử lý
     * } finally {
     *     DBContext.clearConnection();   // LUÔN LUÔN dọn dẹp
     * }
     * </pre>
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param chain    Filter chain tiếp theo
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        // TODO [BƯỚC 3a]: Lấy HTTP method từ request
        // Gợi ý: req.getMethod() trả về "GET", "POST", "PUT", ...
        // String method = ???

        // TODO [BƯỚC 3b]: Kiểm tra method có phải POST không?
        // if ("POST".equalsIgnoreCase(method)) {
        //     ... xử lý với transaction ...
        // } else {
        //     ... xử lý không cần transaction ...
        // }

        // TODO [BƯỚC 3c - Nhánh POST]: Implement try-catch-finally theo JavaDoc trên
        //
        //   ❓ Câu hỏi kiểm tra hiểu biết:
        //   1. Tại sao phải gọi conn.setAutoCommit(false) TRƯỚC chain.doFilter()?
        //   2. Tại sao clearConnection() phải nằm trong finally chứ không phải sau commit()?
        //   3. Điều gì xảy ra nếu Servlet gọi response.sendRedirect() — transaction có bị rollback không?

        // TODO [BƯỚC 3d - Nhánh GET]: Implement try-finally đơn giản
        //   → Gọi chain.doFilter()
        //   → Trong finally: gọi DBContext.clearConnection()

        throw new UnsupportedOperationException("TODO: Implement TransactionFilter.doFilter()");
    }

    @Override
    public void destroy() {
        // Không cần dọn dẹp
    }
}
