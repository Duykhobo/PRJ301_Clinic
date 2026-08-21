package filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import config.DBContext;

/**
 * TransactionFilter — Bộ lọc Quản lý Vòng đời Transaction Nguyên Tử.
 *
 * @see DBContext#getConnection()
 * @see DBContext#clearConnection()
 */
@WebFilter(filterName = "TransactionFilter", urlPatterns = { "/*" })
public class TransactionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * @param request  HTTP request
     * @param response HTTP response
     * @param chain    Filter chain tiếp theo
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String method = req.getMethod();
        if ("POST".equalsIgnoreCase(method)) {
            Connection conn = null;
            try {
                conn = DBContext.getConnection();
                if (conn != null && !conn.isClosed()) {
                    conn.setAutoCommit(false);
                }
                chain.doFilter(request, response);
                conn = DBContext.getConnection();
                if (conn != null && !conn.isClosed()) {
                    conn.commit();
                }
            } catch (Exception e) {
                try {
                    conn = DBContext.getConnection();
                    if (conn != null && !conn.isClosed()) {
                        conn.rollback();
                    }
                } catch (SQLException ignored) {

                }
                throw new ServletException(e);
            } finally {
                DBContext.clearConnection();
            }
        } else {
            try {
                chain.doFilter(request, response);
            } finally {
                DBContext.clearConnection();
            }
        }
    }

    @Override
    public void destroy() {
        // Không cần dọn dẹp
    }
}
