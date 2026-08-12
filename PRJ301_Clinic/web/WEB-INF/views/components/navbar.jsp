<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-expand-lg sticky-top navbar-glass py-3 mb-4">
    <div class="container">
        <%-- Logo & Brand Name --%>
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold text-white fs-4" 
           href="${pageContext.request.contextPath}/MainController?action=home">
            <div class="brand-icon-box d-flex align-items-center justify-content-center rounded-3 text-white shadow-sm" style="width: 42px; height: 42px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);">
                <i class="fa-solid fa-notes-medical fs-5"></i>
            </div>
            <span class="fw-bold text-white tracking-wide">PRJ301 <span class="text-info">Clinic</span></span>
        </a>

        <%-- Mobile Toggle Button --%>
        <button class="navbar-toggler border-0 text-white shadow-none" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar">
            <i class="fa-solid fa-bars fs-3"></i>
        </button>

        <%-- Navbar Links --%>
        <div class="collapse navbar-collapse" id="mainNavbar">
            <ul class="navbar-nav mx-auto mb-2 mb-lg-0 gap-1 gap-lg-3">
                <li class="nav-item">
                    <a class="nav-link text-white-50 text-white-hover px-3 py-2 rounded-3 transition-all" 
                       href="${pageContext.request.contextPath}/MainController?action=home">
                        <i class="fa-solid fa-house me-1 text-info"></i>Trang Chủ
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50 text-white-hover px-3 py-2 rounded-3 transition-all" 
                       href="${pageContext.request.contextPath}/MainController?action=home#services">
                        <i class="fa-solid fa-teeth me-1 text-info"></i>Dịch Vụ & Spa
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white-50 text-white-hover px-3 py-2 rounded-3 transition-all" 
                       href="${pageContext.request.contextPath}/MainController?action=home#doctors">
                        <i class="fa-solid fa-user-doctor me-1 text-info"></i>Đội Ngũ Bác Sĩ
                    </a>
                </li>
                <c:if test="${not empty sessionScope.SESSION_USER}">
                    <li class="nav-item">
                        <a class="nav-link text-white-50 text-white-hover px-3 py-2 rounded-3 transition-all" 
                           href="${pageContext.request.contextPath}/MainController?action=history">
                            <i class="fa-solid fa-clock-rotate-left me-1 text-info"></i>Lịch Sử Khám
                        </a>
                    </li>
                </c:if>
            </ul>

            <%-- Right User Auth Actions --%>
            <div class="d-flex align-items-center gap-3">
                <c:choose>
                    <c:when test="${empty sessionScope.SESSION_USER}">
                        <a href="${pageContext.request.contextPath}/MainController?action=login-page" 
                           class="btn btn-outline-glass px-4 rounded-pill">
                            <i class="fa-solid fa-right-to-bracket me-1"></i>Đăng Nhập
                        </a>
                        <a href="${pageContext.request.contextPath}/MainController?action=booking-page" 
                           class="btn btn-primary-gradient px-4 rounded-pill">
                            <i class="fa-solid fa-calendar-check me-1"></i>Đặt Lịch Ngay
                        </a>
                    </c:when>
                    <c:otherwise>
                        <%-- User Dropdown --%>
                        <div class="dropdown">
                            <button class="btn btn-outline-glass dropdown-toggle d-flex align-items-center gap-2 rounded-pill px-3 py-2" 
                                    type="button" data-bs-toggle="dropdown">
                                <div class="avatar-circle bg-info text-dark fw-bold rounded-circle d-flex align-items-center justify-content-center" style="width: 32px; height: 32px;">
                                    ${sessionScope.SESSION_USER.fullname.substring(0,1).toUpperCase()}
                                </div>
                                <span class="fw-semibold text-white me-1">${sessionScope.SESSION_USER.fullname}</span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end glass-dropdown border-0 shadow-lg mt-2 p-2 rounded-4">
                                <li class="dropdown-header text-muted px-3 py-2">
                                    <small>Vai trò đăng nhập:</small><br>
                                    <strong class="text-info">${sessionScope.SESSION_USER.role}</strong>
                                </li>
                                <li><hr class="dropdown-divider bg-secondary opacity-25"></li>
                                <li>
                                    <a class="dropdown-item rounded-3 py-2 text-white" 
                                       href="${pageContext.request.contextPath}/MainController?action=history">
                                        <i class="fa-solid fa-clock-rotate-left me-2 text-info"></i>Lịch Sử Đặt Khám
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item rounded-3 py-2 text-danger" 
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
