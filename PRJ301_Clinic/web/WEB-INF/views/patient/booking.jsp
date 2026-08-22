<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <title>Đặt Lịch Khám | <c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Phòng Khám & Spa')}"/></title>
        <jsp:include page="/WEB-INF/views/components/head.jsp" />
    </head>
    <body class="d-flex flex-column min-vh-100">

        <%-- Dynamic Navbar Component --%>
        <jsp:include page="/WEB-INF/views/components/navbar.jsp" />

        <div class="container my-auto py-4">
            <div class="glass-card animate-fade-in mx-auto" style="max-width: 720px; width: 100%;">
                <div class="text-center mb-4">
                    <span class="badge bg-cyan bg-opacity-20 text-cyan rounded-pill px-3 py-2 mb-2 fs-7">
                        <i class="fa-solid fa-user-doctor me-1"></i>Hệ Thống Đặt Ca Khám Bệnh Trực Tuyến 24/7
                    </span>
                    <h3 class="fw-bold text-white mb-2">
                        <i class="fa-solid fa-calendar-check text-cyan me-2"></i>Đặt Lịch Khám Y Tế &amp; Spa
                    </h3>
                    <p class="text-muted small">Vui lòng điền thông tin bên dưới để đặt ca 60 phút chống trùng ca tự động</p>
                </div>

                <%-- Visual 4-Step Progress Flow --%>
                <div class="row g-2 text-center mb-4 pb-2 border-bottom border-secondary border-opacity-25 fs-7">
                    <div class="col-3">
                        <div id="step1Box" class="p-2 rounded-3 bg-cyan bg-opacity-20 text-cyan fw-bold border border-cyan border-opacity-30">
                            <span class="d-block text-cyan fs-6">1</span>Chọn Dịch Vụ
                        </div>
                    </div>
                    <div class="col-3">
                        <div id="step2Box" class="p-2 rounded-3 bg-dark bg-opacity-50 text-muted border border-secondary border-opacity-25">
                            <span class="d-block text-white fs-6">2</span>Chọn Bác Sĩ
                        </div>
                    </div>
                    <div class="col-3">
                        <div id="step3Box" class="p-2 rounded-3 bg-dark bg-opacity-50 text-muted border border-secondary border-opacity-25">
                            <span class="d-block text-white fs-6">3</span>Chọn Ngày
                        </div>
                    </div>
                    <div class="col-3">
                        <div id="step4Box" class="p-2 rounded-3 bg-dark bg-opacity-50 text-muted border border-secondary border-opacity-25">
                            <span class="d-block text-white fs-6">4</span>Chọn Ca Giờ
                        </div>
                    </div>
                </div>

                <%-- Component Banner Thông Báo Lỗi & Toast Notification --%>
                <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

                <form id="bookingForm" action="${pageContext.request.contextPath}/booking" method="POST" novalidate="true" onsubmit="return validateBookingForm(event)">
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <input type="hidden" id="selectedScheduleId" name="scheduleId" value="${selectedScheduleId}">

                    <%-- Bước 1: Chọn Dịch vụ --%>
                    <div class="mb-3">
                        <label class="form-label text-muted fw-semibold">1. Chọn Dịch Vụ Khám / Spa (*)</label>
                        <div class="input-group">
                            <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass ${not empty errors.serviceId ? 'is-invalid' : ''}" style="border-right: none;">
                                <i class="fa-solid fa-teeth text-cyan"></i>
                            </span>
                            <select name="serviceId" id="serviceSelect" class="form-select form-control-glass border-start-0 ps-0 ${not empty errors.serviceId ? 'is-invalid' : ''}" onchange="updateStepProgress()">
                                <option value="">-- Chọn dịch vụ khám hoặc spa --</option>
                                <c:forEach items="${services}" var="s">
                                    <option value="${s.id}" ${selectedServiceId == s.id ? 'selected' : ''}>${s.serviceName} - <fmt:formatNumber value="${s.price}" pattern="#,##0" maxFractionDigits="0"/> VNĐ</option>
                                </c:forEach>
                            </select>
                        </div>
                        <c:if test="${not empty errors.serviceId}">
                            <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.serviceId}</span></div>
                        </c:if>
                    </div>

                    <%-- Bước 2: Chọn Bác sĩ --%>
                    <div class="mb-3">
                        <label class="form-label text-muted fw-semibold">2. Chọn Bác Sĩ Phụ Trách (*)</label>
                        <div class="input-group">
                            <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass ${not empty errors.doctorId ? 'is-invalid' : ''}" style="border-right: none;">
                                <i class="fa-solid fa-user-doctor text-cyan"></i>
                            </span>
                            <select name="doctorId" id="doctorSelect" class="form-select form-control-glass border-start-0 ps-0 ${not empty errors.doctorId ? 'is-invalid' : ''}" onchange="updateStepProgress(); fetchSlots();">
                                <option value="">-- Chọn bác sĩ chuyên khoa --</option>
                                <c:forEach items="${doctors}" var="d">
                                    <option value="${d.id}" ${selectedDoctorId == d.id ? 'selected' : ''}>BS. ${not empty d.doctorName ? d.doctorName : d.fullname} (${d.specialty})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <c:if test="${not empty errors.doctorId}">
                            <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.doctorId}</span></div>
                        </c:if>
                    </div>

                    <%-- Bước 3: Chọn Ngày Khám --%>
                    <div class="mb-3">
                        <div class="d-flex justify-content-between align-items-center mb-1">
                            <label class="form-label text-muted fw-semibold mb-0">3. Chọn Ngày Khám Bệnh (*)</label>
                            <div class="d-flex gap-1">
                                <button type="button" class="btn btn-xs btn-outline-info rounded-pill px-2 py-0 fs-8" onclick="setQuickDate(0)">Hôm nay</button>
                                <button type="button" class="btn btn-xs btn-outline-info rounded-pill px-2 py-0 fs-8" onclick="setQuickDate(1)">Ngày mai</button>
                                <button type="button" class="btn btn-xs btn-outline-info rounded-pill px-2 py-0 fs-8" onclick="setQuickDate(2)">Ngày kia</button>
                            </div>
                        </div>
                        <div class="input-group">
                            <span class="input-group-text bg-transparent border-end-0 text-muted form-control-glass ${not empty errors.appointmentDate ? 'is-invalid' : ''}" style="border-right: none;">
                                <i class="fa-regular fa-calendar-days text-cyan"></i>
                            </span>
                            <input type="text" name="appointmentDate" id="appointmentDate" value="${selectedAppointmentDate}" class="form-control form-control-glass border-start-0 ps-0 ${not empty errors.appointmentDate ? 'is-invalid' : ''}" placeholder="Bấm vào đây để chọn ngày khám...">
                        </div>
                        <c:if test="${not empty errors.appointmentDate}">
                            <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.appointmentDate}</span></div>
                        </c:if>
                        <div class="mt-1" style="font-size:.78rem; color:rgba(255,255,255,.4);">
                            <i class="fa-solid fa-circle-info me-1 text-amber"></i>
                            Phòng khám làm việc <strong style="color:#fcd34d;">Thứ 2 — Thứ 7</strong>. Chủ Nhật nghỉ.
                        </div>
                    </div>

                    <%-- Bước 4: Sơ Đồ Ma Trận Slot Giờ Trực Quan (Zero Hardcoding - Tự Động AJAX) --%>
                    <div class="mb-4">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <label class="form-label text-muted fw-semibold mb-0">4. Chọn Ca Khám 60 Phút Khả Dụng (*)</label>
                            <span class="small text-muted" id="slotInstructionText"><i class="fa-solid fa-circle-info me-1 text-cyan"></i>Chọn bác sĩ &amp; ngày để nạp ca khám</span>
                        </div>

                        <%-- Thanh Chú Giải (Legend Badge Bar) --%>
                        <div class="d-flex flex-wrap align-items-center gap-2 mb-3 fs-7">
                            <span class="badge slot-btn-available px-3 py-2 rounded-pill">
                                <i class="fa-solid fa-circle me-1 text-emerald"></i>Khả dụng (Còn trống)
                            </span>
                            <span class="badge slot-btn-selected px-3 py-2 rounded-pill">
                                <i class="fa-solid fa-circle-check me-1"></i>Đang chọn
                            </span>
                            <span class="badge slot-btn-booked px-3 py-2 rounded-pill">
                                <i class="fa-solid fa-lock me-1"></i>Đã được đặt
                            </span>
                        </div>

                        <%-- Ma trận Nút Bấm Khung Giờ (Dynamic Slot Grid Matrix) --%>
                        <div class="row g-2" id="slotMatrixGrid">
                            <div class="col-12 text-center text-muted py-3 border border-dashed rounded-3" style="border-color: rgba(255,255,255,0.1) !important;">
                                <i class="fa-solid fa-info-circle me-1 text-cyan"></i>Vui lòng chọn Bác sĩ và Ngày khám để nạp sơ đồ ca khám khả dụng.
                            </div>
                        </div>
                        <c:if test="${not empty errors.scheduleId}">
                            <div class="field-error-text mt-2"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.scheduleId}</span></div>
                        </c:if>
                    </div>

                    <%-- Bước 5: Ghi chú --%>
                    <div class="mb-4">
                        <label class="form-label text-muted fw-semibold">5. Ghi Chú Tình Trạng Sức Khỏe (Tùy chọn)</label>
                        <textarea name="notes" class="form-control form-control-glass" rows="3" placeholder="Nhập triệu chứng, tiền sử dị ứng thuốc hoặc yêu cầu thêm cho bác sĩ...">${param.notes}</textarea>
                    </div>

                    <button type="submit" id="submitBookingBtn" class="btn btn-primary-gradient w-100 py-3 fs-6 rounded-pill">
                        <i class="fa-solid fa-credit-card me-2"></i>Xác Nhận Đặt Lịch &amp; Thanh Toán VietQR SePay
                    </button>
                </form>
            </div>
        </div>

        <%-- Dynamic Footer Component --%>
        <jsp:include page="/WEB-INF/views/components/footer.jsp" />

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <%-- Inject context path cho booking.js --%>
        <script>window.BOOKING_CTX = '${pageContext.request.contextPath}';</script>
        <script src="${pageContext.request.contextPath}/assets/js/booking.js" charset="UTF-8"></script>
    </body>
</html>