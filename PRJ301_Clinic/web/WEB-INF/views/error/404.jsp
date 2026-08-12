<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Không tìm thấy trang | PRJ301 Clinic</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/error.css" rel="stylesheet">
</head>
<body>

<div class="error-card">
    <div class="error-code code-404">404</div>
    <h3 class="mb-3">Không Tìm Thấy Trang!</h3>
    <p class="text-slate-400 mb-4">
        Đường dẫn bạn yêu cầu không tồn tại hoặc đã được chuyển sang địa chỉ mới trong hệ thống Phòng khám.
    </p>

    <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn-home mt-2">
        <i class="fa-solid fa-house me-2"></i>Quay về Trang Chủ
    </a>
</div>

</body>
</html>
