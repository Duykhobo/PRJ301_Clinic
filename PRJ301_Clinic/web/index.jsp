<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Tự động chuyển hướng từ gốc ứng dụng (/) tới MainController
    response.sendRedirect(request.getContextPath() + "/MainController");
%>
