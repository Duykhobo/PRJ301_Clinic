<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>500 - Lỗi Máy Chủ Y Tế | PRJ301 Clinic</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
    <link href="${pageContext.request.contextPath}/assets/css/error.css" rel="stylesheet">
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100">

<div class="error-card glass-card text-center p-5" style="max-width: 520px; width: 90%;">
    <div class="error-code code-500 text-cyan display-1 fw-extrabold mb-2">500</div>
    <h3 class="fw-bold text-white mb-3">Đã Xảy Ra Lỗi Máy Chủ!</h3>
    <p class="text-muted small mb-4">
        Hệ thống y tế gặp sự cố kỹ thuật tạm thời trong quá trình xử lý yêu cầu. Đội ngũ kỹ thuật viên đã được thông báo tự động.
    </p>

    <c:if test="${not empty exception}">
        <div class="alert alert-danger text-start p-3 my-3 fs-7" style="background: rgba(239, 68, 68, 0.15); border: 1px solid rgba(239, 68, 68, 0.3); border-radius: 12px;">
            <strong>Chi tiết lỗi y tế:</strong> ${exception.message}
        </div>
    </c:if>

    <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-primary-gradient px-4 py-3 rounded-pill fw-bold">
        <i class="fa-solid fa-house me-2"></i>Quay Về Trang Chủ Y Tế
    </a>
</div>

</body>
</html>

