<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Thanh Toán SePay VietQR | PRJ301 Clinic</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex flex-column min-vh-100">

<%-- Dynamic Navbar Component --%>
<jsp:include page="/WEB-INF/views/components/navbar.jsp" />

<div class="container my-auto py-4">
    <div class="glass-card animate-fade-in mx-auto" style="max-width: 880px; width: 100%;">
        <div class="text-center mb-4">
            <span class="badge bg-cyan bg-opacity-20 text-cyan rounded-pill px-3 py-2 mb-2 fs-7">
                <i class="fa-solid fa-qrcode me-1"></i>Hóa Đơn Khám Y Tế VietQR SePay Tự Động
            </span>
            <h3 class="fw-bold text-white mb-2">
                <i class="fa-solid fa-credit-card text-cyan me-2"></i>Thanh Toán Hóa Đơn Lịch Khám
            </h3>
            <p class="text-muted small">Mở ứng dụng Ngân hàng hoặc Ví điện tử để quét mã VietQR bên dưới</p>
        </div>

        <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

        <div class="row g-4 align-items-center">
            <%-- Cột bên trái: Mã VietQR SePay Động & Trạng Thái Tự Động --%>
            <div class="col-md-5 text-center">
                <div class="p-3 rounded-4 position-relative" style="background: rgba(15, 23, 42, 0.85); border: 1px solid rgba(56, 189, 248, 0.25);">
                    <img src="https://vietqr.app/img?bank=${not empty clinicSettings['SEPAY_BANK_NAME'] ? clinicSettings['SEPAY_BANK_NAME'] : 'Sacombank'}&acc=${not empty clinicSettings['SEPAY_BANK_ACC'] ? clinicSettings['SEPAY_BANK_ACC'] : '070148520060'}&amount=${appointment.totalPrice}&des=${appointment.paymentContent}&template=compact&showinfo=true&holder=${not empty clinicSettings['SEPAY_ACCOUNT_HOLDER'] ? clinicSettings['SEPAY_ACCOUNT_HOLDER'] : 'NGUYEN%20THANH%20DUY'}" 
                         alt="Mã VietQR SePay ${not empty clinicSettings['SEPAY_BANK_NAME'] ? clinicSettings['SEPAY_BANK_NAME'] : 'Sacombank'}" class="img-fluid rounded-3 mb-3 shadow" style="max-width: 280px;">
                    
                    <div id="paymentStatusBadge" class="badge px-3 py-2 rounded-pill slot-btn-available mb-2 d-inline-flex align-items-center gap-2">
                        <i class="fa-solid fa-spinner fa-spin text-cyan" id="paymentStatusSpinner"></i>
                        <span id="paymentStatusText">Đang chờ hệ thống ghi nhận giao dịch...</span>
                    </div>

                    <p class="small text-muted mb-0 fs-7"><i class="fa-solid fa-shield-halved me-1 text-cyan"></i>Tự động xác nhận trong 2 giây sau khi chuyển khoản</p>
                </div>
            </div>

            <%-- Cột bên phải: Thông tin Lịch hẹn & Chuyển khoản --%>
            <div class="col-md-7">
                <h5 class="fw-bold text-cyan mb-3"><i class="fa-solid fa-receipt me-2"></i>Chi Tiết Đơn Khám Y Tế</h5>
                
                <table class="table table-borderless text-white mb-4">
                    <tr>
                        <td class="text-muted ps-0">Mã Ca Khám:</td>
                        <td class="fw-bold text-end text-white">#${appointment.id}</td>
                    </tr>
                    <tr>
                        <td class="text-muted ps-0">Tên Dịch Vụ:</td>
                        <td class="fw-bold text-end text-white">${appointment.serviceName}</td>
                    </tr>
                    <tr>
                        <td class="text-muted ps-0">Bác Sĩ Đảm Nhận:</td>
                        <td class="fw-bold text-end text-cyan">${appointment.doctorName}</td>
                    </tr>
                    <tr>
                        <td class="text-muted ps-0">Ngày Khám:</td>
                        <td class="fw-bold text-end text-white">${appointment.appointmentDate}</td>
                    </tr>
                    <tr>
                        <td class="text-muted ps-0">Tổng Tiền Thanh Toán:</td>
                        <td class="fw-bold text-warning fs-5 text-end">${appointment.totalPrice} VNĐ</td>
                    </tr>
                    <tr>
                        <td class="text-muted ps-0">Nội Dung Chuyển Khoản:</td>
                        <td class="fw-bold text-cyan text-end fs-6">${appointment.paymentContent}</td>
                    </tr>
                </table>

                <div class="d-grid gap-2">
                    <%-- Nút Mô Phỏng Thanh Toán SePay Trực Tiếp (Dành Cho Demo/Test Localhost) --%>
                    <button type="button" class="btn btn-success py-2 rounded-pill shadow-sm fw-bold mb-1" onclick="simulateSepayWebhook(${appointment.id})">
                        <i class="fa-solid fa-bolt me-2"></i>Mô Phỏng Webhook SePay (Test Chuyển Khoản)
                    </button>

                    <a href="${pageContext.request.contextPath}/MainController?action=history" class="btn btn-primary-gradient py-2 rounded-pill">
                        <i class="fa-solid fa-clock-rotate-left me-2"></i>Xem Nhật Ký Khám Bệnh
                    </a>
                    <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-outline-glass py-2 rounded-pill">
                        <i class="fa-solid fa-house me-2"></i>Quay Về Trang Chủ
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- Dynamic Footer Component --%>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    let pollInterval = null;

    document.addEventListener('DOMContentLoaded', function() {
        pollInterval = setInterval(checkPaymentStatus, 3000);
    });

    function checkPaymentStatus() {
        fetch('${pageContext.request.contextPath}/booking?action=check-payment-status&id=${appointment.id}')
            .then(res => res.json())
            .then(data => {
                if (data.paymentStatus === 'PAID') {
                    clearInterval(pollInterval);
                    const badge = document.getElementById('paymentStatusBadge');
                    const text = document.getElementById('paymentStatusText');
                    const spinner = document.getElementById('paymentStatusSpinner');

                    if (badge && text) {
                        badge.className = 'badge px-3 py-2 rounded-pill slot-btn-selected mb-2 d-inline-flex align-items-center gap-2';
                        spinner.className = 'fa-solid fa-circle-check text-emerald';
                        text.innerText = 'ĐÃ THANH TOÁN SEPAY THÀNH CÔNG!';
                    }

                    showToast('Xác nhận thanh toán SePay VietQR thành công!', true);
                    setTimeout(() => {
                        window.location.href = '${pageContext.request.contextPath}/MainController?action=history';
                    }, 2000);
                }
            })
            .catch(err => console.log('Checking status...'));
    }

    function simulateSepayWebhook(appointmentId) {
        showToast('Đang gửi tín hiệu mô phỏng thanh toán SePay...', false);
        fetch('${pageContext.request.contextPath}/sepay-webhook?appointmentId=' + appointmentId)
            .then(res => res.json())
            .then(data => {
                if (data.status === 200) {
                    checkPaymentStatus();
                } else {
                    showToast('Lỗi mô phỏng thanh toán: ' + data.message, false);
                }
            })
            .catch(err => showToast('Khởi chạy mô phỏng thất bại', false));
    }
</script>
</body>
</html>

