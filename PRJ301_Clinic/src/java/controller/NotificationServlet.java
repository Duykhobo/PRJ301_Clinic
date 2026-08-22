package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.SystemConstant;
import dao.NotificationDAO;
import model.Notification;
import model.User;
import util.JsonUtil;

/**
 * NotificationServlet - API Endpoint phục vụ Polling thông báo thời gian thực (/api/notifications).
 */
@WebServlet(name = "NotificationServlet", urlPatterns = {"/api/notifications"})
public class NotificationServlet extends HttpServlet {

    private NotificationDAO notificationDAO;

    @Override
    public void init() throws ServletException {
        this.notificationDAO = new NotificationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SystemConstant.SESSION_USER) == null) {
            response.getWriter().write("{\"success\":false,\"unreadCount\":0,\"notifications\":[]}");
            return;
        }

        User loginUser = (User) session.getAttribute(SystemConstant.SESSION_USER);
        int unreadCount = notificationDAO.countUnreadByUserId(loginUser.getId());
        List<Notification> list = notificationDAO.findByUserId(loginUser.getId(), 15);

        StringBuilder json = new StringBuilder();
        json.append("{\"success\":true,\"unreadCount\":").append(unreadCount).append(",\"notifications\":[");
        for (int i = 0; i < list.size(); i++) {
            Notification n = list.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                .append("\"id\":").append(n.getId()).append(",")
                .append("\"title\":\"").append(JsonUtil.escapeJson(n.getTitle())).append("\",")
                .append("\"message\":\"").append(JsonUtil.escapeJson(n.getMessage())).append("\",")
                .append("\"type\":\"").append(JsonUtil.escapeJson(n.getType())).append("\",")
                .append("\"isRead\":").append(n.isRead()).append(",")
                .append("\"link\":\"").append(n.getLink() != null ? JsonUtil.escapeJson(n.getLink()) : "").append("\",")
                .append("\"timeAgo\":\"").append(JsonUtil.escapeJson(n.getTimeAgo())).append("\"")
                .append("}");
        }
        json.append("]}");

        response.getWriter().write(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SystemConstant.SESSION_USER) == null) {
            response.getWriter().write("{\"success\":false,\"message\":\"Chưa đăng nhập!\"}");
            return;
        }

        User loginUser = (User) session.getAttribute(SystemConstant.SESSION_USER);
        String action = request.getParameter("action");

        if ("mark-all-read".equals(action)) {
            boolean ok = notificationDAO.markAllAsRead(loginUser.getId());
            response.getWriter().write("{\"success\":" + ok + ",\"message\":\"Đã đánh dấu tất cả đã đọc!\"}");
            return;
        }

        if ("mark-read".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ok = notificationDAO.markAsRead(id, loginUser.getId());
                response.getWriter().write("{\"success\":" + ok + "}");
                return;
            } catch (Exception e) {
                response.getWriter().write("{\"success\":false,\"message\":\"ID không hợp lệ\"}");
                return;
            }
        }

        response.getWriter().write("{\"success\":false,\"message\":\"Hành động không hợp lệ\"}");
    }
}