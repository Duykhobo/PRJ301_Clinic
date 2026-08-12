<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đăng Nhập | PRJ301 Clinic</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex flex-column min-vh-100">

<%-- Dynamic Navbar Component --%>
<jsp:include page="/WEB-INF/views/components/navbar.jsp" />

<div class="container my-auto py-4">
    <div class="glass-card animate-fade-in mx-auto overflow-hidden p-0" style="max-width: 960px; width: 100%;">
        <div class="row g-0">
            
            <%-- Cột Trái: Banner Splash Thương Hiệu Sang Trọng --%>
            <div class="col-lg-6 d-none d-lg-block position-relative">
                <img src="${pageContext.request.contextPath}/assets/images/auth_splash.jpg" 
                     alt="PRJ301 Clinic Lounge" class="w-100 h-100 object-fit-cover position-absolute top-0 start-0">
                <div class="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-between p-5 text-white" 
                     style="background: linear-gradient(135deg, rgba(15, 23, 42, 0.85) 0%, rgba(30, 41, 59, 0.75) 100%);">
                    
                    <div>
                        <div class="d-flex align-items-center gap-2 fw-bold fs-4 mb-3">
                            <div class="brand-icon-box d-flex align-items-center justify-content-center rounded-3 text-white shadow-sm" style="width: 38px; height: 38px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);">
                                <i class="fa-solid fa-notes-medical fs-6"></i>
                            </div>
                            <span>PRJ301 <span class="text-info">Clinic & Spa</span></span>
                        </div>
                        <h3 class="fw-bold display-6 mb-3">Chào Mừng Bạn Quay Trở Lại!</h3>
                        <p class="text-muted small">Đăng nhập để xem lịch sử khám, quản lý lịch hẹn và thanh toán VietQR SePay tiện lợi 24/7.</p>
                    </div>

                    <div class="d-flex flex-column gap-2 small">
                        <div class="d-flex align-items-center gap-2 text-info">
                            <i class="fa-solid fa-shield-halved"></i>
                            <span>Bảo mật tài khoản chuẩn mã hóa BCrypt 100%</span>
                        </div>
                        <div class="d-flex align-items-center gap-2 text-info">
                            <i class="fa-solid fa-clock"></i>
                            <span>Đặt ca khám rảnh 60 phút chống trùng slot</span>
                        </div>
                    </div>
                </div>
            </div>

            <%-- Cột Phải: Form Đăng Nhập Glassmorphism --%>
            <div class="col-lg-6 p-4 p-md-5 d-flex flex-column justify-content-center">
                <h3 class="fw-bold mb-2">Đăng Nhập Tài Khoản</h3>
                <p class="text-muted small mb-4">Vui lòng nhập Username và Mật khẩu để tiếp tục</p>

                <%-- Nhúng Component Thông Báo Lỗi Tái Sử Dụng --%>
                <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

                <form action="${pageContext.request.contextPath}/MainController" method="POST">
                    <input type="hidden" name="action" value="login">
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <c:if test="${not empty param.redirect}">
                        <input type="hidden" name="redirect" value="${param.redirect}">
                    </c:if>

                    <div class="mb-3">
                        <label class="form-label text-muted">Tên Đăng Nhập (*)</label>
                        <div class="input-group">
                            <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass" style="border-right: none;">
                                <i class="fa-solid fa-user"></i>
                            </span>
                            <input type="text" name="username" class="form-control form-control-glass border-start-0 ps-0" value="${username}" required autofocus placeholder="Nhập tên đăng nhập">
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label text-muted">Mật Khẩu (*)</label>
                        <div class="input-password-wrapper">
                            <div class="input-group">
                                <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass" style="border-right: none;">
                                    <i class="fa-solid fa-lock"></i>
                                </span>
                                <input type="password" id="loginPassword" name="password" class="form-control form-control-glass border-start-0 ps-0 pe-5" required placeholder="Nhập mật khẩu">
                            </div>
                            <button type="button" class="btn-password-toggle me-2" onclick="togglePassword('loginPassword', this)" title="Hiện mật khẩu">
                                <i class="fa-solid fa-eye"></i>
                            </button>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary-gradient w-100 py-2 fs-6 mb-3">
                        <i class="fa-solid fa-right-to-bracket me-2"></i>Bấm Đăng Nhập
                    </button>
                </form>

                <div class="text-center mt-3">
                    <span class="text-muted small">Chưa có tài khoản?</span>
                    <a href="${pageContext.request.contextPath}/MainController?action=register-page" class="text-info text-decoration-none fw-semibold ms-1">Đăng ký tài khoản mới</a>
                </div>
            </div>

        </div>
    </div>
</div>

<%-- Dynamic Footer Component --%>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />

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