package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * EncodingFilter - Bộ lọc Ép kiểu Mã hóa UTF-8 Toàn ứng dụng.
 * Chống triệt để lỗi Font Tiếng Việt (ðŸš€ / âœ…) trên Tomcat & GlassFish.
 */
@WebFilter(filterName = "EncodingFilter", urlPatterns = {"/*"})
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. Ép mã hóa UTF-8 cho Request và Response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 2. Chuyển tiếp Request cho Filter/Servlet tiếp theo
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
