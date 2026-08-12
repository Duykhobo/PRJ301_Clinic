<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Trang Chủ | Phòng Khám & Spa Nha Khoa PRJ301</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body>

<!-- NAVIGATION BAR -->
<nav class="navbar navbar-expand-lg navbar-dark bg-transparent border-bottom border-secondary border-opacity-25 py-3">
    <div class="container">
        <a class="navbar-brand fw-bold fs-4" href="${pageContext.request.contextPath}/MainController?action=home">
            <i class="fa-solid fa-Tooth me-2 text-info"></i>PRJ301 Clinic & Spa
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto align-items-center gap-2">
                <li class="nav-item">
                    <a class="nav-link active" href="${pageContext.request.contextPath}/MainController?action=home">Trang Chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/MainController?action=booking-page">Đặt Lịch Hẹn</a>
                </li>
                
                <c:choose>
                    <c:when test="${not empty sessionScope.LOGIN_USER}">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle btn btn-outline-glass px-3" href="#" role="button" data-bs-toggle="dropdown">
                                <i class="fa-solid fa-user-circle me-1"></i>${sessionScope.LOGIN_USER.fullname}
                            </a>
                            <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end shadow">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/MainController?action=history"><i class="fa-solid fa-clock-rotate-left me-2"></i>Lịch Sử Đặt Hẹn</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/MainController?action=logout"><i class="fa-solid fa-right-from-bracket me-2"></i>Đăng Xuất</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item ms-2">
                            <a class="btn btn-outline-glass px-3" href="${pageContext.request.contextPath}/MainController?action=login-page">Đăng Nhập</a>
                        </li>
                        <li class="nav-item">
                            <a class="btn btn-primary-gradient px-3" href="${pageContext.request.contextPath}/MainController?action=register-page">Đăng Ký</a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>

<!-- HERO SECTION -->
<div class="container my-5 py-5 text-center">
    <div class="glass-card animate-fade-in mx-auto p-5" style="max-width: 800px;">
        <span class="badge px-3 py-2 rounded-pill mb-3 fs-6" style="background: rgba(56, 189, 248, 0.15); color: #38bdf8; border: 1px solid rgba(56, 189, 248, 0.3);">
            <i class="fa-solid fa-sparkles me-1"></i>Hệ thống Đặt Lịch Thông Minh
        </span>
        <h1 class="display-4 fw-bold mb-4">Chăm Sóc Sức Khỏe & Thẩm Mỹ Nụ Cười Cùng Chuyên Gia</h1>
        <p class="lead text-muted mb-4">
            Trải nghiệm dịch vụ Nha khoa & Spa y khoa chuẩn quốc tế. Đặt lịch khám trực tuyến 24/7 chống trùng slot và thanh toán VietQR SePay tiện lợi.
        </p>
        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-primary-gradient btn-lg px-4 fs-6">
                <i class="fa-solid fa-calendar-check me-2"></i>Đặt Lịch Khám Ngay
            </a>
            <a href="${pageContext.request.contextPath}/MainController?action=login-page" class="btn btn-outline-glass btn-lg px-4 fs-6">
                <i class="fa-solid fa-right-to-bracket me-2"></i>Đăng Nhập Tài Khoản
            </a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
