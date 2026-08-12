<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đăng Ký Tài Khoản | PRJ301 Clinic</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex align-items-center justify-content-center py-5">

<div class="glass-card animate-fade-in" style="max-width: 500px; width: 90%;">
    <h3 class="text-center fw-bold mb-4">Đăng Ký Tài Khoản Bệnh Nhân</h3>

    <%-- Nhúng Component Thông Báo Lỗi Tái Sử Dụng --%>
    <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

    <form action="${pageContext.request.contextPath}/MainController" method="POST">
        <input type="hidden" name="action" value="register">
        <input type="hidden" name="csrfToken" value="${csrfToken}">

        <div class="mb-3">
            <label class="form-label text-muted">Tên Đăng Nhập (*)</label>
            <input type="text" name="username" class="form-control form-control-glass" value="${username}" required placeholder="vd: patient123">
        </div>

        <div class="mb-3">
            <label class="form-label text-muted">Họ và Tên (*)</label>
            <input type="text" name="fullname" class="form-control form-control-glass" value="${fullname}" required placeholder="vd: Nguyễn Văn A">
        </div>

        <div class="mb-3">
            <label class="form-label text-muted">Số Điện Thoại (*)</label>
            <input type="tel" name="phone" class="form-control form-control-glass" value="${phone}" required placeholder="vd: 0901234567">
        </div>

        <div class="mb-3">
            <label class="form-label text-muted">Email (Tùy chọn)</label>
            <input type="email" name="email" class="form-control form-control-glass" value="${email}" placeholder="vd: email@gmail.com">
        </div>

        <div class="mb-3">
            <label class="form-label text-muted">Mật Khẩu (*)</label>
            <div class="input-password-wrapper">
                <input type="password" id="regPassword" name="password" class="form-control form-control-glass pe-5" required placeholder="Nhập mật khẩu">
                <button type="button" class="btn-password-toggle" onclick="togglePassword('regPassword', this)" title="Hiện mật khẩu">
                    <i class="fa-solid fa-eye"></i>
                </button>
            </div>
        </div>

        <div class="mb-4">
            <label class="form-label text-muted">Xác Nhận Mật Khẩu (*)</label>
            <div class="input-password-wrapper">
                <input type="password" id="regConfirmPassword" name="confirmPassword" class="form-control form-control-glass pe-5" required placeholder="Nhập lại mật khẩu">
                <button type="button" class="btn-password-toggle" onclick="togglePassword('regConfirmPassword', this)" title="Hiện mật khẩu">
                    <i class="fa-solid fa-eye"></i>
                </button>
            </div>
        </div>

        <button type="submit" class="btn btn-primary-gradient w-100 mb-3">
            <i class="fa-solid fa-user-plus me-2"></i>Đăng Ký Ngay
        </button>
    </form>

    <div class="text-center mt-3">
        <span class="text-muted">Đã có tài khoản?</span>
        <a href="${pageContext.request.contextPath}/MainController?action=login-page" class="text-info text-decoration-none ms-1">Đăng nhập</a>
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
