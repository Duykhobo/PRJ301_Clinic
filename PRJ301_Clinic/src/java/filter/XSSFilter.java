package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import util.XSSRequestWrapper;

/**
 * XSSFilter - Bộ Lọc Bảo Mật Toàn Hệ Thống phòng chống tấn công XSS.
 * Tự động kích hoạt bọc tất cả ServletRequest với XSSRequestWrapper.
 */
@WebFilter(filterName = "XSSFilter", urlPatterns = {"/*"})
public class XSSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
