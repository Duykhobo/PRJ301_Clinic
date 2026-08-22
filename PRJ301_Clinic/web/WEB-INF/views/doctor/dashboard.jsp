<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <title>Bác Sĩ &amp; Khám Bệnh | <c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Phòng Khám & Spa')}"/></title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/doctor.css">
</head>

<body>
    <%-- 1. IMPORT SIDEBAR COMPONENT --%>
    <jsp:include page="/WEB-INF/views/components/sidebar-doctor.jsp" />

    <%-- 2. NỘI DUNG CHÍNH CỦA DASHBOARD --%>
    <div class="container-fluid px-2 px-sm-4 py-3 py-sm-4 flex-grow-1">

        <%-- HERO BANNER --%>
        <div class="doctor-hero mb-4 animate-fade-in">
            <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
                <div class="d-flex align-items-center gap-3">
                    <div class="hero-avatar">
                        <i class="fa-solid fa-stethoscope text-white"></i>
                    </div>
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <div class="live-dot"></div>
                            <span style="font-size:.75rem; color:#38bdf8; font-weight:700; letter-spacing:.06em; text-transform:uppercase;">DOCTOR CLINIC WORKSPACE</span>
                        </div>
                        <h4 class="fw-bold text-white mb-1" style="font-size:1.3rem;">
                            Xin chào, ${sessionScope.LOGIN_USER.fullname.startsWith('BS.') ? '' : 'BS. '}<c:out value="${sessionScope.LOGIN_USER.fullname}"/>
                        </h4>
                        <p class="mb-0" style="color:rgba(255,255,255,.6); font-size:.83rem;">
                            <i class="fa-solid fa-heart-pulse me-1 text-cyan"></i>Chuyên khoa Nha khoa &amp; Da liễu Y khoa &nbsp;·&nbsp;
                            <i class="fa-solid fa-calendar-day me-1 text-emerald"></i>${selectedDate}
                        </p>
                    </div>
                </div>

                <%-- DATE FILTER --%>
                <form action="${pageContext.request.contextPath}/doctor/dashboard" method="GET" class="filter-bar" novalidate="true">
                    <input type="hidden" name="tab" value="${not empty param.tab ? param.tab : 'appointments'}">
                    <div class="filter-wrap">
                        <i class="fa-solid fa-calendar-days fi text-cyan"></i>
                        <input type="text" name="date" class="filter-input flatpickr-date" value="${selectedDate}" placeholder="Chọn ngày" autocomplete="off">
                    </div>
                    <button type="submit" class="btn-filter">
                        <i class="fa-solid fa-filter me-1"></i>Lọc Ca
                    </button>
                </form>
            </div>
        </div>

        <c:choose>
            <c:when test="${param.tab == 'packages'}">
                <%-- DOCTOR TREATMENT PACKAGES MANAGEMENT PANEL --%>
                <div class="panel mb-4 animate-fade-in">
                    <div class="panel-header d-flex align-items-center justify-content-between flex-wrap gap-2">
                        <div class="panel-title text-warning">
                            <i class="fa-solid fa-wand-magic-sparkles text-warning me-2"></i>Quản Lý Gói Liệu Trình Trọn Gói Spa &amp; Thẩm Mỹ (${allPackages.size()} Gói)
                        </div>
                        <button type="button" class="btn btn-sm btn-warning rounded-pill px-3 fw-bold text-dark" data-bs-toggle="modal" data-bs-target="#createPackageModal">
                            <i class="fa-solid fa-plus me-1"></i>Tạo Gói Liệu Trình Mới
                        </button>
                    </div>
                    <div class="table-responsive p-3">
                        <table class="table table-dark table-hover align-middle mb-0">
                            <thead>
                                <tr class="text-warning border-bottom border-secondary border-opacity-25">
                                    <th>Mã Gói</th>
                                    <th>Bệnh Nhân</th>
                                    <th>Tên Gói Liệu Trình</th>
                                    <th>Dịch Vụ Áp Dụng</th>
                                    <th>Tiến Độ Buổi</th>
                                    <th>Trạng Thái</th>
                                    <th class="text-end">Thao Tác Bác Sĩ</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty allPackages}">
                                        <c:forEach var="pkg" items="${allPackages}">
                                            <tr>
                                                <td class="fw-bold text-white">#${pkg.id}</td>
                                                <td>
                                                    <div class="fw-bold text-white"><c:out value="${pkg.patientName}"/></div>
                                                    <div class="small text-muted"><i class="fa-solid fa-phone me-1"></i><c:out value="${pkg.patientPhone}"/></div>
                                                </td>
                                                <td class="text-warning fw-bold"><c:out value="${pkg.packageName}"/></td>
                                                <td><span class="badge bg-dark border border-secondary text-cyan px-2.5 py-1 rounded-pill"><c:out value="${pkg.serviceName}"/></span></td>
                                                <td style="min-width: 180px;">
                                                    <div class="d-flex justify-content-between small text-white-50 mb-1">
                                                        <span><strong>${pkg.completedSessions}</strong> / ${pkg.totalSessions} buổi</span>
                                                        <strong class="text-cyan">${pkg.progressPercent}%</strong>
                                                    </div>
                                                    <div class="progress" style="height: 6px; background: rgba(255,255,255,0.1); border-radius: 10px;">
                                                        <div class="progress-bar bg-warning progress-bar-striped" role="progressbar" style="width: ${pkg.progressPercent}%;"></div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${pkg.status == 'COMPLETED' || pkg.completedSessions >= pkg.totalSessions}">
                                                            <span class="badge bg-success bg-opacity-25 border border-success border-opacity-40 text-emerald px-2.5 py-1 rounded-pill fw-bold">
                                                                <i class="fa-solid fa-circle-check me-1"></i>Hoàn Thành
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-warning bg-opacity-25 border border-warning border-opacity-40 text-warning px-2.5 py-1 rounded-pill fw-bold">
                                                                <i class="fa-solid fa-spinner fa-spin-pulse me-1"></i>Đang Điều Trị
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-end">
                                                    <c:if test="${pkg.status != 'COMPLETED' && pkg.completedSessions < pkg.totalSessions}">
                                                        <form action="${pageContext.request.contextPath}/doctor/dashboard" method="POST" class="d-inline" novalidate="true">
                                                            <input type="hidden" name="action" value="increment-package-session">
                                                            <input type="hidden" name="packageId" value="${pkg.id}">
                                                            <button type="submit" class="btn btn-sm btn-outline-warning rounded-pill px-3 fw-bold">
                                                                <i class="fa-solid fa-plus me-1"></i>+1 Buổi Khám
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="7" class="text-center py-4 text-muted">
                                                <i class="fa-solid fa-wand-magic-sparkles fs-3 mb-2 d-block text-secondary"></i>
                                                Chưa có gói liệu trình nào được đăng ký.
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:when>
            <c:when test="${param.tab == 'schedules'}">
                <%-- DOCTOR SCHEDULE MANAGEMENT PANEL (100% PURE AJAX ZERO-RELOAD) --%>
                <div class="panel mb-4 animate-fade-in">
                    <div class="panel-header d-flex align-items-center justify-content-between flex-wrap gap-2">
                        <div class="panel-title">
                            <i class="fa-solid fa-clock-rotate-left text-cyan me-2"></i>Quản Lý &amp; Đăng Ký Khung Giờ Làm Việc Y Tế (<span id="scheduleDateLabel">${not empty selectedDate ? selectedDate : 'Hôm Nay'}</span>)
                        </div>
                        <div class="d-flex gap-2 flex-wrap">
                            <button type="button" class="btn btn-sm btn-outline-warning rounded-pill px-3 fw-bold" data-bs-toggle="modal" data-bs-target="#weeklyScheduleModal">
                                <i class="fa-solid fa-calendar-week me-1"></i>Đăng Ký Lịch Theo Tuần
                            </button>
                            <button type="button" class="btn btn-sm btn-outline-info rounded-pill px-3" onclick="generateScheduleAjax()">
                                <i class="fa-solid fa-wand-magic-sparkles me-1"></i>Sinh Ca Ngày Này
                            </button>
                            <button type="button" class="btn btn-sm btn-primary-gradient rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#addSlotModal">
                                <i class="fa-solid fa-plus me-1"></i>Thêm Ca Lẻ
                            </button>
                        </div>
                    </div>
                    <div class="p-3" id="doctorSlotsContainer">
                        <c:choose>
                            <c:when test="${not empty doctorSchedules}">
                                <div class="row g-3">
                                    <c:forEach var="slot" items="${doctorSchedules}">
                                        <div class="col-12 col-sm-6 col-md-4 col-xl-3">
                                            <div class="p-3 rounded-3 border d-flex align-items-center justify-content-between gap-2 ${slot.isAvailable ? 'border-cyan bg-cyan bg-opacity-10' : 'border-secondary bg-dark text-muted'} shadow-sm" style="transition:all 0.2s;">
                                                <div class="d-flex align-items-center gap-2">
                                                    <i class="fa-solid fa-clock fs-5 ${slot.isAvailable ? 'text-cyan' : 'text-muted'}"></i>
                                                    <div>
                                                        <div class="fw-bold fs-7 ${slot.isAvailable ? 'text-white' : 'text-muted'}">${slot.startTime} - ${slot.endTime}</div>
                                                        <div class="fs-8 ${slot.isAvailable ? 'text-emerald fw-semibold' : 'text-danger'}">
                                                            ${slot.isAvailable ? '🟢 Khả dụng' : '🔴 Khóa / Đã đặt'}
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="d-flex gap-1">
                                                    <button type="button" class="btn btn-xs ${slot.isAvailable ? 'btn-outline-warning' : 'btn-outline-success'} p-1 rounded-circle" title="${slot.isAvailable ? 'Khóa ca' : 'Mở ca'}" onclick="confirmToggleSlot(${slot.id}, ${!slot.isAvailable}, '${slot.startTime} - ${slot.endTime}')">
                                                        <i class="fa-solid ${slot.isAvailable ? 'fa-lock' : 'fa-unlock'}"></i>
                                                    </button>
                                                    <button type="button" class="btn btn-xs btn-outline-danger p-1 rounded-circle" title="Xóa ca này" onclick="deleteSlotAjax(${slot.id})">
                                                        <i class="fa-solid fa-trash"></i>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-3 text-muted">
                                    <i class="fa-solid fa-calendar-xmark me-2"></i>Bác sĩ chưa có khung giờ làm việc nào cho ngày <strong>${selectedDate}</strong>.
                                    <button type="button" class="btn btn-sm btn-outline-info rounded-pill px-3 py-1 ms-2" onclick="generateScheduleAjax()">
                                        <i class="fa-solid fa-wand-magic-sparkles me-1"></i>Tự Động Tạo Tất Cả Ca Khám
                                    </button>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <%-- STAT CARDS & DANH SÁCH CA KHÁM BỆNH --%>
                <div class="row g-3 mb-4 animate-fade-in">
                    <div class="col-12 col-sm-6 col-lg-4">
                        <div class="stat-card blue h-100">
                            <div class="stat-icon"><i class="fa-solid fa-list-check"></i></div>
                            <div>
                                <div class="stat-value">${not empty totalCount ? totalCount : '0'}</div>
                                <div class="stat-label">Tổng ca khám</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-sm-6 col-lg-4">
                        <div class="stat-card amber h-100">
                            <div class="stat-icon"><i class="fa-solid fa-hourglass-half"></i></div>
                            <div>
                                <div class="stat-value">${not empty pendingCount ? pendingCount : '0'}</div>
                                <div class="stat-label">Bệnh nhân chờ</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-sm-6 col-lg-4">
                        <div class="stat-card green h-100">
                            <div class="stat-icon"><i class="fa-solid fa-circle-check"></i></div>
                            <div>
                                <div class="stat-value">${not empty completedCount ? completedCount : '0'}</div>
                                <div class="stat-label">Đã hoàn tất</div>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- APPOINTMENTS TABLE --%>
                <div class="panel animate-fade-in mb-4">
                    <div class="panel-header flex-wrap gap-3">
                        <div class="panel-title text-cyan">
                            <i class="fa-solid fa-bed-pulse text-cyan"></i>
                            Lịch Khám Y Tế Ngày <span style="color:#38bdf8; margin-left:.4rem;">${selectedDate}</span>
                            <span id="doctorFilterCount" class="badge bg-dark border border-secondary text-cyan px-2 py-1 rounded-pill ms-2" style="font-size: .75rem;"></span>
                        </div>
                        <div class="d-flex gap-2 align-items-center flex-wrap">
                            <%-- LIVE SEARCH INPUT --%>
                            <div class="position-relative">
                                <i class="fa-solid fa-magnifying-glass position-absolute top-50 start-0 translate-middle-y ms-3 text-white-50" style="font-size:.8rem; pointer-events:none;"></i>
                                <input type="text" id="searchDoctorApp" class="adm-search-input" placeholder="Tìm Tên, SĐT, Dịch vụ..." oninput="filterDoctorAppointments()">
                            </div>
                            <%-- QUICK FILTER PILLS --%>
                            <div class="btn-group btn-group-sm" role="group" id="doctorStatusFilterGroup">
                                <button type="button" class="btn btn-outline-secondary active text-white" onclick="setDoctorAppFilter('ALL', this)">Tất cả</button>
                                <button type="button" class="btn btn-outline-warning" onclick="setDoctorAppFilter('PENDING', this)">Chờ Khám</button>
                                <button type="button" class="btn btn-outline-success" onclick="setDoctorAppFilter('COMPLETED', this)">Đã Khám</button>
                                <button type="button" class="btn btn-outline-danger" onclick="setDoctorAppFilter('CANCELLED', this)">Đã Hủy</button>
                            </div>
                        </div>
                    </div>

            <div style="overflow-x:auto;">
                <table class="tbl" id="doctorAppTable">
                    <thead>
                        <tr>
                            <th style="padding-left:1.4rem;">Giờ Khám</th>
                            <th>Bệnh Nhân</th>
                            <th>Dịch Vụ Khám</th>
                            <th>Thanh Toán</th>
                            <th>Trạng Thái</th>
                            <th style="text-align:right; padding-right:1.4rem;">Thao Tác Bác Sĩ</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty appointments}">
                                <c:forEach var="app" items="${appointments}">
                                    <tr data-status="${app.status}" data-payment="${app.paymentStatus}">
                                        <td style="padding-left:1.4rem;">
                                            <span class="time-bubble">
                                                <i class="fa-solid fa-clock me-1"></i>${app.startTime}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="patient-name"><c:out value="${app.patientName}"/></div>
                                            <div class="patient-phone"><i class="fa-solid fa-phone" style="font-size:.7rem;"></i> <c:out value="${app.patientPhone}"/></div>
                                        </td>
                                        <td>
                                            <span class="service-chip"><c:out value="${app.serviceName}"/></span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${app.paymentStatus == 'PAID'}">
                                                    <span class="badge-paid"><i class="fa-solid fa-check-circle me-1"></i>Đã Thanh Toán</span>
                                                </c:when>
                                                <c:when test="${app.status == 'CANCELLED'}">
                                                    <span class="badge-unpaid" style="background: rgba(148, 163, 184, 0.15); color: #94a3b8; border-color: rgba(148, 163, 184, 0.3);"><i class="fa-solid fa-ban me-1"></i>Ca Đã Hủy</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-unpaid"><i class="fa-solid fa-hourglass-half me-1"></i>Chưa Thu</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${app.status == 'COMPLETED'}">
                                                    <span class="badge-completed"><i class="fa-solid fa-circle-check me-1"></i>Khám Xong</span>
                                                </c:when>
                                                <c:when test="${app.status == 'CONFIRMED'}">
                                                    <span class="badge-confirmed"><i class="fa-solid fa-user-check me-1"></i>Đã Tiếp Nhận</span>
                                                </c:when>
                                                <c:when test="${app.status == 'CANCELLED'}">
                                                    <span class="badge-cancelled"><i class="fa-solid fa-ban me-1"></i>Đã Hủy</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-pending"><i class="fa-solid fa-spinner fa-spin me-1"></i>Chờ Khám</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="text-align:right; padding-right:1.4rem;">
                                            <c:choose>
                                                <c:when test="${app.status == 'CANCELLED'}">
                                                    <button type="button" class="btn btn-sm btn-outline-secondary disabled rounded-pill" style="opacity: 0.5; cursor: not-allowed;" disabled>
                                                        <i class="fa-solid fa-ban me-1"></i>Đã Hủy Ca
                                                    </button>
                                                </c:when>
                                                <c:when test="${app.status == 'COMPLETED'}">
                                                    <button type="button" class="btn-examine rounded-pill" style="background: linear-gradient(135deg, #059669 0%, #10b981 100%);"
                                                        onclick="openMedicalModal(${app.id}, '${app.patientName}', '${app.serviceName}', '${recordsMap[app.id].diagnosis}', '${recordsMap[app.id].prescriptionOrResult}', ${not empty recordsMap[app.id].skinMoistureLevel ? recordsMap[app.id].skinMoistureLevel : 'null'}, ${not empty recordsMap[app.id].skinSebumLevel ? recordsMap[app.id].skinSebumLevel : 'null'})">
                                                        <i class="fa-solid fa-file-medical me-1"></i>Xem Bệnh Án
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="button" class="btn-examine rounded-pill"
                                                        onclick="openMedicalModal(${app.id}, '${app.patientName}', '${app.serviceName}', '${recordsMap[app.id].diagnosis}', '${recordsMap[app.id].prescriptionOrResult}', ${not empty recordsMap[app.id].skinMoistureLevel ? recordsMap[app.id].skinMoistureLevel : 'null'}, ${not empty recordsMap[app.id].skinSebumLevel ? recordsMap[app.id].skinSebumLevel : 'null'})">
                                                        <i class="fa-solid fa-notes-medical me-1"></i>Khám & Kê Đơn
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6">
                                        <div class="empty-state">
                                            <i class="fa-solid fa-bed-pulse text-cyan"></i>
                                            <p style="font-size:1rem; font-weight:600; color:rgba(255,255,255,.4);">
                                                Không có ca khám nào trong ngày ${selectedDate}</p>
                                        </div>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <%-- DOCTOR PAGINATION BAR --%>
            <c:if test="${totalPages > 1}">
                <div class="d-flex justify-content-center p-3 border-top border-secondary opacity-75">
                    <nav>
                        <ul class="pagination pagination-sm m-0">
                            <c:forEach var="p" begin="1" end="${totalPages}">
                                <li class="page-item ${p == currentPage ? 'active' : ''}">
                                    <a class="page-link bg-dark text-white border-secondary" href="${pageContext.request.contextPath}/doctor/dashboard?date=${selectedDate}&page=${p}">${p}</a>
                                </li>
                            </c:forEach>
                        </ul>
                    </nav>
                </div>
            </c:if>
        </div>
        </c:otherwise>
        </c:choose>

        <%-- MEDICAL MODAL --%>
        <div class="modal fade" id="medicalModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg">
                <div class="modal-content glass-card border border-cyan border-opacity-30 text-white">
                    <div class="modal-header border-bottom border-secondary border-opacity-25">
                        <h5 class="modal-title fw-bold text-cyan d-flex align-items-center gap-2">
                            <i class="fa-solid fa-stethoscope text-cyan"></i> Hồ Sơ Chẩn Đoán & Đơn Thuốc Y Khoa
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form action="${pageContext.request.contextPath}/doctor/dashboard" method="POST" novalidate="true">
                        <input type="hidden" name="action" value="save-diagnosis">
                        <input type="hidden" name="appointmentId" id="modalAppointmentId">
                        <input type="hidden" name="date" value="${selectedDate}">

                        <div class="modal-body p-4">
                            <div class="p-3 rounded-3 mb-4" style="background: rgba(14, 165, 233, 0.12); border: 1px solid rgba(56, 189, 248, 0.25);">
                                <div class="row g-2">
                                    <div class="col-md-6">
                                        <span class="text-muted small">Bệnh nhân:</span>
                                        <span class="fw-bold text-white ms-2" id="modalPatientName"></span>
                                    </div>
                                    <div class="col-md-6">
                                        <span class="text-muted small">Dịch vụ:</span>
                                        <span class="fw-bold text-cyan ms-2" id="modalServiceName"></span>
                                    </div>
                                </div>
                            </div>

                            <%-- SPA & CLINIC: PHIẾU ĐÁNH GIÁ CHỈ SỐ DA & PHÁC ĐỒ TRỊ LIỆU --%>
                            <div class="p-3 rounded-3 mb-4" style="background: rgba(255, 255, 255, 0.03); border: 1px dashed rgba(56, 189, 248, 0.35);">
                                <h6 class="text-warning fw-bold mb-3 d-flex align-items-center gap-2">
                                    <i class="fa-solid fa-wand-magic-sparkles text-warning"></i> Phiếu Đo Chỉ Số Da &amp; Tình Trạng Khám (Dermatology Assessment)
                                </h6>
                                <div class="row g-3">
                                    <div class="col-12 col-md-4">
                                        <div class="p-2 rounded-2 bg-dark bg-opacity-50 border border-secondary border-opacity-30">
                                            <div class="d-flex justify-content-between small text-cyan fw-bold mb-1">
                                                <span><i class="fa-solid fa-droplet me-1"></i>Độ Ẩm Da:</span>
                                                <span id="moistureVal" class="text-white">55%</span>
                                            </div>
                                            <input type="range" class="form-range" name="skinMoistureLevel" id="moistureSlider" min="10" max="100" value="55" oninput="document.getElementById('moistureVal').innerText = this.value + '%'">
                                        </div>
                                    </div>
                                    <div class="col-12 col-md-4">
                                        <div class="p-2 rounded-2 bg-dark bg-opacity-50 border border-secondary border-opacity-30">
                                            <div class="d-flex justify-content-between small text-warning fw-bold mb-1">
                                                <span><i class="fa-solid fa-fire-flame-simple me-1"></i>Độ Tiết Dầu:</span>
                                                <span id="oilVal" class="text-white">60%</span>
                                            </div>
                                            <input type="range" class="form-range" name="skinSebumLevel" id="oilSlider" min="10" max="100" value="60" oninput="document.getElementById('oilVal').innerText = this.value + '%'">
                                        </div>
                                    </div>
                                    <div class="col-12 col-md-4">
                                        <div class="p-2 rounded-2 bg-dark bg-opacity-50 border border-secondary border-opacity-30">
                                            <div class="d-flex justify-content-between small text-emerald fw-bold mb-1">
                                                <span><i class="fa-solid fa-dna me-1"></i>Sắc Tố / Nhạy Cảm:</span>
                                            </div>
                                            <select id="pigmentationSelect" class="form-select form-select-sm bg-dark text-white border-secondary">
                                                <option value="Level 1 - Nhẹ / Khỏe">Level 1 - Nhẹ / Khỏe</option>
                                                <option value="Level 2 - Trung Bình / Thâm">Level 2 - Trung Bình / Thâm</option>
                                                <option value="Level 3 - Nặng / Nhạy Cảm">Level 3 - Nặng / Nhạy Cảm</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label class="form-label fw-bold text-cyan">
                                    <i class="fa-solid fa-heart-pulse me-1"></i>Chẩn Đoán Bệnh Lý / Tình Trạng Da:
                                </label>
                                <textarea name="diagnosis" id="modalDiagnosis" class="form-control form-control-glass" rows="3" placeholder="Nhập chi tiết chẩn đoán y khoa hoặc tình trạng da..." required></textarea>
                            </div>

                            <div class="mb-4">
                                <label class="form-label fw-bold text-warning">
                                    <i class="fa-solid fa-pills me-1"></i>Chỉ Định, Phác Đồ Trị Liệu &amp; Đơn Thuốc / Mỹ Phẩm:
                                </label>
                                <textarea name="prescription" id="modalPrescription" class="form-control form-control-glass text-warning" rows="4" placeholder="Nhập phác đồ điều trị, đơn thuốc và lời dặn chăm sóc tại nhà..." required></textarea>
                            </div>

                            <div class="mb-4 p-3 rounded-3" style="background: rgba(245, 158, 11, 0.08); border: 1px dashed rgba(245, 158, 11, 0.35);">
                                <label class="form-label fw-bold text-warning mb-2 d-flex align-items-center gap-2">
                                    <i class="fa-solid fa-wand-magic-sparkles text-warning"></i> Quản Lý Gói Liệu Trình Trọn Gói (Spa Treatment Package):
                                </label>
                                <select name="treatmentPackageOption" class="form-select bg-dark text-white border-warning border-opacity-50">
                                    <option value="none">Khám lẻ thông thường (Không liên kết gói)</option>
                                    <option value="advance_1" selected>☑ Ghi nhận hoàn tất 1 buổi của Gói Liệu Trình hiện tại</option>
                                    <option value="create_5">✨ Khởi tạo Gói Liệu Trình Mới (5 Buổi) cho Bệnh nhân</option>
                                    <option value="create_10">✨ Khởi tạo Gói Liệu Trình Mới (10 Buổi) cho Bệnh nhân</option>
                                </select>
                                <div class="form-text text-muted small mt-1">
                                    <i class="fa-solid fa-circle-info text-cyan me-1"></i>Hệ thống sẽ tự động tăng số buổi và gửi Email cập nhật tiến độ cho khách hàng.
                                </div>
                            </div>

                            <div class="mb-2 p-3 rounded-3" style="background: rgba(16, 185, 129, 0.08); border: 1px dashed rgba(52, 211, 153, 0.35);">
                                <label class="form-label fw-bold text-emerald mb-1">
                                    <i class="fa-solid fa-calendar-check me-1"></i>Chỉ Định Ngày Hẹn Tái Khám (Tùy chọn):
                                </label>
                                <input type="date" name="revisitDate" id="modalRevisitDate" class="form-control form-control-glass text-white border-emerald" style="border-color: rgba(52, 211, 153, 0.4) !important;">
                                <div class="form-text text-muted small mt-1">
                                    <i class="fa-solid fa-bell text-warning me-1"></i>Nếu chọn ngày tái khám, hệ thống sẽ tự động gửi <strong>Email &amp; Notification</strong> nhắc nhở bệnh nhân.
                                </div>
                            </div>
                        </div>

                        <div class="modal-footer border-top border-secondary border-opacity-25">
                            <button type="button" class="btn text-white-50" data-bs-dismiss="modal">Hủy Bỏ</button>
                            <button type="submit" class="btn btn-primary-gradient px-4 rounded-pill">
                                <i class="fa-solid fa-floppy-disk me-1"></i>Lưu Hồ Sơ Khám &amp; Gửi Thông Báo
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%-- MODAL THÊM CA KHÁM MỚI FOR DOCTOR --%>
        <div class="modal fade" id="addSlotModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-sm">
                <div class="modal-content bg-dark text-white border-secondary">
                    <div class="modal-header border-secondary">
                        <h6 class="modal-title fw-bold text-cyan"><i class="fa-solid fa-clock me-2"></i>Đăng Ký Ca Khám Mới</h6>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form id="addSlotForm" onsubmit="addSlotAjax(event)" novalidate="true">
                        <input type="hidden" name="action" value="add-slot">
                        <input type="hidden" name="ajax" value="true">
                        <input type="hidden" name="date" id="addSlotDate" value="${selectedDate}">
                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label text-white-50 small">Giờ Bắt Đầu (VD: 08:00)</label>
                                <input type="time" name="startTime" class="form-control bg-dark text-white border-secondary" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label text-white-50 small">Giờ Kết Thúc (VD: 09:00)</label>
                                <input type="time" name="endTime" class="form-control bg-dark text-white border-secondary" required>
                            </div>
                        </div>
                        <div class="modal-footer border-secondary">
                            <button type="button" class="btn btn-sm btn-secondary rounded-pill" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-sm btn-primary-gradient rounded-pill">Đăng Ký Ca</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%-- MODAL ĐĂNG KÝ LỊCH KHÁM THEO TUẦN (WEEKLY BATCH SCHEDULE MODAL) --%>
        <div class="modal fade" id="weeklyScheduleModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg">
                <div class="modal-content glass-card border border-warning border-opacity-40 text-white" style="background: rgba(15, 23, 42, 0.95); backdrop-filter: blur(16px);">
                    <div class="modal-header border-bottom border-secondary border-opacity-25">
                        <h5 class="modal-title fw-bold text-warning d-flex align-items-center gap-2">
                            <i class="fa-solid fa-calendar-week text-warning"></i> Đăng Ký Lịch Làm Việc Theo Tuần (Batch Processing)
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form id="weeklyScheduleForm" onsubmit="submitWeeklyScheduleAjax(event)" novalidate="true">
                        <input type="hidden" name="action" value="register-weekly-schedule">
                        <input type="hidden" name="ajax" value="true">

                        <div class="modal-body p-4">
                            <%-- PRESET BUTTONS --%>
                            <div class="mb-3 d-flex align-items-center justify-content-between flex-wrap gap-2">
                                <label class="form-label text-cyan fw-bold mb-0">
                                    <i class="fa-solid fa-clock me-1"></i>1. Chọn Khoảng Thời Gian Áp Dụng:
                                </label>
                                <div class="btn-group btn-group-sm">
                                    <button type="button" class="btn btn-outline-info" onclick="setWeeklyPreset('this_week')">Tuần Này</button>
                                    <button type="button" class="btn btn-outline-warning active" onclick="setWeeklyPreset('next_week')">Tuần Kế Tiếp</button>
                                    <button type="button" class="btn btn-outline-secondary" onclick="setWeeklyPreset('next_2_weeks')">2 Tuần Tới</button>
                                </div>
                            </div>

                            <div class="row g-3 mb-4">
                                <div class="col-12 col-md-6">
                                    <label class="form-label text-white-50 small">Từ Ngày (Start Date):</label>
                                    <input type="date" name="startDate" id="weeklyStartDate" class="form-control bg-dark text-white border-secondary" required onchange="calculateWeeklyEstimate()">
                                </div>
                                <div class="col-12 col-md-6">
                                    <label class="form-label text-white-50 small">Đến Ngày (End Date - Tối đa 90 ngày):</label>
                                    <input type="date" name="endDate" id="weeklyEndDate" class="form-control bg-dark text-white border-secondary" required onchange="calculateWeeklyEstimate()">
                                </div>
                            </div>

                            <%-- CHỌN THỨ TRONG TUẦN --%>
                            <div class="mb-3 d-flex align-items-center justify-content-between flex-wrap gap-2">
                                <label class="form-label text-cyan fw-bold mb-0">
                                    <i class="fa-solid fa-calendar-check me-1"></i>2. Chọn Các Ngày Làm Việc Trong Tuần:
                                </label>
                                <div class="btn-group btn-group-sm">
                                    <button type="button" class="btn btn-xs btn-outline-light px-2" onclick="selectWeekdayOnly()">Thứ 2 - Thứ 6</button>
                                    <button type="button" class="btn btn-xs btn-outline-light px-2" onclick="toggleAllDays(true)">Tất Cả 7 Ngày</button>
                                    <button type="button" class="btn btn-xs btn-outline-secondary px-2" onclick="toggleAllDays(false)">Bỏ Chọn</button>
                                </div>
                            </div>

                            <div class="d-flex flex-wrap gap-2 mb-4 p-3 rounded-3" style="background: rgba(255, 255, 255, 0.04); border: 1px dashed rgba(56, 189, 248, 0.25);">
                                <label class="btn btn-sm btn-outline-info rounded-pill px-3 day-chip">
                                    <input type="checkbox" name="daysOfWeek" value="1" class="d-none" checked onchange="handleDayChipChange(this)">
                                    <span>Thứ 2</span>
                                </label>
                                <label class="btn btn-sm btn-outline-info rounded-pill px-3 day-chip">
                                    <input type="checkbox" name="daysOfWeek" value="2" class="d-none" checked onchange="handleDayChipChange(this)">
                                    <span>Thứ 3</span>
                                </label>
                                <label class="btn btn-sm btn-outline-info rounded-pill px-3 day-chip">
                                    <input type="checkbox" name="daysOfWeek" value="3" class="d-none" checked onchange="handleDayChipChange(this)">
                                    <span>Thứ 4</span>
                                </label>
                                <label class="btn btn-sm btn-outline-info rounded-pill px-3 day-chip">
                                    <input type="checkbox" name="daysOfWeek" value="4" class="d-none" checked onchange="handleDayChipChange(this)">
                                    <span>Thứ 5</span>
                                </label>
                                <label class="btn btn-sm btn-outline-info rounded-pill px-3 day-chip">
                                    <input type="checkbox" name="daysOfWeek" value="5" class="d-none" checked onchange="handleDayChipChange(this)">
                                    <span>Thứ 6</span>
                                </label>
                                <label class="btn btn-sm btn-outline-warning rounded-pill px-3 day-chip">
                                    <input type="checkbox" name="daysOfWeek" value="6" class="d-none" checked onchange="handleDayChipChange(this)">
                                    <span>Thứ 7</span>
                                </label>
                                <label class="btn btn-sm btn-outline-secondary rounded-pill px-3 day-chip">
                                    <input type="checkbox" name="daysOfWeek" value="7" class="d-none" onchange="handleDayChipChange(this)">
                                    <span>Chủ Nhật</span>
                                </label>
                            </div>

                            <%-- CHỌN KHUNG GIỜ LÀM VIỆC --%>
                            <div class="mb-3 d-flex align-items-center justify-content-between flex-wrap gap-2">
                                <label class="form-label text-warning fw-bold mb-0">
                                    <i class="fa-solid fa-business-time me-1"></i>3. Chọn Khung Giờ Khám Áp Dụng:
                                </label>
                                <div class="btn-group btn-group-sm">
                                    <button type="button" class="btn btn-xs btn-outline-light px-2" onclick="selectMorningSlots()">Ca Sáng (08-11h)</button>
                                    <button type="button" class="btn btn-xs btn-outline-light px-2" onclick="selectAfternoonSlots()">Ca Chiều (14-17h)</button>
                                    <button type="button" class="btn btn-xs btn-outline-warning px-2" onclick="selectAllSlots(true)">Cả Ngày</button>
                                </div>
                            </div>

                            <div class="d-flex flex-wrap gap-2 mb-4 p-3 rounded-3" style="background: rgba(255, 255, 255, 0.04); border: 1px dashed rgba(245, 158, 11, 0.3);">
                                <label class="btn btn-sm btn-outline-warning rounded-pill px-3 slot-chip">
                                    <input type="checkbox" name="timeSlots" value="08:00" class="d-none" checked onchange="handleSlotChipChange(this)">
                                    <span>08:00</span>
                                </label>
                                <label class="btn btn-sm btn-outline-warning rounded-pill px-3 slot-chip">
                                    <input type="checkbox" name="timeSlots" value="09:00" class="d-none" checked onchange="handleSlotChipChange(this)">
                                    <span>09:00</span>
                                </label>
                                <label class="btn btn-sm btn-outline-warning rounded-pill px-3 slot-chip">
                                    <input type="checkbox" name="timeSlots" value="10:00" class="d-none" checked onchange="handleSlotChipChange(this)">
                                    <span>10:00</span>
                                </label>
                                <label class="btn btn-sm btn-outline-warning rounded-pill px-3 slot-chip">
                                    <input type="checkbox" name="timeSlots" value="11:00" class="d-none" checked onchange="handleSlotChipChange(this)">
                                    <span>11:00</span>
                                </label>
                                <label class="btn btn-sm btn-outline-warning rounded-pill px-3 slot-chip">
                                    <input type="checkbox" name="timeSlots" value="14:00" class="d-none" checked onchange="handleSlotChipChange(this)">
                                    <span>14:00</span>
                                </label>
                                <label class="btn btn-sm btn-outline-warning rounded-pill px-3 slot-chip">
                                    <input type="checkbox" name="timeSlots" value="15:00" class="d-none" checked onchange="handleSlotChipChange(this)">
                                    <span>15:00</span>
                                </label>
                                <label class="btn btn-sm btn-outline-warning rounded-pill px-3 slot-chip">
                                    <input type="checkbox" name="timeSlots" value="16:00" class="d-none" checked onchange="handleSlotChipChange(this)">
                                    <span>16:00</span>
                                </label>
                                <label class="btn btn-sm btn-outline-warning rounded-pill px-3 slot-chip">
                                    <input type="checkbox" name="timeSlots" value="17:00" class="d-none" checked onchange="handleSlotChipChange(this)">
                                    <span>17:00</span>
                                </label>
                            </div>

                            <%-- LIVE ESTIMATE BOX --%>
                            <div class="p-3 rounded-3 d-flex align-items-center justify-content-between" style="background: rgba(14, 165, 233, 0.15); border: 1px solid rgba(56, 189, 248, 0.3);">
                                <div>
                                    <i class="fa-solid fa-calculator text-cyan fs-5 me-2"></i>
                                    <span>Dự kiến tạo mới:</span>
                                    <strong id="weeklyEstimatedCount" class="text-warning fs-5 ms-1">0</strong> ca khám
                                </div>
                                <span class="text-white-50 small"><i class="fa-solid fa-shield-halved text-success me-1"></i>Tự động bỏ qua ca trùng</span>
                            </div>
                        </div>

                        <div class="modal-footer border-top border-secondary border-opacity-25 d-flex justify-content-between">
                            <button type="button" class="btn btn-sm btn-outline-danger rounded-pill px-3" onclick="clearWeeklyScheduleAjax()">
                                <i class="fa-solid fa-broom me-1"></i>Dọn Dẹp Ca Trống Đã Chọn
                            </button>
                            <div class="d-flex gap-2">
                                <button type="button" class="btn btn-sm btn-secondary rounded-pill px-3" data-bs-dismiss="modal">Đóng</button>
                                <button type="submit" class="btn btn-sm btn-primary-gradient rounded-pill px-4 fw-bold" id="btnSubmitWeekly">
                                    <i class="fa-solid fa-wand-magic-sparkles me-1"></i>Áp Dụng Lịch Toàn Tuần
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%-- MODAL KHỞI TẠO GÓI LIỆU TRÌNH MỚI (CREATE TREATMENT PACKAGE MODAL) --%>
        <div class="modal fade" id="createPackageModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-md">
                <div class="modal-content glass-card border border-warning border-opacity-40 text-white" style="background: rgba(15, 23, 42, 0.95); backdrop-filter: blur(16px);">
                    <div class="modal-header border-bottom border-secondary border-opacity-25">
                        <h5 class="modal-title fw-bold text-warning d-flex align-items-center gap-2">
                            <i class="fa-solid fa-wand-magic-sparkles text-warning"></i> Khởi Tạo Gói Liệu Trình Trọn Gói
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form action="${pageContext.request.contextPath}/doctor/dashboard" method="POST" novalidate="true">
                        <input type="hidden" name="action" value="create-treatment-package">
                        <div class="modal-body p-4">
                            <div class="mb-3">
                                <label class="form-label text-cyan fw-bold small"><i class="fa-solid fa-user me-1"></i>1. Chọn Bệnh Nhân:</label>
                                <select name="patientId" class="form-select bg-dark text-white border-secondary" required>
                                    <c:forEach var="p" items="${patientsList}">
                                        <option value="${p.id}"><c:out value="${p.fullname}"/> - SĐT: <c:out value="${p.phone}"/></option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="form-label text-warning fw-bold small"><i class="fa-solid fa-notes-medical me-1"></i>2. Chọn Dịch Vụ Liệu Trình:</label>
                                <select name="serviceId" class="form-select bg-dark text-white border-secondary" required>
                                    <c:forEach var="s" items="${servicesList}">
                                        <option value="${s.id}"><c:out value="${s.serviceName}"/></option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="form-label text-white fw-bold small"><i class="fa-solid fa-tag me-1"></i>3. Tên Gói (Tùy chọn):</label>
                                <input type="text" name="packageName" class="form-control bg-dark text-white border-secondary" placeholder="VD: Liệu Trình Trị Mụn 5 Buổi">
                            </div>

                            <div class="mb-3">
                                <label class="form-label text-emerald fw-bold small"><i class="fa-solid fa-list-ol me-1"></i>4. Quy Mô Số Buổi Khám/Chăm Sóc:</label>
                                <select name="totalSessions" class="form-select bg-dark text-white border-emerald">
                                    <option value="5" selected>Gói 5 Buổi (Tiêu Chuẩn)</option>
                                    <option value="10">Gói 10 Buổi (Chuyên Sâu)</option>
                                    <option value="3">Gói 3 Buổi (Trải Nghiệm)</option>
                                    <option value="15">Gói 15 Buổi (VIP)</option>
                                </select>
                            </div>
                        </div>

                        <div class="modal-footer border-top border-secondary border-opacity-25">
                            <button type="button" class="btn btn-secondary rounded-pill px-3" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-warning rounded-pill px-4 fw-bold text-dark">
                                <i class="fa-solid fa-plus me-1"></i>Tạo Gói Liệu Trình
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        window.DOCTOR_CTX = '${pageContext.request.contextPath}';
        window.DOCTOR_CURRENT_DATE = '${selectedDate}';
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/doctor.js" charset="UTF-8"></script>
</body>
</html>