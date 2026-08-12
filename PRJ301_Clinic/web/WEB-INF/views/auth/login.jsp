<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đăng Nhập | PRJ301 Clinic</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex align-items-center justify-content-center">

<div class="glass-card animate-fade-in" style="max-width: 420px; width: 90%;">
    <h3 class="text-center fw-bold mb-4">Đăng Nhập</h3>

    <%-- Nhúng Component Thông Báo Lỗi Tái Sử Dụng --%>
    <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

    <form action="${pageContext.request.contextPath}/MainController" method="POST">
        <input type="hidden" name="action" value="login">
        <input type="hidden" name="csrfToken" value="${csrfToken}">

        <div class="mb-3">
            <label class="form-label text-muted">Tên Đăng Nhập</label>
            <input type="text" name="username" class="form-control form-control-glass" value="${username}" required autofocus placeholder="Nhập tên đăng nhập">
        </div>

        <div class="mb-4">
            <label class="form-label text-muted">Mật Khẩu</label>
            <div class="input-password-wrapper">
                <input type="password" id="loginPassword" name="password" class="form-control form-control-glass pe-5" required placeholder="Nhập mật khẩu">
                <button type="button" class="btn-password-toggle" onclick="togglePassword('loginPassword', this)" title="Hiện mật khẩu">
                    <i class="fa-solid fa-eye"></i>
                </button>
            </div>
        </div>

        <button type="submit" class="btn btn-primary-gradient w-100 mb-3">
            <i class="fa-solid fa-right-to-bracket me-2"></i>Đăng Nhập
        </button>
    </form>

    <div class="text-center mt-3">
        <span class="text-muted">Chưa có tài khoản?</span>
        <a href="${pageContext.request.contextPath}/MainController?action=register-page" class="text-info text-decoration-none ms-1">Đăng ký ngay</a>
    </div>
</div>

<script>
    function togglePassword(inputId, btn) {
        const input = document.getElementById(inputId);
        const icon = btn.querySelector('i');
        if (input.type === 'password') {
            input.type = 'text';
            icon.className = 'fa-solid fa-eye-slash';
        } else {
            input.type = 'password';
            icon.className = 'fa-solid fa-eye';
        }
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>