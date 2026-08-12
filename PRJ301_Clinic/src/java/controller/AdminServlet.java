package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.AppointmentDAO;
import dao.ClinicSettingDAO;
import dao.ServiceDAO;
import dao.UserDAO;
import model.ClinicSetting;
import model.RevenueReport;
import model.Service;
import model.User;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/dashboard"})
public class AdminServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final ClinicSettingDAO clinicSettingDAO = new ClinicSettingDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    private static final int PAGE_SIZE = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. DATE RANGE FOR REVENUE REPORT (Default 30 days window)
        String startStr = request.getParameter("startDate");
        String endStr = request.getParameter("endDate");

        LocalDate endLocalDate = (endStr != null && !endStr.trim().isEmpty())
                ? LocalDate.parse(endStr) : LocalDate.now();
        LocalDate startLocalDate = (startStr != null && !startStr.trim().isEmpty())
                ? LocalDate.parse(startStr) : endLocalDate.minusDays(30);

        Date startDate = Date.valueOf(startLocalDate);
        Date endDate = Date.valueOf(endLocalDate);

        // 2. PAGINATION & TAB PARAMETERS (UX Protection for dual tables)
        int pageUser = 1;
        int pageService = 1;
        try {
            if (request.getParameter("pageUser") != null) {
                pageUser = Math.max(1, Integer.parseInt(request.getParameter("pageUser")));
            }
        } catch (NumberFormatException ignored) {}

        try {
            if (request.getParameter("pageService") != null) {
                pageService = Math.max(1, Integer.parseInt(request.getParameter("pageService")));
            }
        } catch (NumberFormatException ignored) {}

        String activeTab = request.getParameter("tab");
        if (activeTab == null || activeTab.trim().isEmpty()) {
            activeTab = "users";
        }

        // 3. FETCH PAGINATED DATA & COUNTS
        int totalUsers = userDAO.countAll();
        int totalPagesUser = Math.max(1, (int) Math.ceil((double) totalUsers / PAGE_SIZE));
        if (pageUser > totalPagesUser) pageUser = totalPagesUser;
        int offsetUser = (pageUser - 1) * PAGE_SIZE;

        int totalServices = serviceDAO.countAllForAdmin();
        int totalPagesService = Math.max(1, (int) Math.ceil((double) totalServices / PAGE_SIZE));
        if (pageService > totalPagesService) pageService = totalPagesService;
        int offsetService = (pageService - 1) * PAGE_SIZE;

        RevenueReport revenueReport = appointmentDAO.getRevenueReport(startDate, endDate);
        List<User> usersList = userDAO.findPaginated(offsetUser, PAGE_SIZE);
        List<Service> servicesList = serviceDAO.findAllForAdminPaginated(offsetService, PAGE_SIZE);
        List<ClinicSetting> settingsList = clinicSettingDAO.getAllSettings();
        Map<String, String> settingsMap = clinicSettingDAO.getSettingsMap();

        // 4. SET REQUEST ATTRIBUTES
        request.setAttribute("startDate", startLocalDate.toString());
        request.setAttribute("endDate", endLocalDate.toString());
        request.setAttribute("revenueReport", revenueReport);
        request.setAttribute("usersList", usersList);
        request.setAttribute("servicesList", servicesList);
        request.setAttribute("settingsList", settingsList);
        request.setAttribute("settingsMap", settingsMap);

        // Pagination Attributes
        request.setAttribute("currentPageUser", pageUser);
        request.setAttribute("totalPagesUser", totalPagesUser);
        request.setAttribute("currentPageService", pageService);
        request.setAttribute("totalPagesService", totalPagesService);
        request.setAttribute("activeTab", activeTab);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        String pageUserParam = request.getParameter("pageUser");
        String pageServiceParam = request.getParameter("pageService");
        String tabParam = request.getParameter("tab");

        switch (action) {
            case "toggle-user-status": {
                int userId = Integer.parseInt(request.getParameter("userId"));
                userDAO.toggleStatus(userId);
                break;
            }
            case "update-user-role": {
                int userId = Integer.parseInt(request.getParameter("userId"));
                String newRole = request.getParameter("role");
                if (newRole != null && !newRole.trim().isEmpty()) {
                    userDAO.updateRole(userId, newRole.trim().toUpperCase());
                }
                break;
            }
            case "toggle-service-status": {
                int serviceId = Integer.parseInt(request.getParameter("serviceId"));
                serviceDAO.toggleStatus(serviceId);
                break;
            }
            case "add-service": {
                String serviceName = request.getParameter("serviceName");
                String priceStr = request.getParameter("price");
                String durationStr = request.getParameter("durationMinutes");
                String description = request.getParameter("description");
                String imageUrl = request.getParameter("imageUrl");

                if (serviceName != null && !serviceName.trim().isEmpty() && priceStr != null) {
                    BigDecimal price = new BigDecimal(priceStr);
                    int duration = durationStr != null && !durationStr.trim().isEmpty()
                            ? Integer.parseInt(durationStr) : 60;

                    Service newService = new Service();
                    newService.setServiceName(serviceName.trim());
                    newService.setPrice(price);
                    newService.setDurationMinutes(duration);
                    newService.setDescription(description != null ? description.trim() : "");
                    newService.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : "assets/images/default-service.jpg");
                    newService.setStatus(true);

                    serviceDAO.insert(newService);
                }
                break;
            }
            case "update-settings": {
                String[] keys = {"clinic_name", "hotline", "address", "bank_name", "bank_account", "bank_owner"};
                for (String key : keys) {
                    String val = request.getParameter(key);
                    if (val != null) {
                        clinicSettingDAO.updateSetting(key, val.trim());
                    }
                }
                break;
            }
        }

        String redirectUrl = request.getContextPath() + "/admin/dashboard?pageUser=" + (pageUserParam != null ? pageUserParam : "1")
                + "&pageService=" + (pageServiceParam != null ? pageServiceParam : "1")
                + "&tab=" + (tabParam != null ? tabParam : "users");

        response.sendRedirect(redirectUrl);
    }
}
