<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-expand-xl sticky-top navbar-glass py-2 mb-4" style="background: rgba(11, 19, 43, 0.88); backdrop-filter: blur(16px); border-bottom: 1px solid rgba(56, 189, 248, 0.2);">
    <div class="container-fluid px-3 px-xxl-4">
        <%-- Medical Logo & Brand Name (100% Full Visibility & Zero Truncation) --%>
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold text-white me-2 me-xl-3 flex-shrink-0"
           href="${pageContext.request.contextPath}/MainController?action=home">
            <div class="brand-icon-box d-flex align-items-center justify-content-center rounded-3 text-white shadow-sm flex-shrink-0"
                 style="width: 36px; height: 36px; background: linear-gradient(135deg, #0ea5e9 0%, #10b981 100%);">
                <i class="fa-solid fa-heart-pulse fs-5"></i>
            </div>
            <span class="fw-bold text-white tracking-wide text-nowrap" style="font-size: 1.05rem;">
                <c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Phòng Khám & Spa')}"/>
            </span>
        </a>

        <%-- Mobile Toggle Button --%>
        <button class="navbar-toggler border-0 text-white shadow-none ms-auto me-2 p-1" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar">
            <i class="fa-solid fa-bars fs-4 text-cyan"></i>
        </button>

        <%-- Navbar Links & Right Actions --%>
        <div class="collapse navbar-collapse" id="mainNavbar">
            <ul class="navbar-nav mx-auto mb-2 mb-xl-0 gap-1 gap-xxl-2 pt-2 pt-xl-0">
                <li class="nav-item">
                    <a class="nav-link text-white-50 text-white-hover px-2 px-xxl-3 py-1.5 rounded-3 transition-all text-nowrap"
                       href="${pageContext.request.contextPath}/MainController?action=home"
                       data-i18n="nav_home">
                        <i class="fa-solid fa-house me-1 text-cyan"></i><span>Trang Chủ</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50 text-white-hover px-2 px-xxl-3 py-1.5 rounded-3 transition-all text-nowrap"
                       href="${pageContext.request.contextPath}/MainController?action=home#services"
                       data-i18n="nav_services">
                        <i class="fa-solid fa-teeth me-1 text-cyan"></i><span>Dịch Vụ &amp; Spa</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50 text-white-hover px-2 px-xxl-3 py-1.5 rounded-3 transition-all text-nowrap"
                       href="${pageContext.request.contextPath}/MainController?action=home#doctors"
                       data-i18n="nav_doctors">
                        <i class="fa-solid fa-user-doctor me-1 text-cyan"></i><span>Đội Ngũ Bác Sĩ</span>
                    </a>
                </li>
                <c:if test="${sessionScope.LOGIN_USER.role == 'PATIENT'}">
                    <li class="nav-item">
                        <a class="nav-link text-white-50 text-white-hover px-2 px-xxl-3 py-1.5 rounded-3 transition-all text-nowrap"
                           href="${pageContext.request.contextPath}/MainController?action=history"
                           data-i18n="nav_history">
                            <i class="fa-solid fa-clock-rotate-left me-1 text-cyan"></i><span>Lịch Sử Khám</span>
                        </a>
                    </li>
                </c:if>
            </ul>

            <%-- Right User Auth Actions --%>
            <div class="d-flex align-items-center gap-2 mt-2 mt-xl-0 pt-2 pt-xl-0 border-top border-xl-0 border-secondary border-opacity-25 flex-nowrap">
                <%-- Hotline 24/7 (Hidden on screens < 1400px) --%>
                <div class="d-none d-xxl-flex align-items-center gap-2 text-white-50 small me-1 border-end pe-3 border-secondary border-opacity-25 flex-shrink-0">
                    <i class="fa-solid fa-headset text-emerald fs-5"></i>
                    <div>
                        <div class="fs-8 text-muted">Hotline Y Tế 24/7</div>
                        <strong class="text-cyan">${not empty clinicSettings['CLINIC_HOTLINE'] ? clinicSettings['CLINIC_HOTLINE'] : '0901 234 567'}</strong>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty sessionScope.LOGIN_USER}">
                        <a href="${pageContext.request.contextPath}/MainController?action=login-page"
                           class="btn btn-outline-glass px-2 px-sm-3 rounded-pill text-center py-1.5 fs-7 text-nowrap flex-shrink-0"
                           data-i18n="btn_login">
                            <i class="fa-solid fa-right-to-bracket me-1"></i><span>Đăng Nhập</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/MainController?action=booking-page"
                           class="btn btn-primary-gradient px-2 px-sm-3 rounded-pill text-center py-1.5 fs-7 text-nowrap flex-shrink-0"
                           data-i18n="btn_booking">
                            <i class="fa-solid fa-calendar-check me-1"></i><span>Đặt Lịch Khám</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <%-- Notification Bell Component --%>
                        <jsp:include page="/WEB-INF/views/components/notification-bell.jsp" />

                        <%-- User Dropdown --%>
                        <div class="dropdown flex-shrink-0">
                            <button class="btn btn-outline-glass dropdown-toggle d-flex align-items-center gap-2 rounded-pill px-3 py-1.5"
                                    type="button" data-bs-toggle="dropdown">
                                <div class="avatar-circle text-dark fw-bold rounded-circle d-flex align-items-center justify-content-center shadow-sm flex-shrink-0"
                                     style="width: 28px; height: 28px; background: linear-gradient(135deg, #0ea5e9, #10b981); color: #fff !important; font-size: 0.8rem;">
                                    ${sessionScope.LOGIN_USER.fullname.substring(0,1).toUpperCase()}
                                </div>
                                <span class="fw-semibold text-white text-truncate d-none d-sm-inline" style="max-width: 110px; font-size: 0.85rem;">
                                    <c:out value="${sessionScope.LOGIN_USER.fullname}"/>
                                </span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end glass-dropdown border-0 shadow-lg mt-2 p-2 rounded-4">
                                <li class="dropdown-header text-muted px-3 py-2">
                                    <div class="d-flex justify-content-between align-items-center gap-2">
                                        <small data-i18n="role_label">Vai trò:</small>
                                        <strong class="text-cyan">${sessionScope.LOGIN_USER.role}</strong>
                                    </div>
                                    <c:if test="${sessionScope.LOGIN_USER.role == 'PATIENT' && not empty loyaltyProfile}">
                                        <div class="mt-1">
                                            <span class="badge ${loyaltyProfile.tierBadgeClass} rounded-pill px-2 py-1" style="font-size: 0.7rem;">
                                                <i class="fa-solid fa-crown me-1 text-warning"></i><c:out value="${loyaltyProfile.tierName}"/>
                                            </span>
                                        </div>
                                    </c:if>
                                </li>
                                <li><hr class="dropdown-divider bg-secondary opacity-25"></li>
                                
                                <%-- Role-based Workspace Link --%>
                                <c:if test="${sessionScope.LOGIN_USER.role == 'ADMIN'}">
                                    <li>
                                        <a class="dropdown-item rounded-3 py-2 text-warning fw-bold"
                                           href="${pageContext.request.contextPath}/admin/dashboard">
                                            <i class="fa-solid fa-gauge me-2 text-warning"></i><span>Bảng Quản Trị</span>
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.LOGIN_USER.role == 'DOCTOR'}">
                                    <li>
                                        <a class="dropdown-item rounded-3 py-2 text-cyan fw-bold"
                                           href="${pageContext.request.contextPath}/doctor/dashboard">
                                            <i class="fa-solid fa-stethoscope me-2 text-cyan"></i><span>Bàn Khám Bác Sĩ</span>
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.LOGIN_USER.role == 'RECEPTIONIST'}">
                                    <li>
                                        <a class="dropdown-item rounded-3 py-2 text-emerald fw-bold"
                                           href="${pageContext.request.contextPath}/receptionist/dashboard">
                                            <i class="fa-solid fa-hospital-user me-2 text-emerald"></i><span>Sảnh Tiếp Đón</span>
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.LOGIN_USER.role == 'PATIENT'}">
                                    <li>
                                        <a class="dropdown-item rounded-3 py-2 text-white"
                                           href="${pageContext.request.contextPath}/MainController?action=history"
                                           data-i18n="nav_history">
                                            <i class="fa-solid fa-clock-rotate-left me-2 text-cyan"></i><span>Lịch Sử Đặt Khám</span>
                                        </a>
                                    </li>
                                </c:if>

                                <li>
                                    <a class="dropdown-item rounded-3 py-2 text-white"
                                       href="${pageContext.request.contextPath}/profile"
                                       data-i18n="nav_profile">
                                        <i class="fa-solid fa-id-card me-2 text-cyan"></i><span>Hồ Sơ Cá Nhân</span>
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item rounded-3 py-2 text-danger"
                                       href="${pageContext.request.contextPath}/logout"
                                       onclick="return confirmLogout(event);"
                                       data-i18n="nav_logout">
                                        <i class="fa-solid fa-right-from-bracket me-2"></i><span>Đăng Xuất</span>
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