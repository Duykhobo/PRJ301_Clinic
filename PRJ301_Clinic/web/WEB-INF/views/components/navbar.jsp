<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-expand-xl sticky-top navbar-glass py-3 mb-4">
    <div class="container-fluid container-xl">
        <%-- Medical Logo & Brand Name --%>
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold text-white fs-4 text-nowrap flex-shrink-0 me-3" 
           href="${pageContext.request.contextPath}/MainController?action=home">
            <div class="brand-icon-box d-flex align-items-center justify-content-center rounded-3 text-white shadow-sm flex-shrink-0" style="width: 42px; height: 42px; background: linear-gradient(135deg, #0ea5e9 0%, #10b981 100%);">
                <i class="fa-solid fa-heart-pulse fs-5"></i>
            </div>
            <span class="fw-bold text-white tracking-wide text-nowrap">${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : 'PRJ301 Clinic & Spa'}</span>
        </a>

        <%-- Mobile Toggle Button --%>
        <button class="navbar-toggler border-0 text-white shadow-none" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar">
            <i class="fa-solid fa-bars fs-3 text-cyan"></i>
        </button>

        <%-- Navbar Links & Right User Auth Actions --%>
        <div class="collapse navbar-collapse" id="mainNavbar">
            <ul class="navbar-nav mx-auto mb-2 mb-xl-0 gap-1 gap-xxl-2">
                <li class="nav-item">
                    <a class="nav-link text-white-50 text-white-hover px-3 py-2 rounded-3 transition-all text-nowrap" 
                       href="${pageContext.request.contextPath}/MainController?action=home">
                        <i class="fa-solid fa-house me-1 text-cyan"></i>Trang Chủ
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50 text-white-hover px-3 py-2 rounded-3 transition-all text-nowrap" 
                       href="${pageContext.request.contextPath}/MainController?action=home#services">
                        <i class="fa-solid fa-teeth me-1 text-cyan"></i>Dịch Vụ & Spa
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50 text-white-hover px-3 py-2 rounded-3 transition-all text-nowrap" 
                       href="${pageContext.request.contextPath}/MainController?action=home#doctors">
                        <i class="fa-solid fa-user-doctor me-1 text-cyan"></i>Đội Ngũ Bác Sĩ
                    </a>
                </li>
                <c:if test="${not empty sessionScope.LOGIN_USER}">
                    <li class="nav-item">
                        <a class="nav-link text-white-50 text-white-hover px-3 py-2 rounded-3 transition-all text-nowrap" 
                           href="${pageContext.request.contextPath}/MainController?action=history">
                            <i class="fa-solid fa-clock-rotate-left me-1 text-cyan"></i>Lịch Sử Khám
                        </a>
                    </li>
                </c:if>
            </ul>

            <%-- Right User Auth Actions & Emergency Hotline --%>
            <div class="d-flex align-items-center gap-3 mt-3 mt-xl-0 flex-shrink-0">
                <div class="d-none d-xxl-flex align-items-center gap-2 text-white-50 small me-2 border-end pe-3 border-secondary border-opacity-25 hotline-box text-nowrap flex-shrink-0">
                    <i class="fa-solid fa-headset text-emerald fs-5 flex-shrink-0"></i>
                    <div class="text-nowrap">
                        <div class="fs-7 text-muted text-nowrap">Hotline Y Tế 24/7</div>
                        <strong class="text-cyan text-nowrap">${not empty clinicSettings['CLINIC_HOTLINE'] ? clinicSettings['CLINIC_HOTLINE'] : '0901 234 567'}</strong>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty sessionScope.LOGIN_USER}">
                        <a href="${pageContext.request.contextPath}/MainController?action=login-page" 
                           class="btn btn-outline-glass px-4 rounded-pill text-nowrap flex-shrink-0">
                            <i class="fa-solid fa-right-to-bracket me-1"></i>Đăng Nhập
                        </a>
                        <a href="${pageContext.request.contextPath}/MainController?action=booking-page" 
                           class="btn btn-primary-gradient px-4 rounded-pill text-nowrap flex-shrink-0">
                            <i class="fa-solid fa-calendar-check me-1"></i>Đặt Lịch Khám
                        </a>
                    </c:when>
                    <c:otherwise>
                        <%-- User Dropdown --%>
                        <div class="dropdown text-nowrap flex-shrink-0">
                            <button class="btn btn-outline-glass dropdown-toggle d-flex align-items-center gap-2 rounded-pill px-3 py-2 text-nowrap flex-shrink-0" 
                                    type="button" data-bs-toggle="dropdown">
                                <div class="avatar-circle text-dark fw-bold rounded-circle d-flex align-items-center justify-content-center shadow-sm flex-shrink-0" style="width: 34px; height: 34px; background: linear-gradient(135deg, #0ea5e9, #10b981); color: #fff !important;">
                                    ${sessionScope.LOGIN_USER.fullname.substring(0,1).toUpperCase()}
                                </div>
                                <span class="fw-semibold text-white me-1 text-nowrap"><c:out value="${sessionScope.LOGIN_USER.fullname}"/></span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end glass-dropdown border-0 shadow-lg mt-2 p-2 rounded-4">
                                <li class="dropdown-header text-muted px-3 py-2 text-nowrap">
                                    <small>Vai trò hệ thống:</small><br>
                                    <strong class="text-cyan">${sessionScope.LOGIN_USER.role}</strong>
                                </li>
                                <li><hr class="dropdown-divider bg-secondary opacity-25"></li>
                                <li>
                                    <a class="dropdown-item rounded-3 py-2 text-white text-nowrap" 
                                       href="${pageContext.request.contextPath}/profile">
                                        <i class="fa-solid fa-id-card me-2 text-cyan"></i>Hồ Sơ Cá Nhân
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item rounded-3 py-2 text-white text-nowrap" 
                                       href="${pageContext.request.contextPath}/MainController?action=history">
                                        <i class="fa-solid fa-clock-rotate-left me-2 text-cyan"></i>Lịch Sử Đặt Khám
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item rounded-3 py-2 text-danger text-nowrap" 
                                       href="${pageContext.request.contextPath}/MainController?action=logout">
                                        <i class="fa-solid fa-right-from-bracket me-2"></i>Đăng Xuất
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>


