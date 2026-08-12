<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Trang Chủ | Phòng Khám & Spa Nha Khoa PRJ301</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex flex-column min-vh-100">

<%-- Dynamic Navbar Component --%>
<jsp:include page="/WEB-INF/views/components/navbar.jsp" />

<%-- Alert Banner Component --%>
<div class="container mt-2">
    <jsp:include page="/WEB-INF/views/components/alerts.jsp" />
</div>

<%-- HERO BANNER SECTION --%>
<section class="py-5 text-center my-auto">
    <div class="container">
        <div class="glass-card animate-fade-in mx-auto p-5" style="max-width: 900px;">
            <span class="badge px-3 py-2 rounded-pill mb-3 fs-6 slot-btn-available">
                <i class="fa-solid fa-sparkles me-1"></i>Hệ Thống Đặt Lịch Y Khoa Thông Minh 2026
            </span>
            <h1 class="display-4 fw-bold mb-4">Chăm Sóc Sức Khỏe & Thẩm Mỹ Nụ Cười Cùng Chuyên Gia</h1>
            <p class="lead text-muted mb-4">
                Trải nghiệm dịch vụ Nha khoa & Spa y khoa chuẩn quốc tế. Đặt lịch khám trực tuyến 24/7 chống trùng ca giờ và thanh toán VietQR SePay tự động.
            </p>
            <div class="d-flex flex-wrap justify-content-center gap-3">
                <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-primary-gradient btn-lg px-4 fs-6">
                    <i class="fa-solid fa-calendar-check me-2"></i>Đặt Lịch Khám Ngay
                </a>
                <c:if test="${empty sessionScope.SESSION_USER}">
                    <a href="${pageContext.request.contextPath}/MainController?action=login-page" class="btn btn-outline-glass btn-lg px-4 fs-6">
                        <i class="fa-solid fa-right-to-bracket me-2"></i>Đăng Nhập Tài Khoản
                    </a>
                </c:if>
            </div>
        </div>
    </div>
</section>

<%-- FEATURED SERVICES SECTION --%>
<section id="services" class="py-5">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="fw-bold mb-2"><i class="fa-solid fa-teeth text-info me-2"></i>Dịch Vụ Nổi Bật</h2>
            <p class="text-muted">Các giải pháp chăm sóc răng miệng & thẩm mỹ da hàng đầu được tin tưởng</p>
        </div>

        <div class="row g-4">
            <%-- Service 1 --%>
            <div class="col-md-6 col-lg-4">
                <div class="glass-card hover-lift h-100 p-4 d-flex flex-column justify-content-between">
                    <div>
                        <div class="overflow-hidden rounded-4 mb-3" style="height: 200px;">
                            <img src="${pageContext.request.contextPath}/assets/images/dental_service.jpg" 
                                 alt="Tẩy Trắng Răng Laser" class="w-100 h-100 object-fit-cover">
                        </div>
                        <h5 class="fw-bold mb-2">Tẩy Trắng Răng Laser Whitening</h5>
                        <p class="text-muted small">Công nghệ Laser Whitening không ê buốt, bật tông sáng bóng tự nhiên chỉ sau 45 phút.</p>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top border-secondary opacity-75">
                        <span class="fw-bold text-warning fs-5">1.500.000 VNĐ</span>
                        <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-sm btn-outline-glass rounded-pill">Đặt Ca</a>
                    </div>
                </div>
            </div>

            <%-- Service 2 --%>
            <div class="col-md-6 col-lg-4">
                <div class="glass-card hover-lift h-100 p-4 d-flex flex-column justify-content-between">
                    <div>
                        <div class="overflow-hidden rounded-4 mb-3" style="height: 200px;">
                            <img src="${pageContext.request.contextPath}/assets/images/spa_service.jpg" 
                                 alt="Chăm Sóc Da Deep Cleansing" class="w-100 h-100 object-fit-cover">
                        </div>
                        <h5 class="fw-bold mb-2">Chăm Sóc Da Deep Cleansing Spa</h5>
                        <p class="text-muted small">Liệu trình làm sạch sâu, thải độc và trẻ hóa làn da căng mịn chuẩn y khoa.</p>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top border-secondary opacity-75">
                        <span class="fw-bold text-warning fs-5">850.000 VNĐ</span>
                        <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-sm btn-outline-glass rounded-pill">Đặt Ca</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<%-- DOCTOR TEAM SECTION --%>
<section id="doctors" class="py-5">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="fw-bold mb-2"><i class="fa-solid fa-user-doctor text-info me-2"></i>Đội Ngũ Bác Sĩ Chuyên Khoa</h2>
            <p class="text-muted">Đội ngũ y bác sĩ giàu kinh nghiệm, tận tâm vì sức khỏe người bệnh</p>
        </div>

        <div class="row g-4 justify-content-center">
            <%-- Doctor 1 --%>
            <div class="col-md-5 col-lg-4">
                <div class="glass-card hover-lift text-center p-4">
                    <div class="mx-auto rounded-circle overflow-hidden mb-3 border border-info p-1" style="width: 130px; height: 130px;">
                        <img src="${pageContext.request.contextPath}/assets/images/doctor_male.jpg" 
                             alt="BS. Bùi Văn Minh" class="w-100 h-100 object-fit-cover rounded-circle">
                    </div>
                    <h5 class="fw-bold mb-1">BS. Bùi Văn Minh</h5>
                    <p class="text-info small mb-2">Nha Khoa Thẩm Mỹ & Phục Hình</p>
                    <p class="text-muted small">10 năm kinh nghiệm thẩm mỹ nụ cười và phục hình răng sứ.</p>
                </div>
            </div>

            <%-- Doctor 2 --%>
            <div class="col-md-5 col-lg-4">
                <div class="glass-card hover-lift text-center p-4">
                    <div class="mx-auto rounded-circle overflow-hidden mb-3 border border-info p-1" style="width: 130px; height: 130px;">
                        <img src="${pageContext.request.contextPath}/assets/images/doctor_female.jpg" 
                             alt="BS. Trần Thị Lan" class="w-100 h-100 object-fit-cover rounded-circle">
                    </div>
                    <h5 class="fw-bold mb-1">BS. Trần Thị Lan</h5>
                    <p class="text-info small mb-2">Da Liễu & Thẩm Mỹ Skin Care Spa</p>
                    <p class="text-muted small">8 năm kinh nghiệm trẻ hóa da và trị liệu y khoa chuyên sâu.</p>
                </div>
            </div>
        </div>
    </div>
</section>

<%-- Dynamic Footer Component --%>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
