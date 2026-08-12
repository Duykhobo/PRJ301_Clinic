<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <title>Đặt Lịch Khám | PRJ301 Clinic</title>
        <jsp:include page="/WEB-INF/views/components/head.jsp" />
    </head>
    <body class="d-flex align-items-center justify-content-center py-5">

        <div class="glass-card animate-fade-in" style="max-width: 600px; width: 90%;">
            <h3 class="fw-bold mb-4 text-center">
                <i class="fa-solid fa-calendar-check text-info me-2"></i>Đặt Lịch Khám Trực Tuyến
            </h3>

            <%-- Nhúng Component Banner Thông Báo Lỗi --%>
            <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

            <form action="${pageContext.request.contextPath}/booking" method="POST">
                <input type="hidden" name="csrfToken" value="${csrfToken}">

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
                    <select name="doctorId" class="form-select form-control-glass" required>
                        <option value="">-- Chọn bác sĩ --</option>
                        <c:forEach items="${doctors}" var="d">
                            <option value="${d.id}">${d.doctorName} (${d.specialty})</option>
                        </c:forEach>
                    </select>
                </div>

                <%-- Bước 3: Chọn Ngày Khám --%>
                <div class="mb-3">
                    <label class="form-label text-muted">3. Chọn Ngày Khám (*)</label>
                    <input type="date" name="appointmentDate" class="form-control form-control-glass" required>
                </div>

                <%-- Bước 4: Nhập Mã Slot Ca Khám --%>
                <div class="mb-3">
                    <label class="form-label text-muted">4. Mã Ca Khám Slot 60 Phút (*)</label>
                    <input type="number" name="scheduleId" class="form-control form-control-glass" placeholder="Nhập mã khung giờ (ví dụ: 1)" required>
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

    </body>
</html>