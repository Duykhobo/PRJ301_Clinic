<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đăng Ký Tài Khoản | PRJ301 Clinic</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex flex-column min-vh-100">

<%-- Dynamic Navbar Component --%>
<jsp:include page="/WEB-INF/views/components/navbar.jsp" />

<div class="container my-auto py-4">
    <div class="glass-card animate-fade-in mx-auto overflow-hidden p-0" style="max-width: 980px; width: 100%;">
        <div class="row g-0">
            
            <%-- Cột Trái: Banner Splash Thương Hiệu Y Tế --%>
            <div class="col-lg-5 d-none d-lg-block position-relative">
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
                        <h3 class="fw-bold display-6 mb-3">Đăng Ký Thành Viên Mới</h3>
                        <p class="text-muted small">Tạo tài khoản bệnh nhân chỉ trong 30 giây để trải nghiệm dịch vụ nha khoa & spa y khoa cao cấp.</p>
                    </div>

                    <div class="d-flex flex-column gap-2 small">
                        <div class="d-flex align-items-center gap-2 text-cyan">
                            <i class="fa-solid fa-sparkles text-emerald"></i>
                            <span>Đặt lịch khám online 24/7 không lo trùng ca</span>
                        </div>
                        <div class="d-flex align-items-center gap-2 text-cyan">
                            <i class="fa-solid fa-qrcode text-emerald"></i>
                            <span>Thanh toán mã VietQR SePay tự động siêu tốc</span>
                        </div>
                    </div>
                </div>
            </div>

            <%-- Cột Phải: Form Đăng Ký Glassmorphism --%>
            <div class="col-lg-7 p-4 p-md-5 d-flex flex-column justify-content-center">
                <h3 class="fw-bold mb-2 text-white">Đăng Ký Tài Khoản Bệnh Nhân</h3>
                <p class="text-muted small mb-4">Điền đầy đủ thông tin bên dưới để khởi tạo hồ sơ y tế</p>

                <%-- Component Thông Báo Lỗi --%>
                <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

                <form action="${pageContext.request.contextPath}/MainController" method="POST">
                    <input type="hidden" name="action" value="register">
                    <input type="hidden" name="csrfToken" value="${csrfToken}">

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label text-muted fw-semibold">Tên Đăng Nhập (*)</label>
                            <div class="input-group">
                                <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass" style="border-right: none;">
                                    <i class="fa-solid fa-user text-cyan"></i>
                                </span>
                                <input type="text" name="username" class="form-control form-control-glass border-start-0 ps-0" value="${username}" required placeholder="vd: patient123">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted fw-semibold">Họ và Tên (*)</label>
                            <div class="input-group">
                                <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass" style="border-right: none;">
                                    <i class="fa-solid fa-id-card text-cyan"></i>
                                </span>
                                <input type="text" name="fullname" class="form-control form-control-glass border-start-0 ps-0" value="${fullname}" required placeholder="vd: Nguyễn Văn A">
                            </div>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label text-muted fw-semibold">Số Điện Thoại (*)</label>
                            <div class="input-group">
                                <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass" style="border-right: none;">
                                    <i class="fa-solid fa-phone text-cyan"></i>
                                </span>
                                <input type="tel" name="phone" class="form-control form-control-glass border-start-0 ps-0" value="${phone}" required placeholder="vd: 0901234567">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted fw-semibold">Email (Tùy chọn)</label>
                            <div class="input-group">
                                <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass" style="border-right: none;">
                                    <i class="fa-solid fa-envelope text-cyan"></i>
                                </span>
                                <input type="email" name="email" class="form-control form-control-glass border-start-0 ps-0" value="${email}" placeholder="vd: email@gmail.com">
                            </div>
                        </div>
                    </div>

                    <div class="row g-3 mb-4">
                        <div class="col-md-6">
                            <label class="form-label text-muted fw-semibold">Mật Khẩu (*)</label>
                            <div class="input-password-wrapper">
                                <div class="input-group">
                                    <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass" style="border-right: none;">
                                        <i class="fa-solid fa-lock text-cyan"></i>
                                    </span>
                                    <input type="password" id="regPassword" name="password" class="form-control form-control-glass border-start-0 ps-0 pe-5" required placeholder="Nhập mật khẩu">
                                </div>
                                <button type="button" class="btn-password-toggle me-2" onclick="togglePassword('regPassword', this)" title="Hiện mật khẩu">
                                    <i class="fa-solid fa-eye"></i>
                                </button>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted fw-semibold">Xác Nhận Mật Khẩu (*)</label>
                            <div class="input-password-wrapper">
                                <div class="input-group">
                                    <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass" style="border-right: none;">
                                        <i class="fa-solid fa-lock text-cyan"></i>
                                    </span>
                                    <input type="password" id="regConfirmPassword" name="confirmPassword" class="form-control form-control-glass border-start-0 ps-0 pe-5" required placeholder="Nhập lại mật khẩu">
                                </div>
                                <button type="button" class="btn-password-toggle me-2" onclick="togglePassword('regConfirmPassword', this)" title="Hiện mật khẩu">
                                    <i class="fa-solid fa-eye"></i>
                                </button>
                            </div>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary-gradient w-100 py-3 fs-6 mb-3 rounded-pill">
                        <i class="fa-solid fa-user-plus me-2"></i>Đăng Ký Tài Khoản Ngay
                    </button>
                </form>

                <div class="text-center mt-3">
                    <span class="text-muted small">Đã có tài khoản?</span>
                    <a href="${pageContext.request.contextPath}/MainController?action=login-page" class="text-cyan text-decoration-none fw-semibold ms-1">Đăng nhập ngay</a>
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

