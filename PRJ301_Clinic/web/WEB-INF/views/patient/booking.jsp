<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đặt Lịch Khám | PRJ301 Clinic</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex flex-column min-vh-100">

<%-- Dynamic Navbar Component --%>
<jsp:include page="/WEB-INF/views/components/navbar.jsp" />

<div class="container my-auto py-4">
    <div class="glass-card animate-fade-in mx-auto" style="max-width: 650px; width: 100%;">
        <h3 class="fw-bold mb-4 text-center">
            <i class="fa-solid fa-calendar-check text-info me-2"></i>Đặt Lịch Khám Trực Tuyến
        </h3>

        <%-- Nhúng Component Banner Thông Báo Lỗi --%>
        <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

        <form action="${pageContext.request.contextPath}/booking" method="POST">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" id="selectedScheduleId" name="scheduleId" required>

            <%-- Bước 1: Chọn Dịch vụ --%>
            <div class="mb-3">
                <label class="form-label text-muted">1. Chọn Dịch Vụ Khám / Spa (*)</label>
                <select name="serviceId" class="form-select form-control-glass" required>
                    <option value="">-- Chọn dịch vụ --</option>
                    <c:forEach items="${services}" var="s">
                        <option value="${s.id}">${s.serviceName} - ${s.price} VNĐ</option>
                    </c:forEach>
                </select>
            </div>

            <%-- Bước 2: Chọn Bác sĩ --%>
            <div class="mb-3">
                <label class="form-label text-muted">2. Chọn Bác Sĩ (*)</label>
                <select name="doctorId" id="doctorSelect" class="form-select form-control-glass" onchange="fetchSlots()" required>
                    <option value="">-- Chọn bác sĩ --</option>
                    <c:forEach items="${doctors}" var="d">
                        <option value="${d.id}">${d.doctorName} (${d.specialty})</option>
                    </c:forEach>
                </select>
            </div>

            <%-- Bước 3: Chọn Ngày Khám --%>
            <div class="mb-3">
                <label class="form-label text-muted">3. Chọn Ngày Khám (*)</label>
                <input type="date" name="appointmentDate" id="appointmentDate" class="form-control form-control-glass" onchange="fetchSlots()" required>
            </div>

            <%-- Bước 4: Sơ Đồ Ma Trận Slot Giờ Trực Quan (Zero Hardcoding - Tự Động AJAX) --%>
            <div class="mb-4">
                <label class="form-label text-muted d-block">4. Chọn Ca Khám 60 Phút Khả Dụng (*)</label>
                
                <%-- Thanh Chú Giải (Legend Badge Bar) --%>
                <div class="d-flex flex-wrap align-items-center gap-2 mb-3 fs-7">
                    <span class="badge slot-btn-available px-3 py-2 rounded-pill">
                        <i class="fa-solid fa-circle me-1"></i>Khả dụng (Còn trống)
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
                        <i class="fa-solid fa-info-circle me-1"></i>Vui lòng chọn Bác sĩ và Ngày khám để xem danh sách ca rảnh.
                    </div>
                </div>
            </div>

            <%-- Bước 5: Ghi chú --%>
            <div class="mb-4">
                <label class="form-label text-muted">5. Ghi Chú Cho Bác Sĩ (Tùy chọn)</label>
                <textarea name="notes" class="form-control form-control-glass" rows="3" placeholder="Nhập tình trạng sức khỏe hoặc yêu cầu thêm..."></textarea>
            </div>

            <button type="submit" class="btn btn-primary-gradient w-100">
                <i class="fa-solid fa-credit-card me-2"></i>Bấm Đặt Lịch Hẹn & Thanh Toán VietQR
            </button>
        </form>
    </div>
</div>

<%-- Dynamic Footer Component --%>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function selectSlot(scheduleId, btnElement) {
        document.getElementById('selectedScheduleId').value = scheduleId;
        document.querySelectorAll('.slot-pill').forEach(btn => {
            if (!btn.disabled) {
                btn.className = 'btn w-100 py-2 slot-pill slot-btn-available';
            }
        });
        btnElement.className = 'btn w-100 py-2 slot-pill slot-btn-selected';
    }

    function fetchSlots() {
        const doctorId = document.getElementById('doctorSelect').value;
        const date = document.getElementById('appointmentDate').value;
        const grid = document.getElementById('slotMatrixGrid');

        if (!doctorId || !date) {
            grid.innerHTML = '<div class="col-12 text-center text-muted py-3 border border-dashed rounded-3" style="border-color: rgba(255,255,255,0.1) !important;"><i class="fa-solid fa-info-circle me-1"></i>Vui lòng chọn Bác sĩ và Ngày khám để xem danh sách ca rảnh.</div>';
            return;
        }

        grid.innerHTML = '<div class="col-12 text-center text-info py-3"><i class="fa-solid fa-spinner fa-spin me-2"></i>Đang nạp danh sách ca khám từ CSDL...</div>';

        fetch('${pageContext.request.contextPath}/booking?action=get-slots&doctorId=' + doctorId + '&date=' + date)
            .then(res => res.json())
            .then(slots => {
                if (!slots || slots.length === 0) {
                    grid.innerHTML = '<div class="col-12 text-center text-warning py-3 border border-dashed rounded-3" style="border-color: rgba(255,255,255,0.1) !important;"><i class="fa-solid fa-triangle-exclamation me-1"></i>Bác sĩ chưa có ca làm việc nào trong ngày này.</div>';
                    return;
                }

                let html = '';
                for (let i = 0; i < slots.length; i++) {
                    const slot = slots[i];
                    const isAvailable = slot.isAvailable;
                    const btnClass = isAvailable ? 'slot-btn-available' : 'slot-btn-booked';
                    const icon = isAvailable ? 'fa-clock' : 'fa-lock';
                    const disabledStr = isAvailable ? '' : 'disabled';
                    const startTimeStr = slot.startTime ? slot.startTime.substring(0,5) : '';
                    const endTimeStr = slot.endTime ? slot.endTime.substring(0,5) : '';

                    html += '<div class="col-6 col-md-4">' +
                            '<button type="button" class="btn w-100 py-2 slot-pill ' + btnClass + '" ' +
                            'onclick="selectSlot(' + slot.id + ', this)" ' + disabledStr + '>' +
                            '<i class="fa-regular ' + icon + ' me-1"></i>' +
                            startTimeStr + ' - ' + endTimeStr +
                            '</button>' +
                            '</div>';
                }
                grid.innerHTML = html;
            })
            .catch(err => {
                grid.innerHTML = '<div class="col-12 text-center text-danger py-3"><i class="fa-solid fa-triangle-exclamation me-1"></i>Lỗi khi nạp lịch từ CSDL.</div>';
            });
    }
</script>

</body>
</html>