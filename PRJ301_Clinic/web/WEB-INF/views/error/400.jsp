<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>400 - Yêu cầu không hợp lệ | PRJ301 Clinic</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/error.css" rel="stylesheet">
</head>
<body>

<div class="error-card">
    <div class="error-code code-403" style="color: #f59e0b; text-shadow: 0 0 20px rgba(245, 158, 11, 0.4);">400</div>
    <h3 class="mb-3 text-white">Yêu Cầu Không Hợp Lệ!</h3>
    <p class="text-slate-400 mb-4" style="color: #94a3b8;">
        Dữ liệu gửi lên máy chủ không đúng định dạng hoặc thiếu các tham số bắt buộc. Vui lòng kiểm tra lại thao tác.
    </p>

    <div class="d-flex justify-content-center gap-3 mt-3">
        <a href="javascript:history.back()" class="btn btn-outline-light rounded-pill px-4 py-2" style="font-weight: 600;">
            <i class="fa-solid fa-arrow-left me-2"></i>Quay Lại Trang Trước
        </a>
        <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn-home">
            <i class="fa-solid fa-house me-2"></i>Về Trang Chủ
        </a>
    </div>
</div>

</body>
</html>
