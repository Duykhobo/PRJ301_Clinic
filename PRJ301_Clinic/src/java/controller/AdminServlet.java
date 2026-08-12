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

        // 2. FETCH DATA FOR DASHBOARD
        RevenueReport revenueReport = appointmentDAO.getRevenueReport(startDate, endDate);
        List<User> usersList = userDAO.findAll();
        List<Service> servicesList = serviceDAO.findAllForAdmin();
        List<ClinicSetting> settingsList = clinicSettingDAO.getAllSettings();
        Map<String, String> settingsMap = clinicSettingDAO.getSettingsMap();

        // 3. SET REQUEST ATTRIBUTES
        request.setAttribute("startDate", startLocalDate.toString());
        request.setAttribute("endDate", endLocalDate.toString());
        request.setAttribute("revenueReport", revenueReport);
        request.setAttribute("usersList", usersList);
        request.setAttribute("servicesList", servicesList);
        request.setAttribute("settingsList", settingsList);
        request.setAttribute("settingsMap", settingsMap);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

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

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
