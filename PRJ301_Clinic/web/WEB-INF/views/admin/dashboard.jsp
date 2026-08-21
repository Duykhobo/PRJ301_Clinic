<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="/WEB-INF/views/components/head.jsp">
        <jsp:param name="title" value="Trung Tâm Quản Trị Admin | PRJ301 Clinic & Spa" />
    </jsp:include>
    <style>
        /* Glassmorphic Toast Container */
        #toastContainer {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            display: flex;
            flex-direction: column;
            gap: 10px;
            pointer-events: none;
        }
        .toast-glass {
            pointer-events: auto;
            min-width: 300px;
            background: rgba(15, 23, 42, 0.85);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid rgba(255, 255, 255, 0.15);
            border-radius: 14px;
            padding: 12px 18px;
            color: #fff;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            gap: 12px;
            animation: slideInRight 0.3s ease-out forwards;
        }
        .toast-glass.success { border-left: 4px solid #22c55e; }
        .toast-glass.error { border-left: 4px solid #ef4444; }
        @keyframes slideInRight {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        @keyframes fadeOutRight {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }

        /* Modern Glassmorphic Admin Inputs */
        .admin-glass-input {
            width: 100% !important;
            background: rgba(15, 23, 42, 0.75) !important;
            border: 1px solid rgba(56, 189, 248, 0.3) !important;
            color: #f8fafc !important;
            border-radius: 10px !important;
            padding: 10px 14px !important;
            font-size: 0.95rem !important;
            font-weight: 500 !important;
            box-shadow: inset 0 2px 4px rgba(0,0,0,0.2) !important;
            transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1) !important;
        }
        .admin-glass-input:focus {
            background: rgba(15, 23, 42, 0.95) !important;
            border-color: #0ea5e9 !important;
            box-shadow: 0 0 0 4px rgba(14, 165, 233, 0.25), inset 0 2px 4px rgba(0,0,0,0.3) !important;
            color: #38bdf8 !important;
            outline: none !important;
        }
    </style>
</head>

<body>
    <div class="d-flex flex-column min-vh-100">

        <%-- GLASSMORPHIC TOAST CONTAINER --%>
        <div id="toastContainer"></div>

        <%-- 1. WORKSPACE SIDEBAR --%>
        <jsp:include page="/WEB-INF/views/components/sidebar-admin.jsp" />

        <%-- 2. MAIN CONTENT AREA --%>
        <div class="container-fluid px-2 px-sm-4 py-3 py-sm-4 flex-grow-1">

            <%-- HERO BANNER --%>
            <div class="doctor-hero mb-4 animate-fade-in" style="background: linear-gradient(135deg, rgba(14, 165, 233, 0.25), rgba(16, 185, 129, 0.15)); border: 1px solid rgba(56, 189, 248, 0.3);">
                <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
                    <div class="d-flex align-items-center gap-3">
                        <div class="hero-avatar" style="background: linear-gradient(135deg, #0ea5e9, #10b981);">
                            <i class="fa-solid fa-user-shield text-white"></i>
                        </div>
                        <div>
                            <div class="d-flex align-items-center gap-2 mb-1">
                                <span class="live-dot" style="background:#10b981; box-shadow:0 0 10px #10b981;"></span>
                                <span style="font-size:.75rem; color:#38bdf8; font-weight:700; letter-spacing:.06em; text-transform:uppercase;">ADMIN CONTROL CENTER</span>
                            </div>
                            <h4 class="fw-bold text-white mb-1" style="font-size:1.3rem;">
                                Xin chào, Quản Trị Viên <c:out value="${sessionScope.LOGIN_USER.fullname}"/>
                            </h4>
                            <p class="mb-0" style="color:rgba(255,255,255,.6); font-size:.83rem;">
                                <i class="fa-solid fa-chart-pie me-1 text-cyan"></i>Báo cáo Doanh Thu Stored Proc &rsaquo; Từ ${startDate} đến ${endDate}
                            </p>
                        </div>
                    </div>

                    <%-- DATE RANGE FILTER --%>
                    <form action="${pageContext.request.contextPath}/admin/dashboard" method="GET" class="filter-bar" novalidate="true">
                        <input type="hidden" name="pageUser" value="${currentPageUser}">
                        <input type="hidden" name="pageService" value="${currentPageService}">
                        <input type="hidden" name="tab" value="${activeTab}">
                        <div class="filter-wrap">
                            <i class="fa-solid fa-calendar-days fi text-cyan"></i>
                            <input type="text" name="startDate" class="filter-input flatpickr-date" value="${startDate}" placeholder="Từ ngày" autocomplete="off">
                        </div>
                        <div class="filter-wrap">
                            <i class="fa-solid fa-calendar-days fi text-cyan"></i>
                            <input type="text" name="endDate" class="filter-input flatpickr-date" value="${endDate}" placeholder="Đến ngày" autocomplete="off">
                        </div>
                        <button type="submit" class="btn-filter" style="background: linear-gradient(135deg, #0ea5e9, #10b981);">
                            <i class="fa-solid fa-filter me-1"></i>Lọc Báo Cáo
                        </button>
                    </form>
                </div>
            </div>

            <%-- ── STAT CARDS ── --%>
            <div class="row g-3 mb-4 animate-fade-in">
                <div class="col-12 col-sm-6 col-lg-3">
                    <div class="stat-card green h-100">
                        <div class="stat-icon" style="background: rgba(34, 197, 94, 0.2); color: #4ade80;"><i class="fa-solid fa-sack-dollar"></i></div>
                        <div>
                            <div class="stat-value" style="color:#4ade80;">
                                <fmt:formatNumber value="${revenueReport.totalRevenuePaid}" type="number" /> VNĐ
                            </div>
                            <div class="stat-label">Tổng Doanh Thu</div>
                        </div>
                    </div>
                </div>
                <div class="col-12 col-sm-6 col-lg-3">
                    <div class="stat-card blue h-100">
                        <div class="stat-icon" style="background: rgba(59, 130, 246, 0.2); color: #60a5fa;"><i class="fa-solid fa-qrcode"></i></div>
                        <div>
                            <div class="stat-value" style="color:#60a5fa;">
                                <fmt:formatNumber value="${revenueReport.sepayRevenue}" type="number" /> VNĐ
                            </div>
                            <div class="stat-label">Doanh Thu SePay QR</div>
                        </div>
                    </div>
                </div>
                <div class="col-12 col-sm-6 col-lg-3">
                    <div class="stat-card amber h-100">
                        <div class="stat-icon" style="background: rgba(245, 158, 11, 0.2); color: #fbbf24;"><i class="fa-solid fa-money-bill-wave"></i></div>
                        <div>
                            <div class="stat-value" style="color:#fbbf24;">
                                <fmt:formatNumber value="${revenueReport.cashRevenue}" type="number" /> VNĐ
                            </div>
                            <div class="stat-label">Doanh Thu Tiền Mặt</div>
                        </div>
                    </div>
                </div>
                <div class="col-12 col-sm-6 col-lg-3">
                    <div class="stat-card red h-100">
                        <div class="stat-icon" style="background: rgba(239, 68, 68, 0.2); color: #fca5a5;"><i class="fa-solid fa-calendar-check"></i></div>
                        <div>
                            <div class="stat-value" style="color:#fca5a5;">
                                ${revenueReport.completedAppointments} / ${revenueReport.totalAppointments}
                            </div>
                            <div class="stat-label">Ca Đã Hoàn Tất</div>
                        </div>
                    </div>
                </div>
            </div>

            <%-- ── TAB NAVIGATION ── --%>
            <ul class="nav nav-pills mb-4 gap-2" id="adminTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link ${activeTab == 'users' or empty activeTab ? 'active' : ''} px-4 py-2 rounded-pill fw-bold text-white" id="users-tab" data-bs-toggle="pill" data-bs-target="#users-panel" type="button" role="tab" style="background: linear-gradient(135deg, #ef4444, #f59e0b);">
                        <i class="fa-solid fa-users me-2"></i>Quản Lý Người Dùng
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link ${activeTab == 'services' ? 'active' : ''} px-4 py-2 rounded-pill fw-bold text-white" id="services-tab" data-bs-toggle="pill" data-bs-target="#services-panel" type="button" role="tab" style="background: rgba(255, 255, 255, 0.08); border: 1px solid rgba(255, 255, 255, 0.15);">
                        <i class="fa-solid fa-concierge-bell me-2"></i>Quản Lý Dịch Vụ
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link ${activeTab == 'settings' ? 'active' : ''} px-4 py-2 rounded-pill fw-bold text-white" id="settings-tab" data-bs-toggle="pill" data-bs-target="#settings-panel" type="button" role="tab" style="background: rgba(255, 255, 255, 0.08); border: 1px solid rgba(255, 255, 255, 0.15);">
                        <i class="fa-solid fa-gears me-2"></i>Cấu Hình Hệ Thống
                    </button>
                </li>
            </ul>

            <%-- ── TAB PANELS CONTENT ── --%>
            <div class="tab-content" id="adminTabsContent">

                <%-- TAB 1: QUẢN LÝ NGƯỜI DÙNG --%>
                <div class="tab-pane fade ${activeTab == 'users' or empty activeTab ? 'show active' : ''}" id="users-panel" role="tabpanel">
                    <div class="panel animate-fade-in mb-4">
                        <div class="panel-header d-flex justify-content-between align-items-center flex-wrap gap-2">
                            <div class="panel-title">
                                <i class="fa-solid fa-users text-danger me-2"></i>Danh Sách Người Dùng (Trang ${currentPageUser} / ${totalPagesUser})
                            </div>
                            <%-- LIVE SEARCH USER --%>
                            <div class="position-relative" style="max-width:280px; width:100%;">
                                <i class="fa-solid fa-magnifying-glass position-absolute top-50 start-0 translate-middle-y ms-3 text-white-50" style="font-size:.82rem; pointer-events:none;"></i>
                                <input type="text" id="searchUser" class="form-control form-control-sm bg-dark text-white border-secondary rounded-pill ps-5 pe-3" placeholder="Tìm nhanh Tên, Username, SĐT..." onkeyup="filterTable('searchUser', 'usersTable')">
                            </div>
                        </div>
                        <div style="overflow-x:auto;">
                            <table class="tbl" id="usersTable">
                                <thead>
                                    <tr>
                                        <th style="padding-left:1.4rem;">ID</th>
                                        <th>Họ &amp; Tên</th>
                                        <th>Username / Email</th>
                                        <th>Số Điện Thoại</th>
                                        <th>Vai Trò (Role)</th>
                                        <th>Trạng Thái</th>
                                        <th style="text-align:right; padding-right:1.4rem;">Thao Tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="u" items="${usersList}">
                                        <tr id="user-row-${u.id}">
                                            <td style="padding-left:1.4rem;" class="fw-bold text-white">#${u.id}</td>
                                            <td>
                                                <div class="fw-bold text-white"><c:out value="${u.fullname}"/></div>
                                            </td>
                                            <td>
                                                <div class="text-white-50"><c:out value="${u.username}"/></div>
                                                <div class="text-muted" style="font-size:.78rem;"><c:out value="${u.email}"/></div>
                                            </td>
                                            <td><c:out value="${u.phone}"/></td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/admin/dashboard" method="POST" class="d-inline-flex align-items-center gap-1 ajax-form" onsubmit="return false;" novalidate="true">
                                                    <input type="hidden" name="action" value="update-user-role">
                                                    <input type="hidden" name="userId" value="${u.id}">
                                                    <select name="role" class="form-select form-select-sm bg-dark text-white border-secondary" style="font-size:.78rem; width:auto;" onchange="submitRoleAjax(this, ${u.id})">
                                                        <option value="PATIENT" ${u.role == 'PATIENT' ? 'selected' : ''}>PATIENT</option>
                                                        <option value="DOCTOR" ${u.role == 'DOCTOR' ? 'selected' : ''}>DOCTOR</option>
                                                        <option value="RECEPTIONIST" ${u.role == 'RECEPTIONIST' ? 'selected' : ''}>RECEPTIONIST</option>
                                                        <option value="ADMIN" ${u.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                                                    </select>
                                                </form>
                                            </td>
                                            <td id="user-status-td-${u.id}">
                                                <c:choose>
                                                    <c:when test="${u.status}">
                                                        <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-check-circle me-1"></i>Active</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger-subtle text-danger border border-danger-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-ban me-1"></i>Banned</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="text-align:right; padding-right:1.4rem;" id="user-action-td-${u.id}">
                                                <c:if test="${u.id != sessionScope.LOGIN_USER.id}">
                                                    <button type="button" class="btn btn-sm ${u.status ? 'btn-outline-danger' : 'btn-outline-success'} rounded-pill px-3" style="font-size:.78rem;" onclick="toggleUserStatusAjax(${u.id})">
                                                        <i class="fa-solid ${u.status ? 'fa-lock' : 'fa-unlock'} me-1"></i><span>${u.status ? 'Khóa' : 'Mở Khóa'}</span>
                                                    </button>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <%-- USER PAGINATION BAR --%>
                        <c:if test="${totalPagesUser > 1}">
                            <div class="d-flex justify-content-center p-3 border-top border-secondary opacity-75">
                                <nav>
                                    <ul class="pagination pagination-sm m-0">
                                        <c:forEach var="p" begin="1" end="${totalPagesUser}">
                                            <li class="page-item ${p == currentPageUser ? 'active' : ''}">
                                                <a class="page-link bg-dark text-white border-secondary" href="${pageContext.request.contextPath}/admin/dashboard?pageUser=${p}&pageService=${currentPageService}&tab=users">${p}</a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </nav>
                            </div>
                        </c:if>

                    </div>
                </div>

                <%-- TAB 2: QUẢN LÝ DỊCH VỤ --%>
                <div class="tab-pane fade ${activeTab == 'services' ? 'show active' : ''}" id="services-panel" role="tabpanel">
                    <div class="panel animate-fade-in mb-4">
                        <div class="panel-header d-flex justify-content-between align-items-center flex-wrap gap-2">
                            <div class="panel-title">
                                <i class="fa-solid fa-concierge-bell text-warning me-2"></i>Danh Mục Dịch Vụ (Trang ${currentPageService} / ${totalPagesService})
                            </div>
                            <div class="d-flex align-items-center gap-2">
                                <%-- LIVE SEARCH SERVICE --%>
                                <div class="position-relative" style="max-width:240px; width:100%;">
                                    <i class="fa-solid fa-magnifying-glass position-absolute top-50 start-0 translate-middle-y ms-3 text-white-50" style="font-size:.82rem; pointer-events:none;"></i>
                                    <input type="text" id="searchService" class="form-control form-control-sm bg-dark text-white border-secondary rounded-pill ps-5 pe-3" placeholder="Tìm nhanh Dịch vụ..." onkeyup="filterTable('searchService', 'servicesTable')">
                                </div>
                                <button type="button" class="btn btn-warning btn-sm rounded-pill fw-bold px-3" data-bs-toggle="modal" data-bs-target="#addServiceModal">
                                    <i class="fa-solid fa-plus me-1"></i>Thêm Dịch Vụ Mới
                                </button>
                            </div>
                        </div>
                        <div style="overflow-x:auto;">
                            <table class="tbl" id="servicesTable">
                                <thead>
                                    <tr>
                                        <th style="padding-left:1.4rem;">ID</th>
                                        <th>Tên Dịch Vụ</th>
                                        <th>Đơn Giá</th>
                                        <th>Thời Gian</th>
                                        <th>Trạng Thái</th>
                                        <th style="text-align:right; padding-right:1.4rem;">Thao Tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="s" items="${servicesList}">
                                        <tr id="service-row-${s.id}">
                                            <td style="padding-left:1.4rem;" class="fw-bold text-white">#${s.id}</td>
                                            <td>
                                                <div class="fw-bold text-white"><c:out value="${s.serviceName}"/></div>
                                                <div class="text-muted" style="font-size:.78rem;"><c:out value="${s.description}"/></div>
                                            </td>
                                            <td class="fw-bold text-warning">
                                                <fmt:formatNumber value="${s.price}" type="number" /> VNĐ
                                            </td>
                                            <td>${s.durationMinutes} phút</td>
                                            <td id="service-status-td-${s.id}">
                                                <c:choose>
                                                    <c:when test="${s.status}">
                                                        <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-eye me-1"></i>Hiển Thị</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-eye-slash me-1"></i>Bị Ẩn</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="text-align:right; padding-right:1.4rem;" id="service-action-td-${s.id}">
                                                <button type="button" class="btn btn-sm ${s.status ? 'btn-outline-secondary' : 'btn-outline-warning'} rounded-pill px-3" style="font-size:.78rem;" onclick="toggleServiceStatusAjax(${s.id})">
                                                    <i class="fa-solid ${s.status ? 'fa-eye-slash' : 'fa-eye'} me-1"></i><span>${s.status ? 'Ẩn Dịch Vụ' : 'Hiện Dịch Vụ'}</span>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <%-- SERVICE PAGINATION BAR --%>
                        <c:if test="${totalPagesService > 1}">
                            <div class="d-flex justify-content-center p-3 border-top border-secondary opacity-75">
                                <nav>
                                    <ul class="pagination pagination-sm m-0">
                                        <c:forEach var="p" begin="1" end="${totalPagesService}">
                                            <li class="page-item ${p == currentPageService ? 'active' : ''}">
                                                <a class="page-link bg-dark text-white border-secondary" href="${pageContext.request.contextPath}/admin/dashboard?pageUser=${currentPageUser}&pageService=${p}&tab=services">${p}</a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </nav>
                            </div>
                        </c:if>

                    </div>
                </div>

                <%-- TAB 3: CẤU HÌNH HỆ THỐNG --%>
                <div class="tab-pane fade ${activeTab == 'settings' ? 'show active' : ''}" id="settings-panel" role="tabpanel">
                    <div class="panel animate-fade-in mb-4">
                        <div class="panel-header">
                            <div class="panel-title">
                                <i class="fa-solid fa-gears text-info me-2"></i>Cấu Hình Động Hệ Thống (ClinicSettings)
                            </div>
                        </div>
                        <div class="p-4">
                            <form action="${pageContext.request.contextPath}/admin/dashboard" method="POST" id="settingsForm" novalidate="true">
                                <input type="hidden" name="action" value="update-settings">
                                <input type="hidden" name="ajax" value="true">
                                <div class="row g-4">
                                    <h6 class="text-cyan fw-bold mb-0"><i class="fa-solid fa-hospital me-2"></i>Thông Tin Cơ Bản Phòng Khám &amp; Giờ Hoạt Động</h6>

                                    <div class="col-12 col-md-6">
                                        <label class="form-label text-white-50 fw-semibold">Tên Phòng Khám &amp; Spa</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-dark border-secondary text-cyan"><i class="fa-solid fa-clinic-medical"></i></span>
                                            <input type="text" name="CLINIC_NAME" class="form-control admin-glass-input" value="${not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : (not empty settingsMap['clinic_name'] ? settingsMap['clinic_name'] : 'PRJ301 Clinic & Spa')}" required>
                                        </div>
                                    </div>
                                    <div class="col-12 col-md-6">
                                        <label class="form-label text-white-50 fw-semibold">Hotline Tư Vấn / Hỗ Trợ</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-dark border-secondary text-cyan"><i class="fa-solid fa-phone"></i></span>
                                            <input type="text" name="CLINIC_HOTLINE" class="form-control admin-glass-input" value="${not empty settingsMap['CLINIC_HOTLINE'] ? settingsMap['CLINIC_HOTLINE'] : (not empty settingsMap['hotline'] ? settingsMap['hotline'] : '0901234567')}" required>
                                        </div>
                                    </div>
                                    <div class="col-12 col-md-6">
                                        <label class="form-label text-white-50 fw-semibold">Email Hỗ Trợ Khách Hàng</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-dark border-secondary text-cyan"><i class="fa-solid fa-envelope"></i></span>
                                            <input type="email" name="CLINIC_EMAIL" class="form-control admin-glass-input" value="${not empty settingsMap['CLINIC_EMAIL'] ? settingsMap['CLINIC_EMAIL'] : 'contact@prj301clinic.com'}" required>
                                        </div>
                                    </div>
                                    <div class="col-12 col-md-6">
                                        <label class="form-label text-white-50 fw-semibold">Giờ Mở Cửa Hoạt Động</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-dark border-secondary text-cyan"><i class="fa-regular fa-clock"></i></span>
                                            <input type="text" name="OPENING_HOURS" class="form-control admin-glass-input" value="${not empty settingsMap['OPENING_HOURS'] ? settingsMap['OPENING_HOURS'] : '08:00 - 20:00 (Từ Thứ 2 đến Chủ Nhật)'}" required>
                                        </div>
                                    </div>
                                    <div class="col-12">
                                        <label class="form-label text-white-50 fw-semibold">Địa Chỉ Phòng Khám</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-dark border-secondary text-cyan"><i class="fa-solid fa-location-dot"></i></span>
                                            <input type="text" name="CLINIC_ADDRESS" class="form-control admin-glass-input" value="${not empty settingsMap['CLINIC_ADDRESS'] ? settingsMap['CLINIC_ADDRESS'] : (not empty settingsMap['address'] ? settingsMap['address'] : '123 Đường Nguyễn Văn Cừ, Phường 4, Quận 5, TP.HCM')}" required>
                                        </div>
                                    </div>

                                    <div class="col-12 mt-3">
                                        <label class="form-label text-white-50 fw-semibold mb-2">
                                            <i class="fa-solid fa-clock text-cyan me-1"></i>Danh Sách Ca Khám Tự Động Sinh (CLINIC_TIME_SLOTS)
                                        </label>
                                        <div class="p-3 rounded-3 mb-2" style="background: rgba(15, 23, 42, 0.6); border: 1px dashed rgba(56, 189, 248, 0.3);">
                                            <div class="text-white-50 fs-8 mb-2 d-flex justify-content-between align-items-center">
                                                <span><i class="fa-solid fa-hand-pointer text-cyan me-1"></i>Bấm chọn/bỏ chọn để bật tắt mốc giờ nhanh:</span>
                                                <button type="button" class="btn btn-xs btn-outline-info rounded-pill px-2 py-0" onclick="selectAllSlots(true)">Chọn Tất Cả</button>
                                            </div>
                                            <div class="d-flex flex-wrap gap-2" id="adminSlotChips">
                                                <%-- Visual Chips JS rendered --%>
                                            </div>
                                        </div>
                                        <input type="text" name="CLINIC_TIME_SLOTS" id="clinicTimeSlotsInput" class="form-control admin-glass-input text-cyan fw-bold" value="${not empty settingsMap['CLINIC_TIME_SLOTS'] ? settingsMap['CLINIC_TIME_SLOTS'] : '08:00,09:00,10:00,11:00,14:00,15:00,16:00,17:00'}" required oninput="renderSlotChipsFromInput()">
                                        <div class="form-text text-white-50 fs-8 mt-1">Chuỗi phân cách bằng dấu phẩy. Ca khám 60 phút sẽ tự động chèn vào bảng <code>DoctorSchedules</code> khi chọn ngày.</div>
                                    </div>

                                    <hr class="my-4 border-secondary opacity-25">
                                    <h6 class="text-warning fw-bold mb-0"><i class="fa-solid fa-university me-2"></i>Cấu Hình Ngân Hàng Thanh Toán VietQR SePay</h6>

                                    <div class="col-12 col-md-4">
                                        <label class="form-label text-white-50 fw-semibold">Tên Ngân Hàng (Bank Name)</label>
                                        <input type="text" name="SEPAY_BANK_NAME" class="form-control admin-glass-input" value="${not empty settingsMap['SEPAY_BANK_NAME'] ? settingsMap['SEPAY_BANK_NAME'] : (not empty settingsMap['bank_name'] ? settingsMap['bank_name'] : 'Sacombank')}" required>
                                    </div>
                                    <div class="col-12 col-md-4">
                                        <label class="form-label text-white-50 fw-semibold">Số Tài Khoản (Account Number)</label>
                                        <input type="text" name="SEPAY_BANK_ACC" class="form-control admin-glass-input" value="${not empty settingsMap['SEPAY_BANK_ACC'] ? settingsMap['SEPAY_BANK_ACC'] : (not empty settingsMap['bank_account'] ? settingsMap['bank_account'] : '070148520060')}" required>
                                    </div>
                                    <div class="col-12 col-md-4">
                                        <label class="form-label text-white-50 fw-semibold">Chủ Tài Khoản (Account Owner)</label>
                                        <input type="text" name="SEPAY_ACCOUNT_HOLDER" class="form-control admin-glass-input" value="${not empty settingsMap['SEPAY_ACCOUNT_HOLDER'] ? settingsMap['SEPAY_ACCOUNT_HOLDER'] : (not empty settingsMap['bank_owner'] ? settingsMap['bank_owner'] : 'NGUYEN THANH DUY')}" required>
                                    </div>
                                    <div class="col-12 mt-4 text-end">
                                        <button type="button" class="btn btn-warning fw-bold px-4 py-3 rounded-pill shadow" onclick="saveSettingsAjax()">
                                            <i class="fa-solid fa-floppy-disk me-2"></i>Lưu Thay Đổi Cấu Hình (Ajax)
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <%-- FOOTER --%>
        <jsp:include page="/WEB-INF/views/components/footer.jsp" />

    </div>

    <%-- MODAL THÊM DỊCH VỤ MỚI --%>
    <div class="modal fade" id="addServiceModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content bg-dark text-white border-secondary">
                <div class="modal-header border-secondary">
                    <h5 class="modal-title fw-bold text-warning"><i class="fa-solid fa-plus-circle me-2"></i>Thêm Dịch Vụ Khám Mới</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/admin/dashboard" method="POST" novalidate="true">
                    <input type="hidden" name="action" value="add-service">
                    <input type="hidden" name="pageUser" value="${currentPageUser}">
                    <input type="hidden" name="pageService" value="${currentPageService}">
                    <input type="hidden" name="tab" value="services">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label text-white-50">Tên Dịch Vụ (*)</label>
                            <input type="text" name="serviceName" class="form-input" placeholder="Ví dụ: Tẩy Trắng Răng Laser" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-white-50">Đơn Giá VNĐ (*)</label>
                            <input type="number" name="price" class="form-input" placeholder="Ví dụ: 1500000" min="0" step="10000" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-white-50">Thời Gian Khám (Phút)</label>
                            <input type="number" name="durationMinutes" class="form-input" value="60" min="15" step="15" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-white-50">Mô Tả Chi Tiết</label>
                            <textarea name="description" class="form-input" rows="3" placeholder="Nhập mô tả về dịch vụ..."></textarea>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-white-50">Đường Dẫn Ảnh (Image URL)</label>
                            <input type="text" name="imageUrl" class="form-input" value="assets/images/default-service.jpg">
                        </div>
                    </div>
                    <div class="modal-footer border-secondary">
                        <button type="button" class="btn btn-secondary rounded-pill" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-warning rounded-pill fw-bold"><i class="fa-solid fa-check me-1"></i>Lưu Dịch Vụ</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%-- SCRIPTS --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <script>
        const CONTEXT_PATH = "${pageContext.request.contextPath}";

        document.addEventListener("DOMContentLoaded", function () {
            flatpickr(".flatpickr-date", {
                dateFormat: "Y-m-d",
                locale: "vn"
            });
        });

        // 1. GLASSMORPHIC TOAST NOTIFICATION HELPER
        function showToast(type, message) {
            let container = document.getElementById("toastContainer");
            if (!container) {
                container = document.createElement("div");
                container.id = "toastContainer";
                document.body.appendChild(container);
            }
            const toast = document.createElement("div");
            toast.className = "toast-glass " + type;
            const iconClass = type === 'success' ? 'fa-solid fa-circle-check text-success' : 'fa-solid fa-triangle-exclamation text-danger';
            toast.innerHTML = '<i class="' + iconClass + ' fs-5"></i><span style="font-size:.88rem; font-weight:600;">' + (message || '') + '</span>';
            container.appendChild(toast);

            setTimeout(() => {
                toast.style.animation = "fadeOutRight 0.3s ease-in forwards";
                setTimeout(() => toast.remove(), 300);
            }, 3500);
        }

        // 2. REAL-TIME TABLE FILTER
        function filterTable(inputId, tableId) {
            const query = document.getElementById(inputId).value.toLowerCase().trim();
            const table = document.getElementById(tableId);
            const rows = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");

            for (let i = 0; i < rows.length; i++) {
                const text = rows[i].innerText.toLowerCase();
                if (text.includes(query)) {
                    rows[i].style.display = "";
                } else {
                    rows[i].style.display = "none";
                }
            }
        }

        // 3. AJAX TOGGLE USER STATUS
        function toggleUserStatusAjax(userId) {
            const formData = new URLSearchParams();
            formData.append("action", "toggle-user-status");
            formData.append("userId", userId);
            formData.append("ajax", "true");

            fetch(CONTEXT_PATH + "/admin/dashboard", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
                    "X-Requested-With": "XMLHttpRequest"
                },
                body: formData
            })
            .then(res => res.text())
            .then(text => {
                const data = JSON.parse(text);
                if (data.success) {
                    showToast("success", data.message);
                    const statusTd = document.getElementById("user-status-td-" + userId);
                    const actionTd = document.getElementById("user-action-td-" + userId);

                    if (data.newStatus) {
                        statusTd.innerHTML = '<span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-check-circle me-1"></i>Active</span>';
                        actionTd.innerHTML = '<button type="button" class="btn btn-sm btn-outline-danger rounded-pill px-3" style="font-size:.78rem;" onclick="toggleUserStatusAjax(' + userId + ')"><i class="fa-solid fa-lock me-1"></i><span>Khóa</span></button>';
                    } else {
                        statusTd.innerHTML = '<span class="badge bg-danger-subtle text-danger border border-danger-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-ban me-1"></i>Banned</span>';
                        actionTd.innerHTML = '<button type="button" class="btn btn-sm btn-outline-success rounded-pill px-3" style="font-size:.78rem;" onclick="toggleUserStatusAjax(' + userId + ')"><i class="fa-solid fa-unlock me-1"></i><span>Mở Khóa</span></button>';
                    }
                } else {
                    showToast("error", data.message || "Không thể cập nhật trạng thái người dùng.");
                }
            })
            .catch(err => {
                console.error("AJAX Error:", err);
                showToast("error", "Lỗi kết nối máy chủ!");
            });
        }

        // 4. AJAX UPDATE USER ROLE
        function submitRoleAjax(selectElem, userId) {
            const newRole = selectElem.value;
            const formData = new URLSearchParams();
            formData.append("action", "update-user-role");
            formData.append("userId", userId);
            formData.append("role", newRole);
            formData.append("ajax", "true");

            fetch(CONTEXT_PATH + "/admin/dashboard", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
                    "X-Requested-With": "XMLHttpRequest"
                },
                body: formData
            })
            .then(res => res.text())
            .then(text => {
                const data = JSON.parse(text);
                if (data.success) {
                    showToast("success", data.message);
                } else {
                    showToast("error", data.message || "Không thể đổi vai trò người dùng.");
                }
            })
            .catch(err => {
                console.error("AJAX Error:", err);
                showToast("error", "Lỗi kết nối máy chủ!");
            });
        }

        // 5. AJAX TOGGLE SERVICE STATUS
        function toggleServiceStatusAjax(serviceId) {
            const formData = new URLSearchParams();
            formData.append("action", "toggle-service-status");
            formData.append("serviceId", serviceId);
            formData.append("ajax", "true");

            fetch(CONTEXT_PATH + "/admin/dashboard", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
                    "X-Requested-With": "XMLHttpRequest"
                },
                body: formData
            })
            .then(res => res.text())
            .then(text => {
                const data = JSON.parse(text);
                if (data.success) {
                    showToast("success", data.message);
                    const statusTd = document.getElementById("service-status-td-" + serviceId);
                    const actionTd = document.getElementById("service-action-td-" + serviceId);

                    if (data.newStatus) {
                        statusTd.innerHTML = '<span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-eye me-1"></i>Hiển Thị</span>';
                        actionTd.innerHTML = '<button type="button" class="btn btn-sm btn-outline-secondary rounded-pill px-3" style="font-size:.78rem;" onclick="toggleServiceStatusAjax(' + serviceId + ')"><i class="fa-solid fa-eye-slash me-1"></i><span>Ẩn Dịch Vụ</span></button>';
                    } else {
                        statusTd.innerHTML = '<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-eye-slash me-1"></i>Bị Ẩn</span>';
                        actionTd.innerHTML = '<button type="button" class="btn btn-sm btn-outline-warning rounded-pill px-3" style="font-size:.78rem;" onclick="toggleServiceStatusAjax(' + serviceId + ')"><i class="fa-solid fa-eye me-1"></i><span>Hiện Dịch Vụ</span></button>';
                    }
                } else {
                    showToast("error", data.message || "Không thể cập nhật trạng thái dịch vụ.");
                }
            })
            .catch(err => {
                console.error("AJAX Error:", err);
                showToast("error", "Lỗi kết nối máy chủ!");
            });
        }

        // 6. AJAX SAVE CLINIC SETTINGS
        function saveSettingsAjax() {
            const form = document.getElementById("settingsForm");
            const formData = new URLSearchParams(new FormData(form));

            fetch(CONTEXT_PATH + "/admin/dashboard", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
                    "X-Requested-With": "XMLHttpRequest"
                },
                body: formData
            })
            .then(res => res.text())
            .then(text => {
                const data = JSON.parse(text);
                if (data.success) {
                    showToast("success", data.message);
                } else {
                    showToast("error", data.message || "Không thể lưu cấu hình.");
                }
            })
            .catch(err => {
                console.error("AJAX Error:", err);
                showToast("error", "Lỗi kết nối máy chủ!");
            });
        }

        // 7. INTERACTIVE TIME SLOT CHIPS FOR ADMIN
        const availableDefaultHours = ["08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"];

        function renderSlotChipsFromInput() {
            const input = document.getElementById('clinicTimeSlotsInput');
            const container = document.getElementById('adminSlotChips');
            if (!input || !container) return;

            const currentVal = input.value || '';
            const selectedList = currentVal.split(',').map(s => s.trim());

            let html = '';
            availableDefaultHours.forEach(hour => {
                const isSelected = selectedList.includes(hour);
                const styleAttr = isSelected
                    ? 'background: linear-gradient(135deg, #0ea5e9, #10b981) !important; color: #ffffff !important; border: 1px solid #38bdf8 !important; box-shadow: 0 4px 12px rgba(14, 165, 233, 0.35) !important;'
                    : 'background: rgba(30, 41, 59, 0.85) !important; color: #94a3b8 !important; border: 1px solid rgba(148, 163, 184, 0.25) !important;';
                const icon = isSelected ? 'fa-solid fa-circle-check text-white' : 'fa-regular fa-circle text-muted';
                html += '<button type="button" class="btn btn-sm rounded-pill px-3 py-1 fw-bold d-flex align-items-center gap-1" style="' + styleAttr + '" onclick="toggleSlotChip(\'' + hour + '\')">' +
                        '<i class="' + icon + ' me-1"></i> ' + hour +
                        '</button>';
            });
            container.innerHTML = html;
        }

        function toggleSlotChip(hour) {
            const input = document.getElementById('clinicTimeSlotsInput');
            if (!input) return;
            let selectedList = input.value ? input.value.split(',').map(s => s.trim()).filter(Boolean) : [];
            if (selectedList.includes(hour)) {
                selectedList = selectedList.filter(h => h !== hour);
            } else {
                selectedList.push(hour);
                selectedList.sort();
            }
            input.value = selectedList.join(',');
            renderSlotChipsFromInput();
        }

        function selectAllSlots(all) {
            const input = document.getElementById('clinicTimeSlotsInput');
            if (!input) return;
            input.value = all ? availableDefaultHours.join(',') : '';
            renderSlotChipsFromInput();
        }

        document.addEventListener('DOMContentLoaded', function() {
            renderSlotChipsFromInput();
        });
    </script>
</body>

</html>
