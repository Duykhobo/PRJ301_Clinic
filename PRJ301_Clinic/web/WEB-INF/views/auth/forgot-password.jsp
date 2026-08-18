<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Quên Mật Khẩu | PRJ301 Clinic &amp; Spa</title>
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
                     alt="PRJ301 Clinic Lounge" class="w-100 h-100 object-fit-cover position-absolute top-0 start-0">
                <div class="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-between p-5 text-white" 
                     style="background: linear-gradient(135deg, rgba(11, 19, 43, 0.88) 0%, rgba(15, 23, 42, 0.82) 100%);">
                    
                    <div>
                        <div class="d-flex align-items-center gap-2 fw-bold fs-4 mb-3">
                            <div class="brand-icon-box d-flex align-items-center justify-content-center rounded-3 text-white shadow-sm" style="width: 38px; height: 38px; background: linear-gradient(135deg, #0ea5e9 0%, #10b981 100%);">
                                <i class="fa-solid fa-heart-pulse fs-6"></i>
                            </div>
                            <span>PRJ301 <span class="text-cyan">Clinic & Spa</span></span>
                        </div>
                        <h3 class="fw-bold display-6 mb-3">Khôi Phục Mật Khẩu Dễ Dàng</h3>
                        <p class="text-muted small">Nhập địa chỉ Email đăng ký tài khoản của bạn để hệ thống tự động gửi mật khẩu tạm thời bảo mật qua hòm thư.</p>
                    </div>

                    <div class="d-flex flex-column gap-2 small">
                        <div class="d-flex align-items-center gap-2 text-cyan">
                            <i class="fa-solid fa-shield-halved text-emerald"></i>
                            <span>Hệ thống kiểm tra Email tự động &amp; Khôi phục bảo mật</span>
                        </div>
                        <div class="d-flex align-items-center gap-2 text-cyan">
                            <i class="fa-solid fa-envelope-circle-check text-emerald"></i>
                            <span>Gửi mật khẩu qua dịch vụ Mail SMTP Real an toàn 24/7</span>
                        </div>
                    </div>
                </div>
            </div>

            <%-- Cột Phải: Form Quên Mật Khẩu Glassmorphism --%>
            <div class="col-lg-6 p-4 p-md-5 d-flex flex-column justify-content-center">
                <div class="mb-4">
                    <span class="badge bg-cyan bg-opacity-20 text-cyan rounded-pill px-3 py-2 mb-2 fs-7">
                        <i class="fa-solid fa-key me-1"></i>Hỗ Trợ Khôi Phục Tài Khoản
                    </span>
                    <h3 class="fw-bold text-white mb-2">Quên Mật Khẩu?</h3>
                    <p class="text-muted small">Vui lòng nhập Email liên kết với tài khoản của bạn</p>
                </div>

                <%-- Component Thông Báo Lỗi / Thành Công --%>
                <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

                <form action="${pageContext.request.contextPath}/MainController" method="POST">
                    <input type="hidden" name="action" value="forgot-password">

                    <div class="mb-4">
                        <label class="form-label text-muted fw-semibold">Địa Chỉ Email Đăng Ký (*)</label>
                        <div class="input-group">
                            <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass ${not empty errors.email ? 'is-invalid' : ''}" style="border-right: none;">
                                <i class="fa-solid fa-envelope text-cyan"></i>
                            </span>
                            <input type="email" name="email" class="form-control form-control-glass border-start-0 ps-0 ${not empty errors.email ? 'is-invalid' : ''}" value="${email}" required autofocus placeholder="nhapemail@gmail.com">
                        </div>
                        <c:if test="${not empty errors.email}">
                            <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.email}</span></div>
                        </c:if>
                        <div class="form-text text-muted small mt-2">
                            <i class="fa-solid fa-circle-info me-1 text-cyan"></i>Mật khẩu tạm thời sẽ được gửi trực tiếp đến hòm thư này.
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary-gradient w-100 py-3 fs-6 mb-3 rounded-pill">
                        <i class="fa-solid fa-paper-plane me-2"></i>Gửi Email Khôi Phục
                    </button>
                </form>

                <div class="text-center mt-3">
                    <span class="text-muted small">Nhớ lại mật khẩu?</span>
                    <a href="${pageContext.request.contextPath}/MainController?action=login-page" class="text-cyan text-decoration-none fw-semibold ms-1">Quay lại Đăng nhập</a>
                </div>
            </div>

        </div>
    </div>
</div>

<%-- Dynamic Footer Component --%>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
