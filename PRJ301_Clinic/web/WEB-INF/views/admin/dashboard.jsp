<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">

    <title>Trung Tâm Quản Trị Admin | <c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Phòng Khám & Spa')}"/></title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>

<body>
    <div class="d-flex flex-column min-vh-100">

        <%-- GLASSMORPHIC TOAST CONTAINER --%>
        <div id="toastContainer"></div>

        <%-- 1. WORKSPACE SIDEBAR --%>
        <jsp:include page="/WEB-INF/views/components/sidebar-admin.jsp" />

        <%-- 2. MAIN CONTENT AREA --%>
        <div class="container-fluid px-2 px-sm-4 py-3 py-sm-4 flex-grow-1">

            <%-- ── HERO BANNER ── --%>
            <div class="adm-hero mb-4 animate-fade-in">
                <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
                    <div class="d-flex align-items-center gap-3">
                        <div class="hero-avatar" style="background: linear-gradient(135deg, #0ea5e9, #10b981);">
                            <i class="fa-solid fa-user-shield text-white"></i>
                        </div>
                        <div>
                            <div class="d-flex align-items-center gap-2 mb-1">
                                <span class="live-dot" style="background:#10b981; box-shadow:0 0 10px #10b981;"></span>
                                <span style="font-size:.72rem; color:#38bdf8; font-weight:750; letter-spacing:.08em; text-transform:uppercase;">HỆ THỐNG QUẢN TRỊ ADMIN</span>
                            </div>
                            <h4 class="fw-bold text-white mb-1" style="font-size:1.28rem;">
                                Xin chào, <c:out value="${sessionScope.LOGIN_USER.fullname}"/>
                            </h4>
                            <p class="mb-0 text-white-50" style="font-size:.82rem;">
                                <i class="fa-solid fa-chart-pie me-1 text-cyan"></i>Báo cáo Doanh thu &amp; Chỉ số Vận hành &rsaquo; Từ <strong>${startDate}</strong> đến <strong>${endDate}</strong>
                            </p>
                        </div>
                    </div>

                    <%-- DATE RANGE FILTER --%>
                    <form action="${pageContext.request.contextPath}/admin/dashboard" method="GET" class="adm-filter-bar" novalidate="true">
                        <input type="hidden" name="pageUser" value="${currentPageUser}">
                        <input type="hidden" name="pageService" value="${currentPageService}">
                        <input type="hidden" name="tab" value="${activeTab}">
                        <div class="adm-filter-group">
                            <i class="fa-regular fa-calendar-days text-cyan" style="font-size:.82rem;"></i>
                            <input type="text" name="startDate" class="adm-filter-input flatpickr-date" value="${startDate}" placeholder="Từ ngày" autocomplete="off">
                        </div>
                        <div class="adm-filter-group">
                            <i class="fa-regular fa-calendar-days text-cyan" style="font-size:.82rem;"></i>
                            <input type="text" name="endDate" class="adm-filter-input flatpickr-date" value="${endDate}" placeholder="Đến ngày" autocomplete="off">
                        </div>
                        <button type="submit" class="btn-adm-filter">
                            <i class="fa-solid fa-filter me-1"></i>Lọc Dữ Liệu
                        </button>
                    </form>
                </div>
            </div>

            <%-- ── KPI STAT CARDS (4-COLUMNS GRID) ── --%>
            <div class="row g-3 mb-4 animate-fade-in">
                <%-- CARD 1: TỔNG DOANH THU --%>
                <div class="col-12 col-sm-6 col-xl-3">
                    <div class="adm-kpi-card kpi-emerald h-100">
                        <div class="adm-kpi-header">
                            <span class="adm-kpi-label" style="color: #34d399;">TỔNG DOANH THU</span>
                            <div class="adm-kpi-icon icon-emerald"><i class="fa-solid fa-sack-dollar"></i></div>
                        </div>
                        <div class="adm-kpi-value" style="color: #34d399;">
                            <fmt:formatNumber value="${revenueReport.totalRevenuePaid}" pattern="#,##0" maxFractionDigits="0" /> <span class="adm-kpi-unit">VNĐ</span>
                        </div>
                        <div class="adm-kpi-meta text-white-50">
                            <i class="fa-solid fa-circle-check text-emerald me-1"></i>Đã thanh toán (PAID)
                        </div>
                    </div>
                </div>

                <%-- CARD 2: SEPAY QR REVENUE --%>
                <div class="col-12 col-sm-6 col-xl-3">
                    <div class="adm-kpi-card kpi-cyan h-100">
                        <div class="adm-kpi-header">
                            <span class="adm-kpi-label" style="color: #38bdf8;">DOANH THU SEPAY</span>
                            <div class="adm-kpi-icon icon-cyan"><i class="fa-solid fa-qrcode"></i></div>
                        </div>
                        <div class="adm-kpi-value" style="color: #38bdf8;">
                            <fmt:formatNumber value="${revenueReport.sepayRevenue}" pattern="#,##0" maxFractionDigits="0" /> <span class="adm-kpi-unit">VNĐ</span>
                        </div>
                        <div class="adm-kpi-meta text-white-50">
                            <i class="fa-solid fa-bolt text-cyan me-1"></i>Tự động qua VietQR
                        </div>
                    </div>
                </div>

                <%-- CARD 3: CASH REVENUE --%>
                <div class="col-12 col-sm-6 col-xl-3">
                    <div class="adm-kpi-card kpi-amber h-100">
                        <div class="adm-kpi-header">
                            <span class="adm-kpi-label" style="color: #fbbf24;">DOANH THU TIỀN MẶT</span>
                            <div class="adm-kpi-icon icon-amber"><i class="fa-solid fa-money-bill-wave"></i></div>
                        </div>
                        <div class="adm-kpi-value" style="color: #fbbf24;">
                            <fmt:formatNumber value="${revenueReport.cashRevenue}" pattern="#,##0" maxFractionDigits="0" /> <span class="adm-kpi-unit">VNĐ</span>
                        </div>
                        <div class="adm-kpi-meta text-white-50">
                            <i class="fa-solid fa-cash-register text-warning me-1"></i>Thu trực tiếp tại quầy
                        </div>
                    </div>
                </div>

                <%-- CARD 4: COMPLETED APPOINTMENTS --%>
                <div class="col-12 col-sm-6 col-xl-3">
                    <div class="adm-kpi-card kpi-rose h-100">
                        <div class="adm-kpi-header">
                            <span class="adm-kpi-label" style="color: #f87171;">LƯỢNG KHÁM XONG</span>
                            <div class="adm-kpi-icon icon-rose"><i class="fa-solid fa-calendar-check"></i></div>
                        </div>
                        <div class="adm-kpi-value" style="color: #f87171;">
                            ${revenueReport.completedAppointments} <span class="adm-kpi-unit">/ ${revenueReport.totalAppointments} ca</span>
                        </div>
                        <div class="adm-kpi-meta text-white-50">
                            <i class="fa-solid fa-chart-line text-danger me-1"></i>Tiến độ phục vụ bệnh nhân
                        </div>
                    </div>
                </div>
            </div>

            <%-- ── TAB SEGMENTED CONTROL (TRAY) ── --%>
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div class="adm-tab-tray">
                    <a href="${pageContext.request.contextPath}/admin/dashboard?tab=users" class="adm-tab-link ${activeTab == 'users' or empty activeTab ? 'active' : ''}">
                        <i class="fa-solid fa-users"></i>Quản Lý Người Dùng
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/dashboard?tab=services" class="adm-tab-link ${activeTab == 'services' ? 'active' : ''}">
                        <i class="fa-solid fa-hand-holding-medical"></i>Quản Lý Dịch Vụ
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/dashboard?tab=settings" class="adm-tab-link ${activeTab == 'settings' ? 'active' : ''}">
                        <i class="fa-solid fa-sliders"></i>Cấu Hình Hệ Thống
                    </a>
                </div>
            </div>

            <%-- ── TAB PANELS CONTENT ── --%>
            <div class="tab-content" id="adminTabsContent">

                <%-- TAB 1: QUẢN LÝ NGƯỜI DÙNG --%>
                <c:if test="${activeTab == 'users' or empty activeTab}">
                    <div class="adm-panel animate-fade-in mb-4">
                        <div class="adm-panel-header flex-wrap gap-3">
                            <div class="adm-panel-title">
                                <i class="fa-solid fa-users text-cyan me-1"></i>Danh Sách Người Dùng
                                <span class="badge bg-secondary-subtle text-white border border-secondary px-2 py-1 rounded-pill ms-2" style="font-size: .75rem;">
                                    Trang ${currentPageUser} / ${totalPagesUser} (Tổng: ${totalUsersCount} người)
                                </span>
                                <span id="userFilterCount" class="badge bg-dark border border-secondary text-cyan px-2 py-1 rounded-pill ms-1" style="font-size: .75rem;"></span>
                            </div>
                            <%-- LIVE SEARCH & FILTER PILLS --%>
                            <div class="d-flex align-items-center gap-2 flex-wrap">
                                <div class="position-relative">
                                    <i class="fa-solid fa-magnifying-glass position-absolute top-50 start-0 translate-middle-y ms-3 text-white-50" style="font-size:.8rem; pointer-events:none;"></i>
                                    <input type="text" id="searchUser" class="adm-search-input" value="${searchUser}" placeholder="Tìm Tên, SĐT, Email..." onkeypress="if(event.key==='Enter') applyUserFilter('${roleUser}', '${statusUser}')" oninput="clientFilterUserTable()">
                                </div>
                                <div class="btn-group btn-group-sm" role="group" id="userRoleFilterGroup">
                                    <button type="button" class="btn btn-outline-secondary ${roleUser == 'ALL' or empty roleUser ? 'active text-white' : 'text-white-50'}" onclick="applyUserFilter('ALL', '${statusUser}')">Tất cả Role</button>
                                    <button type="button" class="btn btn-outline-secondary ${roleUser == 'PATIENT' ? 'active text-white' : 'text-white-50'}" onclick="applyUserFilter('PATIENT', '${statusUser}')">Patient</button>
                                    <button type="button" class="btn btn-outline-secondary ${roleUser == 'DOCTOR' ? 'active text-white' : 'text-white-50'}" onclick="applyUserFilter('DOCTOR', '${statusUser}')">Doctor</button>
                                    <button type="button" class="btn btn-outline-secondary ${roleUser == 'RECEPTIONIST' ? 'active text-white' : 'text-white-50'}" onclick="applyUserFilter('RECEPTIONIST', '${statusUser}')">Receptionist</button>
                                    <button type="button" class="btn btn-outline-secondary ${roleUser == 'ADMIN' ? 'active text-white' : 'text-white-50'}" onclick="applyUserFilter('ADMIN', '${statusUser}')">Admin</button>
                                </div>
                                <div class="btn-group btn-group-sm" role="group" id="userStatusFilterGroup">
                                    <button type="button" class="btn btn-outline-secondary ${statusUser == 'ALL' or empty statusUser ? 'active text-white' : 'text-white-50'}" onclick="applyUserFilter('${roleUser}', 'ALL')">Tất cả TT</button>
                                    <button type="button" class="btn btn-outline-success ${statusUser == 'ACTIVE' ? 'active text-white' : ''}" onclick="applyUserFilter('${roleUser}', 'ACTIVE')">Active</button>
                                    <button type="button" class="btn btn-outline-danger ${statusUser == 'BANNED' ? 'active text-white' : ''}" onclick="applyUserFilter('${roleUser}', 'BANNED')">Banned</button>
                                </div>
                            </div>
                        </div>
                        <div class="table-responsive">
                            <table class="adm-table" id="usersTable">
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
                                    <c:choose>
                                        <c:when test="${not empty usersList}">
                                            <c:forEach var="u" items="${usersList}">
                                                <tr id="user-row-${u.id}" data-role="${u.role}" data-status="${u.status ? 'ACTIVE' : 'BANNED'}">
                                                    <td style="padding-left:1.4rem;" class="fw-bold text-white-50">#${u.id}</td>
                                                    <td>
                                                        <div class="fw-bold text-white"><c:out value="${u.fullname}"/></div>
                                                    </td>
                                                    <td>
                                                        <div class="text-white-50 font-monospace" style="font-size: .85rem;"><c:out value="${u.username}"/></div>
                                                        <div class="text-muted" style="font-size:.78rem;"><c:out value="${u.email}"/></div>
                                                    </td>
                                                    <td><c:out value="${u.phone}"/></td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${u.id == sessionScope.LOGIN_USER.id}">
                                                                <span class="badge bg-danger-subtle text-danger border border-danger-subtle px-2.5 py-1 rounded-pill fw-bold" style="font-size: .78rem;" title="Bạn không thể tự đổi quyền tài khoản của chính mình">
                                                                    <i class="fa-solid fa-crown me-1"></i>ADMIN (Bạn)
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form id="roleForm_${u.id}" class="m-0" novalidate="true">
                                                                    <select name="role" data-current-role="${u.role}" class="form-select form-select-sm bg-dark text-white border-secondary rounded-pill" style="font-size:.78rem; width:auto; padding-right:2rem;" onchange="confirmChangeUserRole(this, ${u.id}, '${u.role}', '<c:out value="${u.fullname}"/>')">
                                                                        <option value="PATIENT" ${u.role == 'PATIENT' ? 'selected' : ''}>PATIENT</option>
                                                                        <option value="DOCTOR" ${u.role == 'DOCTOR' ? 'selected' : ''}>DOCTOR</option>
                                                                        <option value="RECEPTIONIST" ${u.role == 'RECEPTIONIST' ? 'selected' : ''}>RECEPTIONIST</option>
                                                                        <option value="ADMIN" ${u.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                                                                    </select>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
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
                                                            <button type="button" class="btn btn-sm ${u.status ? 'btn-outline-danger' : 'btn-outline-success'} rounded-pill px-3 fw-semibold" style="font-size:.78rem;" onclick="confirmToggleUserStatus(${u.id}, ${u.status}, '<c:out value="${u.fullname}"/>')">
                                                                <i class="fa-solid ${u.status ? 'fa-lock' : 'fa-unlock'} me-1"></i><span>${u.status ? 'Khóa' : 'Mở Khóa'}</span>
                                                            </button>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr class="no-result-row">
                                                <td colspan="7" class="text-center py-5 text-white-50">
                                                    <i class="fa-solid fa-magnifying-glass fs-3 mb-2 d-block text-cyan"></i>
                                                    Không tìm thấy người dùng nào phù hợp với bộ lọc.
                                                </td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>

                        <%-- USER PAGINATION BAR --%>
                        <c:if test="${totalPagesUser > 1}">
                            <div class="d-flex justify-content-center p-3 border-top border-secondary border-opacity-25">
                                <nav>
                                    <ul class="pagination pagination-sm m-0 gap-1">
                                        <c:forEach var="p" begin="1" end="${totalPagesUser}">
                                            <li class="page-item ${p == currentPageUser ? 'active' : ''}">
                                                <a class="page-link ${p == currentPageUser ? 'bg-info border-info text-white' : 'bg-dark text-white border-secondary'} rounded" href="${pageContext.request.contextPath}/admin/dashboard?pageUser=${p}&pageService=${currentPageService}&tab=users&searchUser=${searchUser}&roleUser=${roleUser}&statusUser=${statusUser}">${p}</a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </nav>
                            </div>
                        </c:if>
                    </div>
                </c:if>

                <%-- TAB 2: QUẢN LÝ DỊCH VỤ --%>
                <c:if test="${activeTab == 'services'}">
                    <div class="adm-panel animate-fade-in mb-4">
                        <div class="adm-panel-header flex-wrap gap-3">
                            <div class="adm-panel-title">
                                <i class="fa-solid fa-hand-holding-medical text-warning me-1"></i>Danh Mục Dịch Vụ
                                <span class="badge bg-secondary-subtle text-white border border-secondary px-2 py-1 rounded-pill ms-2" style="font-size: .75rem;">
                                    Trang ${currentPageService} / ${totalPagesService} (Tổng: ${totalServicesCount} dịch vụ)
                                </span>
                                <span id="serviceFilterCount" class="badge bg-dark border border-secondary text-warning px-2 py-1 rounded-pill ms-1" style="font-size: .75rem;"></span>
                            </div>
                            <div class="d-flex align-items-center gap-2 flex-wrap">
                                <%-- LIVE SEARCH SERVICE & STATUS FILTER --%>
                                <div class="position-relative">
                                    <i class="fa-solid fa-magnifying-glass position-absolute top-50 start-0 translate-middle-y ms-3 text-white-50" style="font-size:.8rem; pointer-events:none;"></i>
                                    <input type="text" id="searchService" class="adm-search-input" value="${searchService}" placeholder="Tìm tên dịch vụ, mô tả..." onkeypress="if(event.key==='Enter') applyServiceFilter('${statusService}')" oninput="clientFilterServiceTable()">
                                </div>
                                <div class="btn-group btn-group-sm" role="group" id="serviceStatusFilterGroup">
                                    <button type="button" class="btn btn-outline-secondary ${statusService == 'ALL' or empty statusService ? 'active text-white' : 'text-white-50'}" onclick="applyServiceFilter('ALL')">Tất cả</button>
                                    <button type="button" class="btn btn-outline-success ${statusService == 'ACTIVE' ? 'active text-white' : ''}" onclick="applyServiceFilter('ACTIVE')">Hiển Thị</button>
                                    <button type="button" class="btn btn-outline-secondary ${statusService == 'HIDDEN' ? 'active text-white' : ''}" onclick="applyServiceFilter('HIDDEN')">Đang Ẩn</button>
                                </div>
                                <button type="button" class="btn btn-warning btn-sm rounded-pill fw-bold px-3 d-flex align-items-center gap-1 shadow-sm" data-bs-toggle="modal" data-bs-target="#addServiceModal">
                                    <i class="fa-solid fa-plus"></i>Thêm Dịch Vụ Mới
                                </button>
                            </div>
                        </div>
                        <div class="table-responsive">
                            <table class="adm-table" id="servicesTable">
                                <thead>
                                    <tr>
                                        <th style="padding-left:1.4rem;">ID</th>
                                        <th>Tên Dịch Vụ</th>
                                        <th>Đơn Giá Niêm Yết</th>
                                        <th>Thời Gian Khám</th>
                                        <th>Trạng Thái</th>
                                        <th style="text-align:right; padding-right:1.4rem;">Thao Tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty servicesList}">
                                            <c:forEach var="s" items="${servicesList}">
                                                <tr id="service-row-${s.id}" data-status="${s.status ? 'ACTIVE' : 'HIDDEN'}">
                                                    <td style="padding-left:1.4rem;" class="fw-bold text-white-50">#${s.id}</td>
                                                    <td>
                                                        <div class="fw-bold text-white"><c:out value="${s.serviceName}"/></div>
                                                        <div class="text-muted" style="font-size:.78rem;"><c:out value="${s.description}"/></div>
                                                    </td>
                                                    <td class="fw-bold text-warning fs-6">
                                                        <fmt:formatNumber value="${s.price}" pattern="#,##0" maxFractionDigits="0" /> VNĐ
                                                    </td>
                                                    <td>
                                                        <span class="badge bg-dark border border-secondary text-cyan px-2 py-1 rounded-pill">
                                                            <i class="fa-regular fa-clock me-1"></i>${s.durationMinutes} phút
                                                        </span>
                                                    </td>
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
                                                        <button type="button" class="btn btn-sm ${s.status ? 'btn-outline-warning' : 'btn-outline-success'} rounded-pill px-3 fw-semibold" style="font-size:.78rem;" onclick="confirmToggleService(${s.id}, ${s.status}, '<c:out value="${s.serviceName}"/>')">
                                                            <i class="fa-solid ${s.status ? 'fa-eye-slash' : 'fa-eye'} me-1"></i><span>${s.status ? 'Ẩn Dịch Vụ' : 'Hiển Thị'}</span>
                                                        </button>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr class="no-result-row">
                                                <td colspan="6" class="text-center py-5 text-white-50">
                                                    <i class="fa-solid fa-hand-holding-medical fs-3 mb-2 d-block text-warning"></i>
                                                    Không tìm thấy dịch vụ nào phù hợp với bộ lọc.
                                                </td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>

                        <%-- SERVICE PAGINATION BAR --%>
                        <c:if test="${totalPagesService > 1}">
                            <div class="d-flex justify-content-center p-3 border-top border-secondary border-opacity-25">
                                <nav>
                                    <ul class="pagination pagination-sm m-0 gap-1">
                                        <c:forEach var="p" begin="1" end="${totalPagesService}">
                                            <li class="page-item ${p == currentPageService ? 'active' : ''}">
                                                <a class="page-link ${p == currentPageService ? 'bg-warning border-warning text-dark fw-bold' : 'bg-dark text-white border-secondary'} rounded" href="${pageContext.request.contextPath}/admin/dashboard?pageUser=${currentPageUser}&pageService=${p}&tab=services&searchService=${searchService}&statusService=${statusService}">${p}</a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </nav>
                            </div>
                        </c:if>
                    </div>
                </c:if>

                <%-- TAB 3: CẤU HÌNH HỆ THỐNG --%>
                <c:if test="${activeTab == 'settings'}">
                    <div class="adm-panel animate-fade-in mb-4">
                        <div class="adm-panel-header">
                            <div class="adm-panel-title">
                                <i class="fa-solid fa-sliders text-emerald me-1"></i>Cài Đặt Cấu Hình Phòng Khám &amp; Cổng Thanh Toán
                            </div>
                            <button type="button" class="btn btn-emerald btn-sm rounded-pill fw-bold px-4 shadow-sm" style="background:linear-gradient(135deg, #10b981, #0ea5e9); color:#fff; border:none;" onclick="confirmSaveSettings()">
                                <i class="fa-solid fa-floppy-disk me-1"></i>Lưu Tất Cả Cấu Hình
                            </button>
                        </div>
                        <div class="p-4">
                            <form id="settingsForm" novalidate="true">
                                <input type="hidden" name="action" value="update-settings">
                                <div class="row g-4">
                                    <div class="col-12 col-md-6">
                                        <div class="p-3 rounded-4 border border-secondary border-opacity-25" style="background: rgba(30, 41, 59, 0.3);">
                                            <h6 class="fw-bold text-cyan mb-3"><i class="fa-solid fa-hospital me-2"></i>Thông Tin Phòng Khám</h6>
                                            <div class="mb-3">
                                                <label class="form-label text-white-50 small fw-bold">Tên Phòng Khám</label>
                                                <input type="text" name="CLINIC_NAME" class="admin-glass-input" value="${settingsMap['CLINIC_NAME'] != null ? settingsMap['CLINIC_NAME'] : 'SZ Clinic & Spa'}" required>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label text-white-50 small fw-bold">Hotline Liên Hệ</label>
                                                <input type="text" name="CLINIC_HOTLINE" class="admin-glass-input" value="${settingsMap['CLINIC_HOTLINE'] != null ? settingsMap['CLINIC_HOTLINE'] : '1900 6868'}" required>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label text-white-50 small fw-bold">Email Hỗ Trợ</label>
                                                <input type="email" name="CLINIC_EMAIL" class="admin-glass-input" value="${settingsMap['CLINIC_EMAIL'] != null ? settingsMap['CLINIC_EMAIL'] : 'support@szclinic.com'}">
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label text-white-50 small fw-bold">Địa Chỉ Cơ Sở</label>
                                                <input type="text" name="CLINIC_ADDRESS" class="admin-glass-input" value="${settingsMap['CLINIC_ADDRESS'] != null ? settingsMap['CLINIC_ADDRESS'] : '123 Nguyễn Văn Cừ, Quận 5, TP.HCM'}">
                                            </div>
                                            <div class="mb-0">
                                                <label class="form-label text-white-50 small fw-bold">Giờ Mở Cửa (Chuẩn Display)</label>
                                                <input type="text" name="OPENING_HOURS" class="admin-glass-input" value="${settingsMap['OPENING_HOURS'] != null ? settingsMap['OPENING_HOURS'] : '08:00 - 20:00 (Thứ 2 - Thứ 7)'}">
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-12 col-md-6">
                                        <div class="p-3 rounded-4 border border-secondary border-opacity-25 h-100 d-flex flex-column justify-content-between" style="background: rgba(30, 41, 59, 0.3);">
                                            <div>
                                                <h6 class="fw-bold text-warning mb-3"><i class="fa-solid fa-qrcode me-2"></i>Cổng Thanh Toán SePay (VietQR Tự Động)</h6>
                                                <div class="mb-3">
                                                    <label class="form-label text-white-50 small fw-bold">Tên Ngân Hàng Thụ Hưởng (SePay / Napas Code)</label>
                                                    <input type="text" name="SEPAY_BANK_NAME" class="admin-glass-input" value="${settingsMap['SEPAY_BANK_NAME'] != null ? settingsMap['SEPAY_BANK_NAME'] : 'MBBank'}" placeholder="MBBank, VCB, ACB, TPB...">
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label text-white-50 small fw-bold">Số Tài Khoản Nhận Tiền</label>
                                                    <input type="text" name="SEPAY_BANK_ACC" class="admin-glass-input font-monospace" value="${settingsMap['SEPAY_BANK_ACC'] != null ? settingsMap['SEPAY_BANK_ACC'] : '0901234567'}" placeholder="0901234567...">
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label text-white-50 small fw-bold">Chủ Tài Khoản (Không Dấu / Viết Hoa)</label>
                                                    <input type="text" name="SEPAY_ACCOUNT_HOLDER" class="admin-glass-input" value="${settingsMap['SEPAY_ACCOUNT_HOLDER'] != null ? settingsMap['SEPAY_ACCOUNT_HOLDER'] : 'PHONG KHAM NHA KHOA VA DA LIEU SZ'}" placeholder="PHONG KHAM...">
                                                </div>
                                            </div>
                                            <div class="p-3 rounded-3 mt-2" style="background: rgba(14, 165, 233, 0.1); border: 1px dashed rgba(56, 189, 248, 0.3);">
                                                <div class="d-flex align-items-center gap-2 text-cyan fs-7">
                                                    <i class="fa-solid fa-bolt"></i>
                                                    <span>Mã QR SePay tự động lấy số tài khoản và ngân hàng từ mục cài đặt này để render VietQR động.</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <%-- QUẢN LÝ KHUNG GIỜ KHÁM TRỰC QUAN --%>
                                    <div class="col-12">
                                        <div class="p-3 rounded-4 border border-secondary border-opacity-25" style="background: rgba(30, 41, 59, 0.3);">
                                            <div class="d-flex justify-content-between align-items-center mb-2 flex-wrap gap-2">
                                                <h6 class="fw-bold text-emerald mb-0"><i class="fa-regular fa-clock me-2"></i>Quản Lý Khung Giờ Khám Toàn Hệ Thống (60 Phút / Ca)</h6>
                                                <div class="d-flex gap-2">
                                                    <button type="button" class="btn btn-outline-info btn-xs rounded-pill px-2 py-1 fs-8" onclick="selectAllSlots(true)">Chọn Tất Cả</button>
                                                    <button type="button" class="btn btn-outline-secondary btn-xs rounded-pill px-2 py-1 fs-8" onclick="selectAllSlots(false)">Bỏ Chọn</button>
                                                </div>
                                            </div>
                                            <p class="text-white-50 small mb-3">Nhấp vào từng ô giờ để BẬT / TẮT khung giờ phục vụ. Bác sĩ và Bệnh nhân sẽ tạo và đặt lịch dựa trên các khung giờ này.</p>
                                            
                                            <%-- Lưới chip chọn giờ --%>
                                            <div id="adminSlotChips" class="d-flex flex-wrap gap-2 mb-3"></div>

                                            <%-- Input ẩn chứa chuỗi cấu hình --%>
                                            <input type="hidden" name="CLINIC_TIME_SLOTS" id="clinicTimeSlotsInput" value="${settingsMap['CLINIC_TIME_SLOTS'] != null ? settingsMap['CLINIC_TIME_SLOTS'] : '08:00,09:00,10:00,11:00,13:00,14:00,15:00,16:00,17:00,18:00,19:00'}">
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:if>

            </div>
        </div>
    </div>

    <%-- MODAL: THÊM DỊCH VỤ MỚI --%>
    <div class="modal fade" id="addServiceModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content glass-card border border-secondary border-opacity-25" style="background: #0f172a; color: #fff;">
                <div class="modal-header border-bottom border-secondary border-opacity-25">
                    <h5 class="modal-title fw-bold text-warning"><i class="fa-solid fa-plus-circle me-2"></i>Thêm Dịch Vụ Mới</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/admin/dashboard" method="POST" novalidate="true">
                    <input type="hidden" name="action" value="add-service">
                    <input type="hidden" name="tab" value="services">
                    <div class="modal-body p-4">
                        <div class="mb-3">
                            <label class="form-label text-white-50 small fw-bold">Tên Dịch Vụ (*)</label>
                            <input type="text" name="serviceName" class="form-control bg-dark text-white border-secondary" required placeholder="Ví dụ: Lấy Cao Răng Sóng Siêu Âm...">
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-white-50 small fw-bold">Đơn Giá Niêm Yết (VNĐ) (*)</label>
                            <input type="number" step="1000" name="price" class="form-control bg-dark text-white border-secondary" required placeholder="Ví dụ: 350000">
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-white-50 small fw-bold">Thời Gian Thực Hiện (Phút)</label>
                            <input type="number" name="durationMinutes" value="60" class="form-control bg-dark text-white border-secondary" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-white-50 small fw-bold">Mô Tả Chi Tiết</label>
                            <textarea name="description" rows="3" class="form-control bg-dark text-white border-secondary" placeholder="Mô tả công nghệ, quy trình thực hiện..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer border-top border-secondary border-opacity-25">
                        <button type="button" class="btn btn-secondary rounded-pill px-4" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-warning rounded-pill px-4 fw-bold shadow-sm">
                            <i class="fa-solid fa-save me-1"></i>Lưu Dịch Vụ
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%-- BOOTSTRAP & DEDICATED ADMIN JS --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        window.ADMIN_CTX = '${pageContext.request.contextPath}';
        window.ADMIN_CURRENT_ROLE_USER = '${roleUser}';
        window.ADMIN_CURRENT_STATUS_USER = '${statusUser}';
        window.ADMIN_CURRENT_STATUS_SERVICE = '${statusService}';
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/admin.js" charset="UTF-8"></script>
</body>

</html>
