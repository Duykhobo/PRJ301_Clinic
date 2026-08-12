<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 - Truy cập bị từ chối | PRJ301 Clinic</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/error.css" rel="stylesheet">
</head>
<body>

<div class="error-card">
    <div class="error-code code-403">403</div>
    <h3 class="mb-3 font-semibold">Truy cập bị Từ chối!</h3>
    <p class="text-slate-400 mb-4">
        Bạn không có quyền hạn truy cập vào khu vực nội bộ này. Vui lòng quay về trang chủ hoặc đăng nhập tài khoản có quyền truy cập phù hợp.
    </p>

    <div class="d-flex flex-column flex-sm-row gap-3 justify-content-center mt-4">
        <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-custom btn-home">
            <i class="fa-solid fa-house me-2"></i>Quay về Trang Chủ
        </a>
        <a href="${pageContext.request.contextPath}/MainController?action=login-page" class="btn btn-custom btn-login">
            <i class="fa-solid fa-right-to-bracket me-2"></i>Đăng Nhập Tài Khoản Khác
        </a>
    </div>
</div>

</body>
</html>
