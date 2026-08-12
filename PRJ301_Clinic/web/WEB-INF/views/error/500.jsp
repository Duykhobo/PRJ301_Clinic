<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Lỗi Máy Chủ | PRJ301 Clinic</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/error.css" rel="stylesheet">
</head>
<body>

<div class="error-card">
    <div class="error-code code-500">500</div>
    <h3 class="mb-3">Đã Xảy Ra Lỗi Máy Chủ!</h3>
    <p class="text-slate-400 mb-3">
        Hệ thống gặp sự cố kỹ thuật trong quá trình xử lý yêu cầu. Kỹ thuật viên đã được thông báo để khắc phục.
    </p>

    <c:if test="${not empty exception}">
        <div class="alert alert-danger text-start p-3 my-3 fs-7" style="background: rgba(239, 68, 68, 0.15); border: 1px solid rgba(239, 68, 68, 0.3); border-radius: 12px;">
            <strong>Chi tiết lỗi:</strong> ${exception.message}
        </div>
    </c:if>

    <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn-home purple mt-2">
        <i class="fa-solid fa-house me-2"></i>Quay về Trang Chủ
    </a>
</div>

</body>
</html>
