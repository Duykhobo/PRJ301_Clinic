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
        if (response instanceof javax.servlet.http.HttpServletResponse && request instanceof javax.servlet.http.HttpServletRequest) {
            javax.servlet.http.HttpServletResponse httpResponse = (javax.servlet.http.HttpServletResponse) response;
            javax.servlet.http.HttpServletRequest httpRequest = (javax.servlet.http.HttpServletRequest) request;
            httpResponse.setCharacterEncoding("UTF-8");
            String uri = httpRequest.getRequestURI();
            if (uri != null) {
                if (uri.endsWith(".js")) {
                    httpResponse.setContentType("application/javascript; charset=UTF-8");
                } else if (uri.endsWith(".css")) {
                    httpResponse.setContentType("text/css; charset=UTF-8");
                }
            }
        }

        // 2. Nạp cấu hình động ClinicSettings & Ngôn Ngữ cho TOÀN BỘ CÁC TRANG WEB
        try {
            Map<String, String> settingsMap = clinicSettingDAO.getSettingsMap();
            request.setAttribute("clinicSettings", settingsMap);
            request.setAttribute("settingsMap", settingsMap);
            if (request.getServletContext() != null) {
                request.getServletContext().setAttribute("clinicSettings", settingsMap);
                request.getServletContext().setAttribute("settingsMap", settingsMap);
            }

            if (request instanceof javax.servlet.http.HttpServletRequest) {
                javax.servlet.http.HttpServletRequest httpRequest = (javax.servlet.http.HttpServletRequest) request;
                javax.servlet.http.HttpSession session = httpRequest.getSession(false);
                String lang = (session != null && session.getAttribute("LANG") != null)
                        ? (String) session.getAttribute("LANG")
                        : "vi";
                request.setAttribute("CURRENT_LANG", lang);

                if (session != null && session.getAttribute(constant.SystemConstant.SESSION_USER) != null) {
                    model.User u = (model.User) session.getAttribute(constant.SystemConstant.SESSION_USER);
                    if (constant.RoleConstant.PATIENT.equals(u.getRole())) {
                        dao.LoyaltyDAO loyaltyDAO = new dao.LoyaltyDAO();
                        request.setAttribute("loyaltyProfile", loyaltyDAO.getLoyaltyProfileByPatient(u.getId()));
                    }
                }
            }
        } catch (Exception ignored) {}

        // 3. Chuyển tiếp Request cho Filter/Servlet tiếp theo
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
