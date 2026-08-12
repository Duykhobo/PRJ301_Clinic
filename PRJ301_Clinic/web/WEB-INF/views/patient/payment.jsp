<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Thanh Toán SePay VietQR | PRJ301 Clinic</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex align-items-center justify-content-center py-5">

<div class="glass-card animate-fade-in" style="max-width: 800px; width: 95%;">
    <h3 class="fw-bold mb-4 text-center">
        <i class="fa-solid fa-qrcode text-info me-2"></i>Thanh Toán Lịch Hẹn SePay VietQR
    </h3>

    <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

    <div class="row g-4 align-items-center">
        <%-- Cột bên trái: Mã VietQR SePay Động --%>
        <div class="col-md-5 text-center">
            <div class="p-3 rounded-4" style="background: rgba(255, 255, 255, 0.05); border: 1px solid rgba(255,255,255,0.1);">
                <img src="https://qr.sepay.vn/img?bank=MBBank&acc=0901234567&amount=${appointment.totalPrice}&des=${appointment.paymentContent}" 
                     alt="Mã VietQR SePay" class="img-fluid rounded-3 mb-2" style="max-width: 240px;">
                <p class="small text-muted mb-0"><i class="fa-solid fa-camera me-1"></i>Mở App Ngân Hàng để Quét QR</p>
            </div>
        </div>

        <%-- Cột bên phải: Thông tin Lịch hẹn & Chuyển khoản --%>
        <div class="col-md-7">
            <h5 class="fw-bold text-info mb-3">Thông Tin Chi Tiết Lịch Hẹn</h5>
            
            <table class="table table-borderless text-white mb-4">
                <tr>
                    <td class="text-muted ps-0">Mã Cuộc Hẹn:</td>
                    <td class="fw-bold text-end">#${appointment.id}</td>
                </tr>
                <tr>
                    <td class="text-muted ps-0">Tên Dịch Vụ:</td>
                    <td class="fw-bold text-end">${appointment.serviceName}</td>
                </tr>
                <tr>
                    <td class="text-muted ps-0">Bác Sĩ Đảm Nhận:</td>
                    <td class="fw-bold text-end">${appointment.doctorName}</td>
                </tr>
                <tr>
                    <td class="text-muted ps-0">Ngày Khám:</td>
                    <td class="fw-bold text-end">${appointment.appointmentDate}</td>
                </tr>
                <tr>
                    <td class="text-muted ps-0">Tổng Tiền:</td>
                    <td class="fw-bold text-warning fs-5 text-end">${appointment.totalPrice} VNĐ</td>
                </tr>
                <tr>
                    <td class="text-muted ps-0">Nội Dung Chuyển Khoản:</td>
                    <td class="fw-bold text-info text-end">${appointment.paymentContent}</td>
                </tr>
            </table>

            <div class="d-grid gap-2">
                <a href="${pageContext.request.contextPath}/MainController?action=history" class="btn btn-primary-gradient">
                    <i class="fa-solid fa-clock-rotate-left me-2"></i>Xem Lịch Sử Đặt Lịch
                </a>
                <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-outline-glass">
                    <i class="fa-solid fa-house me-2"></i>Quay về Trang Chủ
                </a>
            </div>
        </div>
    </div>
</div>

</body>
</html>
