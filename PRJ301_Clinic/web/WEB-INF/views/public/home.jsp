<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Trang Chủ | <c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Phòng Khám & Spa')}"/></title>
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
<section class="py-5 text-center my-auto position-relative overflow-hidden">
    <div class="container">
        <div class="glass-card animate-fade-in mx-auto p-4 p-md-5 position-relative" style="max-width: 960px;">
            <div class="d-inline-flex align-items-center gap-2 px-3 py-2 rounded-pill mb-3 fs-6 slot-btn-available shadow-sm">
                <i class="fa-solid fa-notes-medical text-emerald"></i>
                <span class="fw-semibold" data-i18n="hero_badge">Hệ Thống Đặt Lịch Khám & Chăm Sóc Sức Khỏe Thông Minh 2026</span>
            </div>
            
            <h1 class="display-4 fw-extrabold mb-4 text-white" data-i18n="hero_title">Chăm Sóc Sức Khỏe & Thẩm Mỹ Nụ Cười Cùng Bác Sĩ Chuyên Khoa</h1>
            
            <p class="lead text-muted mb-4 mx-auto" style="max-width: 780px;" data-i18n="hero_desc">
                Trải nghiệm dịch vụ Nha khoa & Spa y khoa chuẩn quốc tế. Đặt lịch khám trực tuyến 24/7 chống trùng ca 60 phút, mã hóa bảo mật hồ sơ y tế và thanh toán VietQR SePay tự động.
            </p>
            
            <div class="d-flex flex-wrap justify-content-center gap-3 mb-5">
                <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-primary-gradient btn-lg px-4 py-3 fs-6 rounded-pill" data-i18n="btn_book_now">
                    <i class="fa-solid fa-calendar-check me-2"></i><span>Đặt Lịch Khám Ngay</span>
                </a>
                <c:choose>
                    <c:when test="${empty sessionScope.LOGIN_USER}">
                        <a href="${pageContext.request.contextPath}/MainController?action=login-page" class="btn btn-outline-glass btn-lg px-4 py-3 fs-6 rounded-pill" data-i18n="btn_login_account">
                            <i class="fa-solid fa-user-shield me-2 text-cyan"></i><span>Đăng Nhập Tài Khoản</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/MainController?action=history" class="btn btn-outline-glass btn-lg px-4 py-3 fs-6 rounded-pill" data-i18n="btn_view_history">
                            <i class="fa-solid fa-clock-rotate-left me-2 text-cyan"></i><span>Xem Lịch Sử Khám Bệnh</span>
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- CLINICAL KEY METRICS & TRUST STATS --%>
            <div class="row g-3 pt-4 border-top border-secondary border-opacity-25 text-start">
                <div class="col-4 col-md-4 text-center">
                    <div class="display-6 fw-bold text-cyan">10,000+</div>
                    <div class="fs-7 text-muted" data-i18n="kpi_patients">Bệnh Nhân Tin Chọn</div>
                </div>
                <div class="col-4 col-md-4 text-center border-start border-end border-secondary border-opacity-25">
                    <div class="display-6 fw-bold text-emerald">50+</div>
                    <div class="fs-7 text-muted" data-i18n="kpi_specialists">Bác Sĩ Chứng Chỉ</div>
                </div>
                <div class="col-4 col-md-4 text-center">
                    <div class="display-6 fw-bold text-warning">99.8%</div>
                    <div class="fs-7 text-muted" data-i18n="kpi_satisfaction">Tỷ Lệ Hài Lòng</div>
                </div>
            </div>

        </div>
    </div>
</section>

<%-- TOP FEATURED SERVICES SECTION --%>
<section id="services" class="py-5">
    <div class="container">
        <div class="text-center mb-5">
            <span class="badge bg-cyan bg-opacity-20 text-cyan rounded-pill px-3 py-2 mb-2" data-i18n="section_specialty_badge">Chuyên Khoa Hàng Đầu</span>
            <h2 class="fw-bold mb-2 text-white" data-i18n="section_services_title"><i class="fa-solid fa-teeth text-cyan me-2"></i><span>Dịch Vụ Khám & Spa Nổi Bật</span></h2>
            <p class="text-muted" data-i18n="section_services_desc">Các giải pháp chăm sóc răng miệng & liệu trình phục hồi da chuẩn y khoa</p>
        </div>

        <div class="row g-4 justify-content-center">
            <c:choose>
                <c:when test="${not empty services}">
                    <c:forEach items="${services}" var="s" varStatus="loop" end="2">
                        <div class="col-md-6 col-lg-4">
                            <div class="glass-card hover-lift h-100 p-4 d-flex flex-column justify-content-between">
                                <div>
                                    <div class="overflow-hidden rounded-4 mb-3 position-relative" style="height: 210px;">
                                        <span class="position-absolute top-0 end-0 m-3 badge bg-dark bg-opacity-75 text-emerald border border-success border-opacity-30 rounded-pill px-3 py-2 fs-7 backdrop-blur">
                                            <i class="fa-regular fa-clock me-1"></i>60 Phút
                                        </span>
                                        <c:choose>
                                            <c:when test="${loop.index == 0}">
                                                <img src="${pageContext.request.contextPath}/assets/images/dental_service.jpg" alt="${s.serviceName}" class="w-100 h-100 object-fit-cover">
                                            </c:when>
                                            <c:when test="${loop.index == 1}">
                                                <img src="${pageContext.request.contextPath}/assets/images/spa_service.jpg" alt="${s.serviceName}" class="w-100 h-100 object-fit-cover">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/assets/images/acne_service.jpg" alt="${s.serviceName}" class="w-100 h-100 object-fit-cover">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <h5 class="fw-bold mb-2 text-white">${s.serviceName}</h5>
                                    <p class="text-muted small mb-3">${s.description}</p>
                                </div>
                                <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top border-secondary border-opacity-25">
                                    <div>
                                        <div class="fs-7 text-muted">Giá niêm yết</div>
                                        <span class="fw-bold text-warning fs-5"><fmt:formatNumber value="${s.price}" pattern="#,##0" maxFractionDigits="0"/> VNĐ</span>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-sm btn-primary-gradient rounded-pill px-3 py-2">
                                        <i class="fa-solid fa-calendar-plus me-1"></i>Đặt Ca
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <%-- Static Fallback Service Cards --%>
                    <div class="col-md-6 col-lg-4">
                        <div class="glass-card hover-lift h-100 p-4 d-flex flex-column justify-content-between">
                            <div>
                                <div class="overflow-hidden rounded-4 mb-3 position-relative" style="height: 210px;">
                                    <span class="position-absolute top-0 end-0 m-3 badge bg-dark bg-opacity-75 text-emerald border border-success border-opacity-30 rounded-pill px-3 py-2 fs-7">
                                        <i class="fa-regular fa-clock me-1"></i>60 Phút
                                    </span>
                                    <img src="${pageContext.request.contextPath}/assets/images/dental_service.jpg" alt="Tẩy Trắng Răng Laser" class="w-100 h-100 object-fit-cover">
                                </div>
                                <h5 class="fw-bold mb-2 text-white">Tẩy Trắng Răng Laser Whitening</h5>
                                <p class="text-muted small">Công nghệ Laser Whitening không ê buốt, bật tông sáng bóng tự nhiên chỉ sau 45 phút.</p>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top border-secondary border-opacity-25">
                                <div>
                                    <div class="fs-7 text-muted">Giá trọn gói</div>
                                    <span class="fw-bold text-warning fs-5">1.500.000 VNĐ</span>
                                </div>
                                <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-sm btn-primary-gradient rounded-pill px-3 py-2">
                                    <i class="fa-solid fa-calendar-plus me-1"></i>Đặt Ca
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-4">
                        <div class="glass-card hover-lift h-100 p-4 d-flex flex-column justify-content-between">
                            <div>
                                <div class="overflow-hidden rounded-4 mb-3 position-relative" style="height: 210px;">
                                    <span class="position-absolute top-0 end-0 m-3 badge bg-dark bg-opacity-75 text-emerald border border-success border-opacity-30 rounded-pill px-3 py-2 fs-7">
                                        <i class="fa-regular fa-clock me-1"></i>60 Phút
                                    </span>
                                    <img src="${pageContext.request.contextPath}/assets/images/spa_service.jpg" alt="Chăm Sóc Da Spa" class="w-100 h-100 object-fit-cover">
                                </div>
                                <h5 class="fw-bold mb-2 text-white">Chăm Sóc Da Deep Cleansing Spa</h5>
                                <p class="text-muted small">Liệu trình làm sạch sâu, thải độc và trẻ hóa làn da căng mịn chuẩn y khoa.</p>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top border-secondary border-opacity-25">
                                <div>
                                    <div class="fs-7 text-muted">Giá trọn gói</div>
                                    <span class="fw-bold text-warning fs-5">850.000 VNĐ</span>
                                </div>
                                <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-sm btn-primary-gradient rounded-pill px-3 py-2">
                                    <i class="fa-solid fa-calendar-plus me-1"></i>Đặt Ca
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-4">
                        <div class="glass-card hover-lift h-100 p-4 d-flex flex-column justify-content-between">
                            <div>
                                <div class="overflow-hidden rounded-4 mb-3 position-relative" style="height: 210px;">
                                    <span class="position-absolute top-0 end-0 m-3 badge bg-dark bg-opacity-75 text-emerald border border-success border-opacity-30 rounded-pill px-3 py-2 fs-7">
                                        <i class="fa-regular fa-clock me-1"></i>60 Phút
                                    </span>
                                    <img src="${pageContext.request.contextPath}/assets/images/acne_service.jpg" alt="Phục Hồi Da Mụn" class="w-100 h-100 object-fit-cover">
                                </div>
                                <h5 class="fw-bold mb-2 text-white">Chăm Sóc Da Mụn & Phục Hồi Y Khoa</h5>
                                <p class="text-muted small">Điều trị mụn chuyên y khoa, chiếu ánh sáng sinh học làm lành da nhanh chóng.</p>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top border-secondary border-opacity-25">
                                <div>
                                    <div class="fs-7 text-muted">Giá trọn gói</div>
                                    <span class="fw-bold text-warning fs-5">650.000 VNĐ</span>
                                </div>
                                <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-sm btn-primary-gradient rounded-pill px-3 py-2">
                                    <i class="fa-solid fa-calendar-plus me-1"></i>Đặt Ca
                                </a>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<%-- DOCTOR TEAM SECTION --%>
<section id="doctors" class="py-5">
    <div class="container">
        <div class="text-center mb-5">
            <span class="badge bg-emerald bg-opacity-20 text-emerald rounded-pill px-3 py-2 mb-2" data-i18n="section_doctors_badge">Đội Ngũ Chuyên Gia</span>
            <h2 class="fw-bold mb-2 text-white" data-i18n="section_doctors_title"><i class="fa-solid fa-user-doctor text-emerald me-2"></i><span>Bác Sĩ Chuyên Khoa Tận Tâm</span></h2>
            <p class="text-muted" data-i18n="section_doctors_desc">Đội ngũ y bác sĩ chứng chỉ hành nghề, giàu kinh nghiệm phụ trách từng ca bệnh</p>
        </div>

        <div class="row g-4 justify-content-center">
            <c:choose>
                <c:when test="${not empty doctors}">
                    <c:forEach items="${doctors}" var="d" varStatus="loop">
                        <div class="col-md-5 col-lg-4">
                            <div class="glass-card hover-lift text-center p-4">
                                <div class="mx-auto rounded-circle overflow-hidden mb-3 border border-2 border-info p-1 shadow" style="width: 130px; height: 130px;">
                                    <img src="${loop.index % 2 == 0 ? pageContext.request.contextPath.concat('/assets/images/doctor_male.jpg') : pageContext.request.contextPath.concat('/assets/images/doctor_female.jpg')}" 
                                         alt="${d.doctorName}" class="w-100 h-100 object-fit-cover rounded-circle">
                                </div>
                                <h5 class="fw-bold mb-1 text-white">${d.doctorName}</h5>
                                <p class="text-cyan small fw-semibold mb-2">${d.specialty}</p>
                                <div class="d-flex justify-content-center align-items-center gap-1 text-warning small mb-3">
                                    <i class="fa-solid fa-star"></i>
                                    <i class="fa-solid fa-star"></i>
                                    <i class="fa-solid fa-star"></i>
                                    <i class="fa-solid fa-star"></i>
                                    <i class="fa-solid fa-star"></i>
                                    <span class="text-white ms-1 fw-bold">5.0</span>
                                </div>
                                <p class="text-muted small mb-3">${d.bio}</p>
                                <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-sm btn-outline-glass w-100 rounded-pill" data-i18n="btn_book_doctor">
                                    <i class="fa-regular fa-calendar-check me-1 text-cyan"></i><span>Đặt Lịch Với Bác Sĩ</span>
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <%-- Static Fallback Doctor Card --%>
                    <div class="col-md-5 col-lg-4">
                        <div class="glass-card hover-lift text-center p-4">
                            <div class="mx-auto rounded-circle overflow-hidden mb-3 border border-2 border-info p-1 shadow" style="width: 130px; height: 130px;">
                                <img src="${pageContext.request.contextPath}/assets/images/doctor_male.jpg" 
                                     alt="BS. Bùi Văn Minh" class="w-100 h-100 object-fit-cover rounded-circle">
                            </div>
                            <h5 class="fw-bold mb-1 text-white">BS. Bùi Văn Minh</h5>
                            <p class="text-cyan small fw-semibold mb-2">Nha Khoa Thẩm Mỹ & Phục Hình</p>
                            <div class="d-flex justify-content-center align-items-center gap-1 text-warning small mb-3">
                                <i class="fa-solid fa-star"></i>
                                <i class="fa-solid fa-star"></i>
                                <i class="fa-solid fa-star"></i>
                                <i class="fa-solid fa-star"></i>
                                <i class="fa-solid fa-star"></i>
                                <span class="text-white ms-1 fw-bold">5.0</span>
                            </div>
                            <p class="text-muted small mb-3">10 năm kinh nghiệm thẩm mỹ nụ cười và phục hình răng sứ cao cấp.</p>
                            <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-sm btn-outline-glass w-100 rounded-pill">
                                <i class="fa-regular fa-calendar-check me-1 text-cyan"></i>Đặt Lịch Với Bác Sĩ
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<%-- WHY CHOOSE US / MEDICAL TRUST FEATURES --%>
<section class="py-5">
    <div class="container">
        <div class="glass-card p-4 p-md-5 text-center">
            <h3 class="fw-bold text-white mb-4"><i class="fa-solid fa-shield-halved text-cyan me-2"></i>Tại Sao Bệnh Nhân Tin Chọn PRJ301 Clinic?</h3>
            <div class="row g-4">
                <div class="col-md-3">
                    <div class="p-3">
                        <div class="d-inline-flex align-items-center justify-content-center rounded-circle mb-3 shadow-sm" style="width: 58px; height: 58px; background: rgba(14, 165, 233, 0.15); border: 1px solid rgba(14, 165, 233, 0.4); color: #38bdf8;">
                            <i class="fa-solid fa-clock-rotate-left fs-4"></i>
                        </div>
                        <h6 class="fw-bold text-white mb-2">Chống Trùng Slot 100%</h6>
                        <p class="text-muted fs-7 mb-0">Thuật toán ma trận 60 phút đảm bảo không bao giờ xảy ra tình trạng trùng ca khám.</p>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="p-3">
                        <div class="d-inline-flex align-items-center justify-content-center rounded-circle mb-3 shadow-sm" style="width: 58px; height: 58px; background: rgba(16, 185, 129, 0.15); border: 1px solid rgba(16, 185, 129, 0.4); color: #34d399;">
                            <i class="fa-solid fa-qrcode fs-4"></i>
                        </div>
                        <h6 class="fw-bold text-white mb-2">Thanh Toán VietQR SePay</h6>
                        <p class="text-muted fs-7 mb-0">Khởi tạo mã VietQR tự động, xác nhận thanh toán tức thì mà không cần chờ thủ công.</p>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="p-3">
                        <div class="d-inline-flex align-items-center justify-content-center rounded-circle mb-3 shadow-sm" style="width: 58px; height: 58px; background: rgba(245, 158, 11, 0.15); border: 1px solid rgba(245, 158, 11, 0.4); color: #fbbf24;">
                            <i class="fa-solid fa-shield-halved fs-4"></i>
                        </div>
                        <h6 class="fw-bold text-white mb-2">Mã Hóa BCrypt 100%</h6>
                        <p class="text-muted fs-7 mb-0">Bảo mật thông tin tài khoản và nhật ký khám y tế bệnh nhân chuẩn mã hóa quốc tế.</p>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="p-3">
                        <div class="d-inline-flex align-items-center justify-content-center rounded-circle mb-3 shadow-sm" style="width: 58px; height: 58px; background: rgba(239, 68, 68, 0.15); border: 1px solid rgba(239, 68, 68, 0.4); color: #f87171;">
                            <i class="fa-solid fa-microscope fs-4"></i>
                        </div>
                        <h6 class="fw-bold text-white mb-2">Thiết Bị Y Khoa Tân Tiến</h6>
                        <p class="text-muted fs-7 mb-0">Hệ thống máy móc nha khoa & spa y tế nhập khẩu trực tiếp từ Châu Âu.</p>
                    </div>
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

