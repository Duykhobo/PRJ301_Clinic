package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
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
import util.PaginationUtil;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/dashboard"})
public class AdminServlet extends BaseRoleServlet {

    private UserDAO userDAO;
    private ServiceDAO serviceDAO;
    private ClinicSettingDAO clinicSettingDAO;
    private AppointmentDAO appointmentDAO;

    private static final int PAGE_SIZE = 5;

    @Override
    public void init() throws ServletException {
        this.userDAO = new UserDAO();
        this.serviceDAO = new ServiceDAO();
        this.clinicSettingDAO = new ClinicSettingDAO();
        this.appointmentDAO = new AppointmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User loginUser = requireRole(request, response, "ADMIN");
        if (loginUser == null) {
            return;
        }

        // 1. DATE RANGE FOR REVENUE REPORT
        String startStr = request.getParameter("startDate");
        String endStr = request.getParameter("endDate");

        LocalDate endLocalDate = (endStr != null && !endStr.trim().isEmpty())
                ? LocalDate.parse(endStr) : LocalDate.now();
        LocalDate startLocalDate = (startStr != null && !startStr.trim().isEmpty())
                ? LocalDate.parse(startStr) : endLocalDate.minusDays(30);

        Date startDate = Date.valueOf(startLocalDate);
        Date endDate = Date.valueOf(endLocalDate);

        // 2. PAGINATION & TAB PARAMETERS
        int pageUser = PaginationUtil.parsePage(request, "pageUser");
        int pageService = PaginationUtil.parsePage(request, "pageService");

        String activeTab = request.getParameter("tab");
        if (activeTab == null || activeTab.trim().isEmpty()) {
            activeTab = "users";
        }

        // 3. FETCH PAGINATED DATA & COUNTS
        int totalUsers = userDAO.countAll();
        int totalPagesUser = PaginationUtil.totalPages(totalUsers, PAGE_SIZE);
        pageUser = Math.min(pageUser, totalPagesUser);
        int offsetUser = PaginationUtil.offset(pageUser, PAGE_SIZE);

        int totalServices = serviceDAO.countAllForAdmin();
        int totalPagesService = PaginationUtil.totalPages(totalServices, PAGE_SIZE);
        pageService = Math.min(pageService, totalPagesService);
        int offsetService = PaginationUtil.offset(pageService, PAGE_SIZE);

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

        request.setAttribute("currentPageUser", pageUser);
        request.setAttribute("totalPagesUser", totalPagesUser);
        request.setAttribute("currentPageService", pageService);
        request.setAttribute("totalPagesService", totalPagesService);
        request.setAttribute("activeTab", activeTab);

        request.getRequestDispatcher(constant.RouterConstant.ADMIN_DASHBOARD_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User loginUser = requireRole(request, response, "ADMIN");
        if (loginUser == null) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "true".equalsIgnoreCase(request.getParameter("ajax"));

        String action = request.getParameter("action");
        if (action == null) action = "";

        String pageUserParam = request.getParameter("pageUser");
        String pageServiceParam = request.getParameter("pageService");
        String tabParam = request.getParameter("tab");

        boolean success = false;
        String message = "Thao tác thất bại!";
        boolean newStatus = false;
        String newRoleStr = "";

        switch (action) {
            case "toggle-user-status": {
                String userIdStr = request.getParameter("userId");
                if (userIdStr != null && !userIdStr.trim().isEmpty() && !"undefined".equalsIgnoreCase(userIdStr.trim())) {
                    try {
                        int userId = Integer.parseInt(userIdStr.trim());
                        success = userDAO.toggleStatus(userId);
                        User updatedUser = userDAO.findById(userId);
                        newStatus = updatedUser != null && updatedUser.isStatus();
                        message = newStatus ? "Đã MỞ KHÓA tài khoản #" + userId : "Đã KHÓA tài khoản #" + userId;
                    } catch (NumberFormatException e) {
                        message = "Mã người dùng không hợp lệ!";
                    }
                }
                break;
            }
            case "update-user-role": {
                String userIdStr = request.getParameter("userId");
                String newRole = request.getParameter("role");
                if (userIdStr != null && !userIdStr.trim().isEmpty() && !"undefined".equalsIgnoreCase(userIdStr.trim())) {
                    try {
                        int userId = Integer.parseInt(userIdStr.trim());
                        if (newRole != null && !newRole.trim().isEmpty()) {
                            newRoleStr = newRole.trim().toUpperCase();
                            success = userDAO.updateRole(userId, newRoleStr);
                            message = "Đã cập nhật vai trò người dùng #" + userId + " thành " + newRoleStr;
                        }
                    } catch (NumberFormatException e) {
                        message = "Mã người dùng không hợp lệ!";
                    }
                }
                break;
            }
            case "toggle-service-status": {
                String serviceIdStr = request.getParameter("serviceId");
                if (serviceIdStr != null && !serviceIdStr.trim().isEmpty() && !"undefined".equalsIgnoreCase(serviceIdStr.trim())) {
                    try {
                        int serviceId = Integer.parseInt(serviceIdStr.trim());
                        success = serviceDAO.toggleStatus(serviceId);
                        Service updatedSvc = serviceDAO.findById(serviceId);
                        newStatus = updatedSvc != null && updatedSvc.isStatus();
                        message = newStatus ? "Đã HIỂN THỊ dịch vụ #" + serviceId : "Đã ẨN dịch vụ #" + serviceId;
                    } catch (NumberFormatException e) {
                        message = "Mã dịch vụ không hợp lệ!";
                    }
                }
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

                    success = serviceDAO.insert(newService);
                    message = "Thêm mới dịch vụ thành công!";
                }
                break;
            }
            case "update-settings": {
                Map<String, String> keyMapping = new HashMap<>();
                // Form field -> DB setting key mapping
                keyMapping.put("CLINIC_NAME", "CLINIC_NAME");
                keyMapping.put("clinic_name", "CLINIC_NAME");
                keyMapping.put("CLINIC_HOTLINE", "CLINIC_HOTLINE");
                keyMapping.put("hotline", "CLINIC_HOTLINE");
                keyMapping.put("CLINIC_EMAIL", "CLINIC_EMAIL");
                keyMapping.put("CLINIC_ADDRESS", "CLINIC_ADDRESS");
                keyMapping.put("address", "CLINIC_ADDRESS");
                keyMapping.put("OPENING_HOURS", "OPENING_HOURS");
                keyMapping.put("CLINIC_TIME_SLOTS", "CLINIC_TIME_SLOTS");
                keyMapping.put("time_slots", "CLINIC_TIME_SLOTS");
                keyMapping.put("SEPAY_BANK_NAME", "SEPAY_BANK_NAME");
                keyMapping.put("bank_name", "SEPAY_BANK_NAME");
                keyMapping.put("SEPAY_BANK_ACC", "SEPAY_BANK_ACC");
                keyMapping.put("bank_account", "SEPAY_BANK_ACC");
                keyMapping.put("SEPAY_ACCOUNT_HOLDER", "SEPAY_ACCOUNT_HOLDER");
                keyMapping.put("bank_owner", "SEPAY_ACCOUNT_HOLDER");

                for (Map.Entry<String, String> entry : keyMapping.entrySet()) {
                    String paramName = entry.getKey();
                    String dbKey = entry.getValue();
                    String val = request.getParameter(paramName);
                    if (val != null && !val.trim().isEmpty()) {
                        clinicSettingDAO.updateSetting(dbKey, val.trim());
                        if ("CLINIC_TIME_SLOTS".equals(dbKey)) {
                            clinicSettingDAO.updateSetting("time_slots", val.trim());
                        }
                    }
                }
                success = true;
                message = "Cập nhật cấu hình hệ thống thành công!";
                break;
            }
        }

        if (isAjax) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(String.format(
                    "{\"success\":%b,\"message\":\"%s\",\"newStatus\":%b,\"newRole\":\"%s\"}",
                    success, message, newStatus, newRoleStr
            ));
            return;
        }

        String redirectUrl = request.getContextPath() + constant.RouterConstant.DASHBOARD_ADMIN + "?pageUser=" + (pageUserParam != null ? pageUserParam : "1")
                + "&pageService=" + (pageServiceParam != null ? pageServiceParam : "1")
                + "&tab=" + (tabParam != null ? tabParam : "users");

        response.sendRedirect(redirectUrl);
    }
}
