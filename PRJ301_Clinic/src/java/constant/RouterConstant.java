package constant;

/**
 * RouterConstant - Quản lý các Hằng số Đường dẫn JSP Views & Servlet Routing URLs.
 */
public class RouterConstant {

    // JSP View Files
    public static final String LOGIN_JSP = "/WEB-INF/views/auth/login.jsp";
    public static final String REGISTER_JSP = "/WEB-INF/views/auth/register.jsp";
    public static final String HOME_JSP = "/WEB-INF/views/public/home.jsp";
    public static final String BOOKING_JSP = "/WEB-INF/views/patient/booking.jsp";
    public static final String PAYMENT_JSP = "/WEB-INF/views/patient/payment.jsp";
    public static final String HISTORY_JSP = "/WEB-INF/views/patient/history.jsp";
    
    // Error JSPs Encapsulated inside WEB-INF (Chống gõ trực tiếp từ URL)
    public static final String ERROR_403_JSP = "/WEB-INF/views/error/403.jsp";
    public static final String ERROR_404_JSP = "/WEB-INF/views/error/404.jsp";
    public static final String ERROR_500_JSP = "/WEB-INF/views/error/500.jsp";

    // Servlet Routing URLs
    public static final String ROUTE_MAIN_CONTROLLER = "MainController";
    public static final String ROUTE_LOGIN = "/login";
    public static final String ROUTE_REGISTER = "/register";
    public static final String ROUTE_LOGOUT = "/logout";
    public static final String ROUTE_HOME = "/home";
    public static final String ROUTE_BOOKING = "/booking";
    public static final String ROUTE_HISTORY = "/history";
    public static final String ROUTE_SEPAY_WEBHOOK = "/sepay-webhook";
    
    // Role Redirect Dashboards
    public static final String DASHBOARD_ADMIN = "/admin/dashboard";
    public static final String DASHBOARD_DOCTOR = "/doctor/dashboard";
    public static final String DASHBOARD_RECEPTIONIST = "/receptionist/dashboard";
    public static final String DASHBOARD_PATIENT = "/patient/history";
}
