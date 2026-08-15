package filter;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * CSRFTokenFilter - Bộ lọc Khởi tạo & Kiểm tra Mã Bảo mật CSRF Token.
 * Giúp chống tấn công Cross-Site Request Forgery trên các Form POST.
 */
@WebFilter(filterName = "CSRFTokenFilter", urlPatterns = {"/*"})
public class CSRFTokenFilter implements Filter {

    public static final String CSRF_TOKEN_SESSION = "CSRF_TOKEN";
    public static final String CSRF_TOKEN_REQ_ATTR = "csrfToken";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization required
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpSession session = httpRequest.getSession(true);

            String csrfToken = (String) session.getAttribute(CSRF_TOKEN_SESSION);
            if (csrfToken == null) {
                csrfToken = UUID.randomUUID().toString();
                session.setAttribute(CSRF_TOKEN_SESSION, csrfToken);
            }
            httpRequest.setAttribute(CSRF_TOKEN_REQ_ATTR, csrfToken);

            // Làm mới mã CSRF Token trong Session sau mỗi lần Submit POST để chống F5 Re-submit form cũ
            if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
                session.setAttribute(CSRF_TOKEN_SESSION, UUID.randomUUID().toString());
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No cleanup required
    }
}
