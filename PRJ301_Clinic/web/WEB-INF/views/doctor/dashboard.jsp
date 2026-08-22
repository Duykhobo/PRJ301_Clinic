<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <title>Bác Sĩ &amp; Khám Bệnh | Phòng Khám &amp; Spa PRJ301</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
    <style>
        .slot-chip, .day-chip {
            cursor: pointer;
            user-select: none;
            transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
            border: 1px solid rgba(255, 255, 255, 0.18) !important;
            background: rgba(255, 255, 255, 0.05) !important;
            color: #94a3b8 !important;
        }
        .slot-chip:hover, .day-chip:hover {
            border-color: rgba(56, 189, 248, 0.5) !important;
            color: #f8fafc !important;
        }
        .slot-chip.active {
            background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%) !important;
            color: #0f172a !important;
            font-weight: 700 !important;
            border-color: #f59e0b !important;
            box-shadow: 0 0 12px rgba(245, 158, 11, 0.45) !important;
        }
        .day-chip.active {
            background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%) !important;
            color: #ffffff !important;
            font-weight: 700 !important;
            border-color: #38bdf8 !important;
            box-shadow: 0 0 12px rgba(14, 165, 233, 0.45) !important;
        }
    </style>
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
                                                    <button type="button" class="btn btn-xs ${slot.isAvailable ? 'btn-outline-warning' : 'btn-outline-success'} p-1 rounded-circle" title="${slot.isAvailable ? 'Khóa ca' : 'Mở ca'}" onclick="toggleSlotAjax(${slot.id}, ${!slot.isAvailable})">
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
                    <div class="panel-header">
                        <div class="panel-title text-cyan">
                            <i class="fa-solid fa-bed-pulse text-cyan"></i>
                            Lịch Khám Y Tế Ngày <span style="color:#38bdf8; margin-left:.4rem;">${selectedDate}</span>
                        </div>
                    </div>

            <div style="overflow-x:auto;">
                <table class="tbl">
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
                                    <tr>
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
                                                        onclick="openMedicalModal(${app.id}, '${app.patientName}', '${app.serviceName}', '${recordsMap[app.id].diagnosis}', '${recordsMap[app.id].prescriptionOrResult}')">
                                                        <i class="fa-solid fa-file-medical me-1"></i>Xem Bệnh Án
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="button" class="btn-examine rounded-pill"
                                                        onclick="openMedicalModal(${app.id}, '${app.patientName}', '${app.serviceName}', '${recordsMap[app.id].diagnosis}', '${recordsMap[app.id].prescriptionOrResult}')">
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
                                            <input type="range" class="form-range" id="moistureSlider" min="10" max="100" value="55" oninput="document.getElementById('moistureVal').innerText = this.value + '%'">
                                        </div>
                                    </div>
                                    <div class="col-12 col-md-4">
                                        <div class="p-2 rounded-2 bg-dark bg-opacity-50 border border-secondary border-opacity-30">
                                            <div class="d-flex justify-content-between small text-warning fw-bold mb-1">
                                                <span><i class="fa-solid fa-fire-flame-simple me-1"></i>Độ Tiết Dầu:</span>
                                                <span id="oilVal" class="text-white">60%</span>
                                            </div>
                                            <input type="range" class="form-range" id="oilSlider" min="10" max="100" value="60" oninput="document.getElementById('oilVal').innerText = this.value + '%'">
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

        <%-- Include Footer --%>
        <jsp:include page="/WEB-INF/views/components/footer.jsp" />

    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const CONTEXT_PATH = "${pageContext.request.contextPath}";
        let CURRENT_DATE = "${selectedDate}";

        flatpickr(".flatpickr-date", {
            dateFormat: "Y-m-d",
            defaultDate: "${selectedDate}"
        });

        function openMedicalModal(id, patientName, serviceName, diagnosis, prescription) {
            document.getElementById('modalAppointmentId').value = id;
            document.getElementById('modalPatientName').innerText = patientName;
            document.getElementById('modalServiceName').innerText = serviceName;
            document.getElementById('modalDiagnosis').value = diagnosis || '';
            document.getElementById('modalPrescription').value = prescription || '';

            // Khởi tạo ngẫu nhiên hoặc mặc định cho chỉ số da nếu chưa có
            const moisture = Math.floor(Math.random() * 25) + 50; // 50 - 75%
            const oil = Math.floor(Math.random() * 30) + 40; // 40 - 70%
            document.getElementById('moistureSlider').value = moisture;
            document.getElementById('moistureVal').innerText = moisture + '%';
            document.getElementById('oilSlider').value = oil;
            document.getElementById('oilVal').innerText = oil + '%';

            const modal = new bootstrap.Modal(document.getElementById('medicalModal'));
            modal.show();
        }

        // ── DOCTOR AJAX SCHEDULE MANAGEMENT ──
        function renderDoctorSlots(slots) {
            const container = document.getElementById('doctorSlotsContainer');
            if (!container) return;
            if (!slots || slots.length === 0) {
                container.innerHTML = '<div class="text-center py-4 text-muted"><i class="fa-solid fa-calendar-xmark me-2"></i>Bác sĩ chưa có khung giờ làm việc nào cho ngày này.<button type="button" class="btn btn-sm btn-outline-info rounded-pill px-3 py-1 ms-2" onclick="generateScheduleAjax()"><i class="fa-solid fa-wand-magic-sparkles me-1"></i>Tự Động Sinh Tất Cả Ca Khám</button></div>';
                return;
            }
            let html = '<div class="row g-3 animate-fade-in">';
            slots.forEach(slot => {
                const isAvail = slot.isAvailable === true || slot.isAvailable === 1;
                const borderClass = isAvail ? 'border-cyan bg-cyan bg-opacity-10' : 'border-secondary bg-dark text-muted';
                const clockIcon = isAvail ? 'text-cyan' : 'text-muted';
                const textClass = isAvail ? 'text-white' : 'text-muted';
                const statusBadge = isAvail ? '🟢 Khả dụng' : '🔴 Khóa / Đã đặt';
                const lockBtnClass = isAvail ? 'btn-outline-warning' : 'btn-outline-success';
                const lockIcon = isAvail ? 'fa-lock' : 'fa-unlock';
                const lockTitle = isAvail ? 'Khóa ca này' : 'Mở ca này';

                html += '<div class="col-12 col-sm-6 col-md-4 col-xl-3">' +
                        '<div class="p-3 rounded-3 border d-flex align-items-center justify-content-between gap-2 ' + borderClass + ' shadow-sm" style="transition:all 0.2s;">' +
                        '<div class="d-flex align-items-center gap-2">' +
                        '<i class="fa-solid fa-clock fs-5 ' + clockIcon + '"></i>' +
                        '<div>' +
                        '<div class="fw-bold fs-7 ' + textClass + '">' + slot.startTime + ' - ' + slot.endTime + '</div>' +
                        '<div class="fs-8 ' + (isAvail ? 'text-emerald fw-semibold' : 'text-danger') + '">' + statusBadge + '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div class="d-flex gap-1">' +
                        '<button type="button" class="btn btn-xs ' + lockBtnClass + ' p-1 rounded-circle" title="' + lockTitle + '" onclick="toggleSlotAjax(' + slot.id + ', ' + (!isAvail) + ')">' +
                        '<i class="fa-solid ' + lockIcon + '"></i>' +
                        '</button>' +
                        '<button type="button" class="btn btn-xs btn-outline-danger p-1 rounded-circle" title="Xóa ca này" onclick="deleteSlotAjax(' + slot.id + ')">' +
                        '<i class="fa-solid fa-trash"></i>' +
                        '</button>' +
                        '</div>' +
                        '</div>' +
                        '</div>';
            });
            html += '</div>';
            container.innerHTML = html;
        }

        function fetchDoctorSlotsAjax() {
            const params = new URLSearchParams();
            params.append("action", "get-slots");
            params.append("date", CURRENT_DATE);
            params.append("ajax", "true");

            fetch(CONTEXT_PATH + "/doctor/dashboard", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.slots) renderDoctorSlots(data.slots);
            })
            .catch(err => console.error("AJAX Error:", err));
        }

        function generateScheduleAjax() {
            fetchDoctorSlotsAjax();
        }

        function toggleSlotAjax(slotId, newStatus) {
            const params = new URLSearchParams();
            params.append("action", "toggle-slot");
            params.append("slotId", slotId);
            params.append("status", newStatus);
            params.append("date", CURRENT_DATE);
            params.append("ajax", "true");

            fetch(CONTEXT_PATH + "/doctor/dashboard", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.slots) renderDoctorSlots(data.slots);
                showToastNotification(data.success ? "success" : "error", data.message);
            })
            .catch(err => console.error("AJAX Error:", err));
        }

        function deleteSlotAjax(slotId) {
            Swal.fire({
                title: 'Xóa Ca Khám Này?',
                text: 'Bạn có chắc chắn muốn xóa khung giờ khám này không?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#ef4444',
                cancelButtonColor: '#64748b',
                confirmButtonText: '<i class="fa-solid fa-trash me-1"></i> Xóa Ca',
                cancelButtonText: 'Hủy Bỏ',
                background: '#0f172a',
                color: '#f8fafc',
                customClass: {
                    popup: 'border border-danger border-opacity-40 shadow-lg'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    const params = new URLSearchParams();
                    params.append("action", "delete-slot");
                    params.append("slotId", slotId);
                    params.append("date", CURRENT_DATE);
                    params.append("ajax", "true");

                    fetch(CONTEXT_PATH + "/doctor/dashboard", {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                        body: params
                    })
                    .then(res => res.json())
                    .then(data => {
                        if (data.slots) renderDoctorSlots(data.slots);
                        showToastNotification(data.success ? "success" : "error", data.message);
                    })
                    .catch(err => console.error("AJAX Error:", err));
                }
            });
        }

        function addSlotAjax(event) {
            event.preventDefault();
            const form = document.getElementById("addSlotForm");
            const params = new URLSearchParams(new FormData(form));
            params.append("date", CURRENT_DATE);

            fetch(CONTEXT_PATH + "/doctor/dashboard", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body: params
            })
            .then(res => res.json())
            .then(data => {
                if (data.slots) renderDoctorSlots(data.slots);
                showToastNotification(data.success ? "success" : "error", data.message);
                const modalEl = document.getElementById('addSlotModal');
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
            })
            .catch(err => console.error("AJAX Error:", err));
        }

        // ── WEEKLY BATCH SCHEDULE JAVASCRIPT HELPERS ──
        function setWeeklyPreset(preset) {
            const today = new Date();
            const dayOfWeek = today.getDay() === 0 ? 7 : today.getDay(); // 1 (Mon) -> 7 (Sun)
            
            let start = new Date(today);
            let end = new Date(today);

            if (preset === 'this_week') {
                start.setDate(today.getDate() - (dayOfWeek - 1));
                end.setDate(today.getDate() + (7 - dayOfWeek));
            } else if (preset === 'next_week') {
                start.setDate(today.getDate() + (8 - dayOfWeek));
                end.setDate(today.getDate() + (14 - dayOfWeek));
            } else if (preset === 'next_2_weeks') {
                start.setDate(today.getDate() + (8 - dayOfWeek));
                end.setDate(today.getDate() + (21 - dayOfWeek));
            }

            const startStr = start.toISOString().split('T')[0];
            const endStr = end.toISOString().split('T')[0];

            document.getElementById('weeklyStartDate').value = startStr;
            document.getElementById('weeklyEndDate').value = endStr;
            calculateWeeklyEstimate();
        }

        function toggleAllDays(selected) {
            document.querySelectorAll('#weeklyScheduleForm input[name="daysOfWeek"]').forEach(cb => {
                cb.checked = selected;
                updateChipStyle(cb, 'day-chip');
            });
            calculateWeeklyEstimate();
        }

        function selectWeekdayOnly() {
            document.querySelectorAll('#weeklyScheduleForm input[name="daysOfWeek"]').forEach(cb => {
                const val = parseInt(cb.value);
                cb.checked = (val >= 1 && val <= 5);
                updateChipStyle(cb, 'day-chip');
            });
            calculateWeeklyEstimate();
        }

        function selectMorningSlots() {
            const morning = ["08:00", "09:00", "10:00", "11:00"];
            document.querySelectorAll('#weeklyScheduleForm input[name="timeSlots"]').forEach(cb => {
                cb.checked = morning.includes(cb.value);
                updateChipStyle(cb, 'slot-chip');
            });
            calculateWeeklyEstimate();
        }

        function selectAfternoonSlots() {
            const afternoon = ["14:00", "15:00", "16:00", "17:00"];
            document.querySelectorAll('#weeklyScheduleForm input[name="timeSlots"]').forEach(cb => {
                cb.checked = afternoon.includes(cb.value);
                updateChipStyle(cb, 'slot-chip');
            });
            calculateWeeklyEstimate();
        }

        function selectAllSlots(selected) {
            document.querySelectorAll('#weeklyScheduleForm input[name="timeSlots"]').forEach(cb => {
                cb.checked = selected;
                updateChipStyle(cb, 'slot-chip');
            });
            calculateWeeklyEstimate();
        }

        function handleDayChipChange(cb) {
            updateChipStyle(cb, 'day-chip');
            calculateWeeklyEstimate();
        }

        function handleSlotChipChange(cb) {
            updateChipStyle(cb, 'slot-chip');
            calculateWeeklyEstimate();
        }

        function updateChipStyle(cb, parentClass) {
            const label = cb.closest('.' + parentClass);
            if (!label) return;
            if (cb.checked) {
                label.classList.add('active');
                label.style.opacity = "1";
            } else {
                label.classList.remove('active');
                label.style.opacity = "0.5";
            }
        }

        function calculateWeeklyEstimate() {
            const startVal = document.getElementById('weeklyStartDate').value;
            const endVal = document.getElementById('weeklyEndDate').value;
            if (!startVal || !endVal) {
                document.getElementById('weeklyEstimatedCount').innerText = "0";
                return;
            }

            const start = new Date(startVal);
            const end = new Date(endVal);
            if (start > end) {
                document.getElementById('weeklyEstimatedCount').innerText = "0";
                return;
            }

            const selectedDays = [];
            document.querySelectorAll('#weeklyScheduleForm input[name="daysOfWeek"]:checked').forEach(cb => {
                selectedDays.push(parseInt(cb.value));
            });

            const selectedSlotsCount = document.querySelectorAll('#weeklyScheduleForm input[name="timeSlots"]:checked').length;

            let matchingDaysCount = 0;
            let cur = new Date(start);
            while (cur <= end) {
                const day = cur.getDay() === 0 ? 7 : cur.getDay();
                if (selectedDays.includes(day)) {
                    matchingDaysCount++;
                }
                cur.setDate(cur.getDate() + 1);
            }

            const totalEstimate = matchingDaysCount * selectedSlotsCount;
            document.getElementById('weeklyEstimatedCount').innerText = totalEstimate;
        }

        function submitWeeklyScheduleAjax(event) {
            event.preventDefault();
            const start = document.getElementById('weeklyStartDate').value;
            const end = document.getElementById('weeklyEndDate').value;
            const estimate = document.getElementById('weeklyEstimatedCount').innerText;

            if (!start || !end) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Chưa Chọn Khoảng Ngày',
                    text: 'Vui lòng chọn ngày bắt đầu và kết thúc áp dụng lịch!',
                    background: '#0f172a',
                    color: '#f8fafc',
                    confirmButtonColor: '#0ea5e9'
                });
                return;
            }
            if (parseInt(estimate) <= 0) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Chưa Chọn Khung Giờ',
                    text: 'Vui lòng chọn ít nhất một ngày trong tuần và một khung giờ khám!',
                    background: '#0f172a',
                    color: '#f8fafc',
                    confirmButtonColor: '#0ea5e9'
                });
                return;
            }

            Swal.fire({
                title: 'Áp Dụng Lịch Khám Toàn Tuần?',
                html: 'Hệ thống sẽ tạo khoảng <b class="text-warning fs-5">' + estimate + '</b> ca khám 60 phút từ ngày <b class="text-cyan">' + start + '</b> đến ngày <b class="text-cyan">' + end + '</b>.<br><br><span class="text-emerald fw-semibold"><i class="fa-solid fa-shield-check me-1"></i>Hệ thống tự động bỏ qua các ca đã trùng lặp!</span>',
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#0ea5e9',
                cancelButtonColor: '#64748b',
                confirmButtonText: '<i class="fa-solid fa-wand-magic-sparkles me-1"></i> Xác Nhận Tạo Lịch',
                cancelButtonText: 'Hủy Bỏ',
                background: '#0f172a',
                color: '#f8fafc',
                customClass: {
                    popup: 'border border-cyan border-opacity-40 shadow-lg'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    const form = document.getElementById("weeklyScheduleForm");
                    const formData = new FormData(form);
                    const params = new URLSearchParams();
                    for (let pair of formData.entries()) {
                        params.append(pair[0], pair[1]);
                    }

                    const btn = document.getElementById("btnSubmitWeekly");
                    btn.disabled = true;
                    btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-1"></i>Đang xử lý...';

                    fetch(CONTEXT_PATH + "/doctor/dashboard", {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                        body: params
                    })
                    .then(res => res.json())
                    .then(data => {
                        btn.disabled = false;
                        btn.innerHTML = '<i class="fa-solid fa-wand-magic-sparkles me-1"></i>Áp Dụng Lịch Toàn Tuần';
                        if (data.success) {
                            Swal.fire({
                                icon: 'success',
                                title: 'Thành Công!',
                                text: data.message,
                                background: '#0f172a',
                                color: '#f8fafc',
                                confirmButtonColor: '#10b981'
                            });
                            const modalEl = document.getElementById('weeklyScheduleModal');
                            const modal = bootstrap.Modal.getInstance(modalEl);
                            if (modal) modal.hide();
                            generateScheduleAjax();
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Không Thể Tạo Lịch',
                                text: data.message,
                                background: '#0f172a',
                                color: '#f8fafc',
                                confirmButtonColor: '#ef4444'
                            });
                        }
                    })
                    .catch(err => {
                        btn.disabled = false;
                        btn.innerHTML = '<i class="fa-solid fa-wand-magic-sparkles me-1"></i>Áp Dụng Lịch Toàn Tuần';
                        console.error("AJAX Error:", err);
                        Swal.fire({
                            icon: 'error',
                            title: 'Lỗi Kết Nối Máy Chủ',
                            text: 'Vui lòng kiểm tra lại đường truyền!',
                            background: '#0f172a',
                            color: '#f8fafc'
                        });
                    });
                }
            });
        }

        function clearWeeklyScheduleAjax() {
            const start = document.getElementById('weeklyStartDate').value;
            const end = document.getElementById('weeklyEndDate').value;

            if (!start || !end) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Chưa Chọn Khoảng Ngày',
                    text: 'Vui lòng chọn Từ Ngày và Đến Ngày để thực hiện dọn dẹp ca trống!',
                    background: '#0f172a',
                    color: '#f8fafc',
                    confirmButtonColor: '#0ea5e9'
                });
                return;
            }

            Swal.fire({
                title: 'Xác Nhận Dọn Dẹp Ca Trống?',
                html: 'Thao tác này sẽ <b class="text-danger">XÓA TOÀN BỘ</b> các ca khám <b>TRỐNG</b> (chưa có bệnh nhân đặt) từ ngày <b class="text-cyan">' + start + '</b> đến ngày <b class="text-cyan">' + end + '</b>.<br><br><span class="text-emerald fw-semibold"><i class="fa-solid fa-shield-check me-1"></i>Các ca đã có bệnh nhân đặt sẽ được bảo toàn an toàn 100%.</span>',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#ef4444',
                cancelButtonColor: '#64748b',
                confirmButtonText: '<i class="fa-solid fa-trash-can me-1"></i> Xóa Ca Trống',
                cancelButtonText: 'Hủy Bỏ',
                background: '#0f172a',
                color: '#f8fafc',
                customClass: {
                    popup: 'border border-danger border-opacity-40 shadow-lg'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    const params = new URLSearchParams();
                    params.append("action", "clear-weekly-schedule");
                    params.append("startDate", start);
                    params.append("endDate", end);
                    params.append("ajax", "true");

                    document.querySelectorAll('#weeklyScheduleForm input[name="daysOfWeek"]:checked').forEach(cb => {
                        params.append("daysOfWeek", cb.value);
                    });

                    fetch(CONTEXT_PATH + "/doctor/dashboard", {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                        body: params
                    })
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) {
                            Swal.fire({
                                icon: 'success',
                                title: 'Đã Dọn Dẹp!',
                                text: data.message,
                                background: '#0f172a',
                                color: '#f8fafc',
                                confirmButtonColor: '#10b981'
                            });
                            generateScheduleAjax();
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Thất Bại',
                                text: data.message,
                                background: '#0f172a',
                                color: '#f8fafc',
                                confirmButtonColor: '#ef4444'
                            });
                        }
                    })
                    .catch(err => {
                        console.error("AJAX Error:", err);
                        Swal.fire({
                            icon: 'error',
                            title: 'Lỗi Kết Nối',
                            text: 'Không thể kết nối máy chủ!',
                            background: '#0f172a',
                            color: '#f8fafc'
                        });
                    });
                }
            });
        }

        // Init weekly modal preset on load
        document.addEventListener('DOMContentLoaded', () => {
            setWeeklyPreset('next_week');
            document.querySelectorAll('#weeklyScheduleForm input[type="checkbox"]').forEach(cb => {
                updateChipStyle(cb, cb.name === 'daysOfWeek' ? 'day-chip' : 'slot-chip');
            });
            calculateWeeklyEstimate();
        });

        function showToastNotification(type, message) {
            let container = document.getElementById("toastContainer");
            if (!container) {
                container = document.createElement("div");
                container.id = "toastContainer";
                container.style.cssText = "position:fixed;top:20px;right:20px;z-index:9999;display:flex;flex-direction:column;gap:10px;";
                document.body.appendChild(container);
            }

            const toast = document.createElement("div");
            toast.className = "toast-glass " + type;
            toast.style.cssText = "background:rgba(15,23,42,0.9);color:#fff;border-radius:12px;padding:12px 18px;border-left:4px solid " + (type === "success" ? "#22c55e" : "#ef4444") + ";box-shadow:0 10px 25px rgba(0,0,0,0.5);display:flex;align-items:center;gap:10px;animation:slideInRight 0.3s ease-out;";
            toast.innerHTML = '<i class="fa-solid ' + (type === "success" ? "fa-circle-check text-success" : "fa-triangle-exclamation text-danger") + ' fs-5"></i><span>' + message + '</span>';
            container.appendChild(toast);

            setTimeout(() => {
                toast.style.opacity = "0";
                toast.style.transition = "opacity 0.3s ease";
                setTimeout(() => toast.remove(), 300);
            }, 3000);
        }
    </script>
</body>
</html>