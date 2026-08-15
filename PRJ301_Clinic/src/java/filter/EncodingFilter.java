package filter;

import java.io.IOException;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import dao.ClinicSettingDAO;

/**
 * EncodingFilter - Bộ lọc Ép kiểu Mã hóa UTF-8 & Nạp Cấu Hình CSDL Toàn Ứng Dụng (Navbar, Footer, Booking, Payment...).
 */
@WebFilter(filterName = "EncodingFilter", urlPatterns = {"/*"})
public class EncodingFilter implements Filter {

    private final ClinicSettingDAO clinicSettingDAO = new ClinicSettingDAO();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. Ép mã hóa UTF-8 cho Request và Response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 2. Nạp cấu hình động ClinicSettings cho TOÀN BỘ CÁC TRANG WEB
        try {
            Map<String, String> settingsMap = clinicSettingDAO.getSettingsMap();
            request.setAttribute("clinicSettings", settingsMap);
            request.setAttribute("settingsMap", settingsMap);
        } catch (Exception ignored) {}

        // 3. Chuyển tiếp Request cho Filter/Servlet tiếp theo
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
