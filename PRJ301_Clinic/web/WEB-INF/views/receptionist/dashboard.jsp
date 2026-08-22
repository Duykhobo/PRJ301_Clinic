<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Lễ Tân &amp; Tiếp Đón | <c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Phòng Khám & Spa')}"/></title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>

<body>

<%-- 1. IMPORT RECEPTION SIDEBAR COMPONENT --%>
<jsp:include page="/WEB-INF/views/components/sidebar-receptionist.jsp" />

<%-- 2. NỘI DUNG CHÍNH DÀNH CHO LỄ TÂN --%>
<div class="container-fluid px-2 px-sm-4 py-3 py-sm-4 flex-grow-1">

    <%-- HERO BANNER --%>
    <div class="reception-hero mb-4 animate-fade-in">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
            <div class="d-flex align-items-center gap-3">
                <div class="hero-avatar">
                    <i class="fa-solid fa-headset text-white"></i>
                </div>
                <div>
                    <div class="d-flex align-items-center gap-2 mb-1">
                        <div class="live-dot"></div>
                        <span style="font-size:.75rem; color:#10b981; font-weight:700; letter-spacing:.06em; text-transform:uppercase;">LIVE · RECEPTION WORKSPACE</span>
                    </div>
                    <h4 class="fw-bold text-white mb-1" style="font-size:1.3rem;">
                        Sảnh Tiếp Đón — <c:out value="${sessionScope.LOGIN_USER.fullname}"/>
                    </h4>
                    <p class="mb-0" style="color:rgba(255,255,255,.6); font-size:.83rem;">
                        <i class="fa-solid fa-hospital me-1 text-emerald"></i>Điều phối khách hàng &amp; Thu tiền mặt &nbsp;·&nbsp;
                        <i class="fa-solid fa-calendar-day me-1 text-cyan"></i>${selectedDate}
                    </p>
                </div>
            </div>

            <%-- DATE FILTER --%>
            <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="GET" class="filter-bar" novalidate="true">
                <div class="filter-wrap">
                    <i class="fa-solid fa-calendar-days fi text-cyan"></i>
                    <input type="text" name="date" class="filter-input flatpickr-date" value="${selectedDate}" placeholder="Chọn ngày" autocomplete="off">
                </div>
                <button type="submit" class="btn-filter">
                    <i class="fa-solid fa-magnifying-glass me-1"></i>Lọc Ca
                </button>
            </form>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

    <%-- STAT CARDS --%>
    <div class="row g-3 mb-4 animate-fade-in">
        <div class="col-6 col-md-3">
            <div class="stat-card cyan">
                <div class="stat-icon"><i class="fa-solid fa-users"></i></div>
                <div>
                    <div class="stat-value">${totalCount}</div>
                    <div class="stat-label">Tổng khách đặt</div>
                </div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-card green">
                <div class="stat-icon"><i class="fa-solid fa-circle-check"></i></div>
                <div>
                    <div class="stat-value" style="font-size: 1.15rem;">
                        <fmt:formatNumber value="${todayRevenuePaid}" pattern="#,##0" maxFractionDigits="0"/> <span style="font-size: 0.75rem; font-weight: normal;">VNĐ</span>
                    </div>
                    <div class="stat-label">Đã thu (${paidCount} ca)</div>
                </div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-card amber">
                <div class="stat-icon"><i class="fa-solid fa-money-bill-wave"></i></div>
                <div>
                    <div class="stat-value">${cashUnpaidCount}</div>
                    <div class="stat-label">Chưa thu tiền</div>
                </div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-card blue">
                <div class="stat-icon"><i class="fa-solid fa-stethoscope"></i></div>
                <div>
                    <div class="stat-value">${completedCount}</div>
                    <div class="stat-label">Ca hoàn tất khám</div>
                </div>
            </div>
        </div>
    </div>

    <%-- APPOINTMENTS TABLE --%>
    <div class="panel animate-fade-in">
        <div class="panel-header flex-wrap gap-3">
            <div class="panel-title text-cyan">
                <i class="fa-solid fa-clipboard-list me-2"></i>
                Danh Sách Bệnh Nhân Ngày <span style="color:#0ea5e9; margin-left:.4rem;">${selectedDate}</span>
                <span id="receptionFilterCount" class="badge bg-dark border border-secondary text-cyan px-2 py-1 rounded-pill ms-2" style="font-size: .75rem;"></span>
            </div>
            <div class="d-flex gap-2 align-items-center flex-wrap">
                <%-- LIVE SEARCH INPUT --%>
                <div class="position-relative">
                    <i class="fa-solid fa-magnifying-glass position-absolute top-50 start-0 translate-middle-y ms-3 text-white-50" style="font-size:.8rem; pointer-events:none;"></i>
                    <input type="text" id="searchReception" class="adm-search-input" placeholder="Tìm Tên, SĐT, Bác sĩ..." oninput="filterReceptionTable()">
                </div>
                <%-- QUICK STATUS FILTER PILLS --%>
                <div class="btn-group btn-group-sm" role="group" id="receptionStatusFilterGroup">
                    <button type="button" class="btn btn-outline-secondary active text-white" onclick="setReceptionFilter('ALL', this)">Tất cả</button>
                    <button type="button" class="btn btn-outline-warning" onclick="setReceptionFilter('UNPAID', this)">Chưa Thu</button>
                    <button type="button" class="btn btn-outline-info" onclick="setReceptionFilter('CONFIRMED', this)">Đã Check-in</button>
                    <button type="button" class="btn btn-outline-success" onclick="setReceptionFilter('COMPLETED', this)">Hoàn Tất</button>
                    <button type="button" class="btn btn-outline-danger" onclick="setReceptionFilter('REFUND', this)">Hoàn Tiền</button>
                </div>
                <button type="button" class="btn btn-sm rounded-pill px-3 py-1.5 fw-bold shadow"
                    style="background:linear-gradient(135deg,#10b981,#0ea5e9); color:#fff; font-size:.82rem; border:none;"
                    data-bs-toggle="modal" data-bs-target="#walkInModal">
                    <i class="fa-solid fa-person-walking-arrow-right me-1"></i>+ Đặt Lịch Tại Quầy
                </button>
            </div>
        </div>

        <div style="overflow-x:auto;">
            <table class="tbl" id="receptionTable">
                <thead>
                    <tr>
                        <th style="padding-left:1.4rem;">Giờ Hẹn</th>
                        <th>Bệnh Nhân</th>
                        <th>Bác Sĩ</th>
                        <th>Dịch Vụ</th>
                        <th>Thanh Toán</th>
                        <th>Trạng Thái</th>
                        <th style="text-align:right; padding-right:1.4rem;">Thao Tác Tiếp Đón</th>
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
                                        <span class="doctor-chip">
                                            <i class="fa-solid fa-user-doctor me-1"></i><c:out value="${app.doctorName}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="service-chip"><c:out value="${app.serviceName}"/></span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.paymentStatus == 'PAID'}">
                                                <span class="badge-paid">
                                                    <i class="fa-solid fa-check-circle me-1"></i>Đã Thanh Toán
                                                </span>
                                                <div style="font-size:.7rem; color:rgba(255,255,255,.4); margin-top:.2rem;">${app.paymentMethod}</div>
                                            </c:when>
                                            <c:when test="${app.paymentStatus == 'REFUND_PENDING'}">
                                                <span class="badge bg-danger text-white px-2 py-1 rounded-pill shadow-sm">
                                                    <i class="fa-solid fa-hand-holding-dollar me-1"></i>Chờ Hoàn Tiền
                                                </span>
                                                <div style="font-size:.75rem; color:#fca5a5; margin-top:.2rem; font-weight:bold;">
                                                    Hoàn: <fmt:formatNumber value="${app.totalPrice}" pattern="#,##0" maxFractionDigits="0"/> VNĐ
                                                </div>
                                            </c:when>
                                            <c:when test="${app.paymentStatus == 'REFUNDED'}">
                                                <span class="badge bg-secondary text-white px-2 py-1 rounded-pill">
                                                    <i class="fa-solid fa-rotate-left me-1"></i>Đã Hoàn Tiền
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-unpaid">
                                                    <i class="fa-solid fa-hourglass-half me-1"></i>Chưa Thu
                                                </span>
                                                <div style="font-size:.75rem; color:#fcd34d; margin-top:.2rem; font-weight:bold;">
                                                    <fmt:formatNumber value="${app.totalPrice}" pattern="#,##0" maxFractionDigits="0"/> VNĐ
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.status == 'COMPLETED'}">
                                                <span class="badge-completed"><i class="fa-solid fa-circle-check me-1"></i>Hoàn Tất</span>
                                            </c:when>
                                            <c:when test="${app.status == 'CONFIRMED'}">
                                                <span class="badge-confirmed"><i class="fa-solid fa-user-check me-1"></i>Đã Tiếp Nhận</span>
                                            </c:when>
                                            <c:when test="${app.status == 'CANCELLED'}">
                                                <span class="badge-cancelled"><i class="fa-solid fa-ban me-1"></i>Đã Hủy</span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:choose>
                                                    <c:when test="${app.paymentStatus == 'PAID'}">
                                                        <span class="badge-pending" style="background: rgba(14, 165, 233, 0.15); color: #38bdf8; border: 1px solid rgba(56, 189, 248, 0.3);">
                                                            <i class="fa-solid fa-clock me-1"></i>Chờ Đón (Đã TT)
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge-pending"><i class="fa-solid fa-spinner fa-spin me-1"></i>Chờ Đón</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="text-align:right; padding-right:1.4rem;">
                                        <div class="d-flex gap-2 justify-content-end align-items-center">
                                            <c:if test="${app.paymentStatus == 'REFUND_PENDING'}">
                                                <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline" novalidate="true">
                                                    <input type="hidden" name="action" value="confirm-refund">
                                                    <input type="hidden" name="appointmentId" value="${app.id}">
                                                    <input type="hidden" name="date" value="${selectedDate}">
                                                    <button type="button" class="btn btn-sm btn-danger px-3 py-1 rounded-pill shadow-sm" style="font-size:0.8rem; font-weight:600;" title="Xác nhận đã chuyển tiền lại cho khách" onclick="confirmRefund(this.form, '<c:out value="${app.patientName}"/>')">
                                                        <i class="fa-solid fa-money-bill-transfer me-1"></i>Xác Nhận Hoàn Tiền
                                                    </button>
                                                </form>
                                            </c:if>

                                            <c:choose>
                                                <%-- Trường hợp 1: Ca mới Chờ Đón và Chưa Thu Tiền --%>
                                                <c:when test="${app.status == 'PENDING' && app.paymentStatus == 'UNPAID'}">
                                                    <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline" novalidate="true">
                                                        <input type="hidden" name="action" value="collect-cash">
                                                        <input type="hidden" name="appointmentId" value="${app.id}">
                                                        <input type="hidden" name="date" value="${selectedDate}">
                                                        <button type="button" class="btn-collect rounded-pill" title="Thu tiền mặt tại quầy và check-in vào sảnh" onclick="confirmCollectCash(this.form, '<c:out value="${app.patientName}"/>', '<fmt:formatNumber value="${app.totalPrice}" pattern="#,##0" maxFractionDigits="0"/> VNĐ')">
                                                            <i class="fa-solid fa-hand-holding-dollar me-1"></i>Tiền Mặt
                                                        </button>
                                                    </form>
                                                    <button type="button" class="btn btn-sm btn-outline-info rounded-pill px-3 py-1 shadow-sm fw-bold"
                                                        title="Mở mã VietQR SePay để khách quét chuyển khoản tại quầy"
                                                        onclick="openCounterQRModal(${app.id}, '<c:out value="${app.patientName}"/>', '<c:out value="${app.serviceName}"/>', '<fmt:formatNumber value="${app.totalPrice}" pattern="#,##0" maxFractionDigits="0"/>', '${app.totalPrice}', '${not empty app.paymentContent ? app.paymentContent : ('CLN'.concat(app.id))}')">
                                                        <i class="fa-solid fa-qrcode me-1"></i>Quét QR
                                                    </button>
                                                    <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline" novalidate="true">
                                                        <input type="hidden" name="action" value="confirm-checkin">
                                                        <input type="hidden" name="appointmentId" value="${app.id}">
                                                        <input type="hidden" name="date" value="${selectedDate}">
                                                        <button type="button" class="btn-checkin rounded-pill" title="Chỉ tiếp nhận vào sảnh, thu tiền sau" onclick="confirmCheckin(this.form, '<c:out value="${app.patientName}"/>')">
                                                            <i class="fa-solid fa-user-check me-1"></i>Check-in
                                                        </button>
                                                    </form>
                                                </c:when>

                                                <%-- Trường hợp 2: Ca mới Chờ Đón và ĐÃ Thanh Toán (Ví dụ qua VietQR SePay) --%>
                                                <c:when test="${app.status == 'PENDING'}">
                                                    <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline" novalidate="true">
                                                        <input type="hidden" name="action" value="confirm-checkin">
                                                        <input type="hidden" name="appointmentId" value="${app.id}">
                                                        <input type="hidden" name="date" value="${selectedDate}">
                                                        <button type="button" class="btn-checkin rounded-pill" title="Tiếp nhận bệnh nhân vào sảnh chờ khám" onclick="confirmCheckin(this.form, '<c:out value="${app.patientName}"/>')">
                                                            <i class="fa-solid fa-user-check me-1"></i>Tiếp Nhận Check-in
                                                        </button>
                                                    </form>
                                                </c:when>

                                                <%-- Trường hợp 3: Đã Check-in nhưng vẫn Chưa Thu Tiền --%>
                                                <c:when test="${app.paymentStatus == 'UNPAID' && app.status != 'CANCELLED'}">
                                                    <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline" novalidate="true">
                                                        <input type="hidden" name="action" value="collect-cash">
                                                        <input type="hidden" name="appointmentId" value="${app.id}">
                                                        <input type="hidden" name="date" value="${selectedDate}">
                                                        <button type="button" class="btn-collect rounded-pill" title="Thu tiền mặt" onclick="confirmCollectCash(this.form, '<c:out value="${app.patientName}"/>', '<fmt:formatNumber value="${app.totalPrice}" pattern="#,##0" maxFractionDigits="0"/> VNĐ')">
                                                            <i class="fa-solid fa-hand-holding-dollar me-1"></i>Tiền Mặt
                                                        </button>
                                                    </form>
                                                    <button type="button" class="btn btn-sm btn-outline-info rounded-pill px-3 py-1 shadow-sm fw-bold"
                                                        title="Mở mã VietQR SePay để khách quét chuyển khoản tại quầy"
                                                        onclick="openCounterQRModal(${app.id}, '<c:out value="${app.patientName}"/>', '<c:out value="${app.serviceName}"/>', '<fmt:formatNumber value="${app.totalPrice}" pattern="#,##0" maxFractionDigits="0"/>', '${app.totalPrice}', '${not empty app.paymentContent ? app.paymentContent : ('CLN'.concat(app.id))}')">
                                                        <i class="fa-solid fa-qrcode me-1"></i>Quét QR
                                                    </button>
                                                </c:when>
                                            </c:choose>

                                            <c:if test="${app.status != 'COMPLETED' && app.status != 'CANCELLED'}">
                                                <form id="cancelForm_${app.id}" action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline" novalidate="true">
                                                    <input type="hidden" name="action" value="cancel-appointment">
                                                    <input type="hidden" name="appointmentId" value="${app.id}">
                                                    <input type="hidden" name="date" value="${selectedDate}">
                                                    <button type="button" class="btn-cancel rounded-circle" title="Hủy cuộc hẹn" onclick="confirmCancelAppointment(${app.id}, '${app.patientName}')">
                                                        <i class="fa-solid fa-xmark"></i>
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7">
                                    <div class="empty-state">
                                        <i class="fa-solid fa-calendar-xmark text-cyan"></i>
                                        <p style="font-size:1rem; font-weight:600; color:rgba(255,255,255,.4);">Không có lịch hẹn nào trong ngày ${selectedDate}</p>
                                        <p style="font-size:.83rem; color:rgba(255,255,255,.25);">Chọn ngày khác để xem lịch tiếp đón</p>
                                    </div>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <%-- RECEPTIONIST PAGINATION BAR --%>
        <c:if test="${totalPages > 1}">
            <div class="d-flex justify-content-center p-3 border-top border-secondary opacity-75">
                <nav>
                    <ul class="pagination pagination-sm m-0">
                        <c:forEach var="p" begin="1" end="${totalPages}">
                            <li class="page-item ${p == currentPage ? 'active' : ''}">
                                <a class="page-link bg-dark text-white border-secondary" href="${pageContext.request.contextPath}/receptionist/dashboard?date=${selectedDate}&page=${p}">${p}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </nav>
            </div>
        </c:if>

    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<%-- ===== WALK-IN BOOKING MODAL ===== --%>
<div class="modal fade" id="walkInModal" tabindex="-1" aria-labelledby="walkInModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content" style="background:#0b1628; border:1px solid rgba(14,165,233,.25); border-radius:1rem;">
            <div class="modal-header" style="border-bottom:1px solid rgba(255,255,255,.08);">
                <h5 class="modal-title fw-bold text-white" id="walkInModalLabel">
                    <i class="fa-solid fa-person-walking-arrow-right me-2 text-emerald"></i>
                    Đặt Lịch Tại Quầy (Walk-in)
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" id="walkInForm" novalidate="true">
                <input type="hidden" name="action" value="walk-in-booking">
                <input type="hidden" name="date" value="${selectedDate}">
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <%-- Họ Tên Bệnh Nhân --%>
                        <div class="col-md-6">
                            <label class="form-label text-white fw-600" style="font-size:.88rem;">
                                <i class="fa-solid fa-user me-1 text-cyan"></i>Họ Tên Bệnh Nhân <span class="text-danger">*</span>
                            </label>
                            <input type="text" name="wi_patientName" id="wi_patientName" required
                                class="form-control" style="background:#111d35; border:1px solid rgba(14,165,233,.3); color:#fff; border-radius:.6rem;"
                                placeholder="Nguyễn Văn A">
                        </div>
                        <%-- Số Điện Thoại --%>
                        <div class="col-md-6">
                            <label class="form-label text-white fw-600" style="font-size:.88rem;">
                                <i class="fa-solid fa-phone me-1 text-cyan"></i>Số Điện Thoại <span class="text-danger">*</span>
                            </label>
                            <input type="text" name="wi_patientPhone" id="wi_patientPhone" required
                                class="form-control" style="background:#111d35; border:1px solid rgba(14,165,233,.3); color:#fff; border-radius:.6rem;"
                                placeholder="0901234567">
                        </div>
                        <%-- Chọn Dịch Vụ --%>
                        <div class="col-md-6">
                            <label class="form-label text-white fw-600" style="font-size:.88rem;">
                                <i class="fa-solid fa-briefcase-medical me-1 text-cyan"></i>Dịch Vụ <span class="text-danger">*</span>
                            </label>
                            <select name="wi_serviceId" id="wi_serviceId" required class="form-select"
                                style="background:#111d35; border:1px solid rgba(14,165,233,.3); color:#fff; border-radius:.6rem;">
                                <option value="">-- Chọn Dịch Vụ --</option>
                                <c:forEach var="svc" items="${allServices}">
                                    <option value="${svc.id}">${svc.serviceName} &#8212;
                                        <fmt:formatNumber value="${svc.price}" pattern="#,##0" maxFractionDigits="0"/> VNĐ
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <%-- Chọn Bác Sĩ --%>
                        <div class="col-md-6">
                            <label class="form-label text-white fw-600" style="font-size:.88rem;">
                                <i class="fa-solid fa-user-doctor me-1 text-cyan"></i>Bác Sĩ <span class="text-danger">*</span>
                            </label>
                            <select name="wi_doctorId" id="wi_doctorId" required class="form-select"
                                style="background:#111d35; border:1px solid rgba(14,165,233,.3); color:#fff; border-radius:.6rem;"
                                onchange="loadWalkInSlots()">
                                <option value="">-- Chọn Bác Sĩ --</option>
                                <c:forEach var="doc" items="${allDoctors}">
                                    <option value="${doc.id}">${doc.doctorName} — ${doc.specialty}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <%-- Ngày Khám --%>
                        <div class="col-md-6">
                            <label class="form-label text-white fw-600" style="font-size:.88rem;">
                                <i class="fa-solid fa-calendar-day me-1 text-cyan"></i>Ngày Khám <span class="text-danger">*</span>
                            </label>
                            <input type="date" name="wi_date" id="wi_date" required
                                class="form-control" style="background:#111d35; border:1px solid rgba(14,165,233,.3); color:#fff; border-radius:.6rem;"
                                value="${selectedDate}" onchange="loadWalkInSlots()">
                        </div>
                        <%-- Ca Khám (load động) --%>
                        <div class="col-md-6">
                            <label class="form-label text-white fw-600" style="font-size:.88rem;">
                                <i class="fa-solid fa-clock me-1 text-cyan"></i>Ca Khám (Giờ Trống) <span class="text-danger">*</span>
                            </label>
                            <select name="wi_scheduleId" id="wi_scheduleId" required class="form-select"
                                style="background:#111d35; border:1px solid rgba(14,165,233,.3); color:#fff; border-radius:.6rem;">
                                <option value="">-- Chọn Bác Sĩ và Ngày trước --</option>
                            </select>
                        </div>
                        <%-- Ghi Chú --%>
                        <div class="col-12">
                            <label class="form-label text-white fw-600" style="font-size:.88rem;">
                                <i class="fa-solid fa-note-sticky me-1 text-cyan"></i>Ghi Chú (Tùy Chọn)
                            </label>
                            <textarea name="wi_notes" id="wi_notes" rows="2" class="form-control"
                                style="background:#111d35; border:1px solid rgba(14,165,233,.3); color:#fff; border-radius:.6rem;"
                                placeholder="Lý do khám, triệu chứng...">[Chọn người dùng tại quầy - Lễ Tân]</textarea>
                        </div>
                    </div>
                    <div id="walkInAlert" class="mt-3" style="display:none;"></div>
                </div>
                <div class="modal-footer" style="border-top:1px solid rgba(255,255,255,.08);">
                    <button type="button" class="btn btn-secondary rounded-pill px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn rounded-pill px-4 fw-bold shadow"
                        style="background:linear-gradient(135deg,#10b981,#0ea5e9); color:#fff; border:none;">
                        <i class="fa-solid fa-circle-check me-1"></i>Xác Nhận &amp; Thu Tiền Mặt
                    </button>
                </div>
            </form>
        </div>
        <%-- MODAL QUÉT MÃ VIETQR TẠI QUẦY (COUNTER QR PAYMENT MODAL) --%>
        <div class="modal fade" id="counterQRModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-md">
                <div class="modal-content glass-card border border-info border-opacity-40 text-white" style="background: rgba(15, 23, 42, 0.96); backdrop-filter: blur(16px);">
                    <div class="modal-header border-bottom border-secondary border-opacity-25">
                        <h5 class="modal-title fw-bold text-cyan d-flex align-items-center gap-2">
                            <i class="fa-solid fa-qrcode text-cyan"></i> Thanh Toán VietQR Tại Quầy
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body p-4 text-center">
                        <div class="p-3 mb-3 rounded-4 shadow-sm mx-auto" style="background:#fff; max-width: 260px;">
                            <img id="counterQRImage" src="" alt="Mã VietQR Chuyển Khoản" class="img-fluid rounded-3 w-100 shadow-sm">
                        </div>

                        <div class="p-3 rounded-3 text-start mb-3" style="background: rgba(14, 165, 233, 0.1); border: 1px solid rgba(56, 189, 248, 0.3);">
                            <div class="d-flex justify-content-between mb-1">
                                <span class="text-muted small">Mã Ca Khám:</span>
                                <strong class="text-cyan" id="qrModalAppId">#--</strong>
                            </div>
                            <div class="d-flex justify-content-between mb-1">
                                <span class="text-muted small">Bệnh Nhân:</span>
                                <strong class="text-white" id="qrModalPatientName">--</strong>
                            </div>
                            <div class="d-flex justify-content-between mb-1">
                                <span class="text-muted small">Dịch Vụ:</span>
                                <strong class="text-white" id="qrModalServiceName">--</strong>
                            </div>
                            <div class="d-flex justify-content-between mb-1">
                                <span class="text-muted small">Số Tiền:</span>
                                <strong class="text-warning fs-5" id="qrModalAmount">0 VNĐ</strong>
                            </div>
                            <div class="d-flex justify-content-between">
                                <span class="text-muted small">Nội Dung Chuyển Khoản:</span>
                                <strong class="badge bg-dark border border-secondary text-cyan px-2 py-1" id="qrModalContent">CLN--</strong>
                            </div>
                        </div>

                        <form id="counterQRConfirmForm" action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" novalidate="true">
                            <input type="hidden" name="action" value="confirm-qr-payment">
                            <input type="hidden" name="appointmentId" id="qrConfirmAppId" value="">
                            <input type="hidden" name="date" value="${selectedDate}">
                            
                            <button type="submit" class="btn rounded-pill w-100 py-2.5 fw-bold shadow text-white"
                                style="background:linear-gradient(135deg,#0ea5e9,#10b981); border:none;">
                                <i class="fa-solid fa-bolt me-1"></i>Xác Nhận Đã Nhận Tiền &amp; Tiếp Nhận Vào Sảnh
                            </button>
                        </form>
                    </div>
                    <div class="modal-footer border-top border-secondary border-opacity-25 justify-content-center">
                        <button type="button" class="btn btn-sm btn-secondary rounded-pill px-4" data-bs-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<%-- Inject context path & SePay config cho receptionist.js --%>
<script>
    window.RECEPTIONIST_CTX = '${pageContext.request.contextPath}';
    window.SEPAY_BANK_NAME = '${not empty clinicSettings["SEPAY_BANK_NAME"] ? clinicSettings["SEPAY_BANK_NAME"] : "Sacombank"}';
    window.SEPAY_BANK_ACC = '${not empty clinicSettings["SEPAY_BANK_ACC"] ? clinicSettings["SEPAY_BANK_ACC"] : "070148520060"}';
    window.SEPAY_HOLDER = '${not empty clinicSettings["SEPAY_ACCOUNT_HOLDER"] ? clinicSettings["SEPAY_ACCOUNT_HOLDER"] : "NGUYEN%20THANH%20DUY"}';
</script>
<script src="${pageContext.request.contextPath}/assets/js/receptionist.js" charset="UTF-8"></script>

</body>
</html>

