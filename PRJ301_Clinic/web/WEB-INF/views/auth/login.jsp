<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <title>Đăng Nhập | <c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Phòng Khám & Spa')}"/></title>
        <jsp:include page="/WEB-INF/views/components/head.jsp" />
    </head>
    <body class="d-flex flex-column min-vh-100">

        <%-- Dynamic Navbar Component --%>
        <jsp:include page="/WEB-INF/views/components/navbar.jsp" />

        <div class="container my-auto py-4">
            <div class="glass-card animate-fade-in mx-auto overflow-hidden p-0" style="max-width: 960px; width: 100%;">
                <div class="row g-0">

                    <%-- Cột Trái: Banner Splash Thương Hiệu Y Tế --%>
                    <div class="col-lg-6 d-none d-lg-block position-relative">
                        <img src="${pageContext.request.contextPath}/assets/images/auth_splash.jpg"
                             alt="Clinic Lounge" class="w-100 h-100 object-fit-cover position-absolute top-0 start-0">
                        <div class="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-between p-5 text-white"
                             style="background: linear-gradient(135deg, rgba(11, 19, 43, 0.88) 0%, rgba(15, 23, 42, 0.82) 100%);">

                            <div>
                                <div class="d-flex align-items-center gap-2 fw-bold fs-4 mb-3">
                                    <div class="brand-icon-box d-flex align-items-center justify-content-center rounded-3 text-white shadow-sm" style="width: 38px; height: 38px; background: linear-gradient(135deg, #0ea5e9 0%, #10b981 100%);">
                                        <i class="fa-solid fa-heart-pulse fs-6"></i>
                                    </div>
                                    <span><c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Phòng Khám & Spa')}"/></span>
                                </div>
                                <h3 class="fw-bold display-6 mb-3" data-i18n="auth_login_banner_title">Chào Mừng Bạn Quay Trở Lại!</h3>
                                <p class="text-muted small" data-i18n="auth_login_banner_desc">Đăng nhập tài khoản bệnh nhân để xem nhật ký khám y tế, quản lý lịch hẹn và thanh toán VietQR SePay tự động 24/7.</p>
                            </div>

                            <div class="d-flex flex-column gap-2 small">
                                <div class="d-flex align-items-center gap-2 text-cyan">
                                    <i class="fa-solid fa-shield-halved text-emerald"></i>
                                    <span data-i18n="auth_login_feat1">Bảo mật dữ liệu y tế chuẩn mã hóa BCrypt 100%</span>
                                </div>
                                <div class="d-flex align-items-center gap-2 text-cyan">
                                    <i class="fa-solid fa-clock text-emerald"></i>
                                    <span data-i18n="auth_login_feat2">Đặt ca khám rảnh 60 phút chống trùng slot tự động</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%-- Cột Phải: Form Đăng Nhập Glassmorphism --%>
                    <div class="col-lg-6 p-4 p-md-5 d-flex flex-column justify-content-center">
                        <div class="mb-3">
                            <span class="badge bg-cyan bg-opacity-20 text-cyan rounded-pill px-3 py-1.5 fs-8 fw-semibold" data-i18n="badge_security">
                                <i class="fa-solid fa-shield-halved me-1"></i>Bảo Mật Chuẩn BCrypt
                            </span>
                        </div>

                        <h3 class="fw-bold mb-2 text-white" data-i18n="auth_login_title">Đăng Nhập Tài Khoản</h3>
                        <p class="text-muted small mb-4" data-i18n="auth_login_subtitle">Vui lòng nhập Tên đăng nhập và Mật khẩu để tiếp tục</p>

                        <%-- Component Thông Báo Lỗi --%>
                        <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

                        <form action="${pageContext.request.contextPath}/MainController" method="POST" novalidate="true">
                            <input type="hidden" name="action" value="login">
                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                            <c:if test="${not empty param.redirect}">
                                <input type="hidden" name="redirect" value="${param.redirect}">
                            </c:if>

                            <div class="mb-3">
                                <label class="form-label text-muted fw-semibold" data-i18n="label_username">Tên Đăng Nhập (*)</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass ${not empty errors.username ? 'is-invalid' : ''}" style="border-right: none;">
                                        <i class="fa-solid fa-user text-cyan"></i>
                                    </span>
                                    <input type="text" name="username" class="form-control form-control-glass border-start-0 ps-0 ${not empty errors.username ? 'is-invalid' : ''}" value="${username}" required autofocus placeholder="Nhập tên đăng nhập" data-i18n="placeholder_username">
                                </div>
                                <c:if test="${not empty errors.username}">
                                    <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.username}</span></div>
                                </c:if>
                            </div>

                            <div class="mb-4">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <label class="form-label text-muted fw-semibold mb-0" data-i18n="label_password">Mật Khẩu (*)</label>
                                    <a href="${pageContext.request.contextPath}/MainController?action=forgot-password-page" class="text-cyan text-decoration-none small" tabindex="-1">
                                        <i class="fa-solid fa-key me-1"></i><span data-i18n="link_forgot_pwd">Quên mật khẩu?</span>
                                    </a>
                                </div>
                                <div class="input-password-wrapper">
                                    <div class="input-group">
                                        <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass ${not empty errors.password ? 'is-invalid' : ''}" style="border-right: none;">
                                            <i class="fa-solid fa-lock text-cyan"></i>
                                        </span>
                                        <input type="password" id="loginPassword" name="password" class="form-control form-control-glass border-start-0 ps-0 pe-5 ${not empty errors.password ? 'is-invalid' : ''}" required placeholder="Nhập mật khẩu" data-i18n="placeholder_password">
                                    </div>
                                    <button type="button" class="btn-password-toggle me-2" onclick="togglePassword('loginPassword', this)" title="Hiện mật khẩu">
                                        <i class="fa-solid fa-eye"></i>
                                    </button>
                                </div>
                                <c:if test="${not empty errors.password}">
                                    <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.password}</span></div>
                                </c:if>
                            </div>

                            <button type="submit" class="btn btn-primary-gradient w-100 py-3 fs-6 mb-3 rounded-pill">
                                <i class="fa-solid fa-right-to-bracket me-2"></i><span data-i18n="btn_submit_login">Bấm Đăng Nhập</span>
                            </button>
                        </form>

                        <div class="text-center mt-3">
                            <span class="text-muted small" data-i18n="link_no_account">Chưa có tài khoản?</span>
                            <a href="${pageContext.request.contextPath}/MainController?action=register-page" class="text-cyan text-decoration-none fw-semibold ms-1" data-i18n="link_register_now">Đăng ký tài khoản mới</a>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <script>
            function togglePassword(inputId, btn) {
                const input = document.getElementById(inputId);
                const icon = btn.querySelector('i');
                if (input.type === 'password') {
                    input.type = 'text';
                    icon.className = 'fa-solid fa-eye-slash text-cyan';
                } else {
                    input.type = 'password';
                    icon.className = 'fa-solid fa-eye';
                }
            }
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>