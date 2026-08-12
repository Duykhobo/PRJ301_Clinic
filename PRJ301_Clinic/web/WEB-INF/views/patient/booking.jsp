<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đặt Lịch Kám | PRJ301 Clinic</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex align-items-center justify-content-center py-5">

<div class="glass-card animate-fade-in text-center p-5" style="max-width: 600px; width: 90%;">
    <h3 class="fw-bold mb-3"><i class="fa-solid fa-calendar-check text-info me-2"></i>Đặt Lịch Khám Trực Tuyến</h3>
    <p class="text-muted mb-4">Trang đặt lịch hẹn khám nha khoa & spa đang được hoàn thiện.</p>
    <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-primary-gradient">
        <i class="fa-solid fa-house me-2"></i>Quay về Trang Chủ
    </a>
</div>

</body>
</html>
