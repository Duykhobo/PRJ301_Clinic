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
    <div class="glass-card animate-fade-in mx-auto" style="max-width: 850px; width: 100%;">
        <h3 class="fw-bold mb-4 text-center">
            <i class="fa-solid fa-qrcode text-info me-2"></i>Thanh Toán Lịch Hẹn SePay VietQR
        </h3>

        <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

        <div class="row g-4 align-items-center">
            <%-- Cột bên trái: Mã VietQR SePay Động & Trạng Thái Tự Động --%>
            <div class="col-md-5 text-center">
                <div class="p-3 rounded-4 position-relative" style="background: rgba(255, 255, 255, 0.05); border: 1px solid rgba(255,255,255,0.1);">
                    <img src="https://vietqr.app/img?bank=Sacombank&acc=070148520060&amount=${appointment.totalPrice}&des=${appointment.paymentContent}&template=compact&showinfo=true&holder=NGUYEN%20THANH%20DUY" 
                         alt="Mã VietQR SePay Sacombank" class="img-fluid rounded-3 mb-3 shadow-sm" style="max-width: 280px;">
                    
                    <div id="paymentStatusBadge" class="badge px-3 py-2 rounded-pill slot-btn-available mb-2 d-inline-flex align-items-center gap-2">
                        <i class="fa-solid fa-spinner fa-spin text-info" id="paymentStatusSpinner"></i>
                        <span id="paymentStatusText">Đang chờ quét mã VietQR...</span>
                    </div>

                    <p class="small text-muted mb-0"><i class="fa-solid fa-shield-halved me-1 text-info"></i>Hệ thống tự động xác nhận sau 2 giây khi chuyển khoản</p>
                </div>
            </div>

            <%-- Cột bên phải: Thông tin Lịch hẹn & Chuyển khoản --%>
            <div class="col-md-7">
                <h5 class="fw-bold text-info mb-3"><i class="fa-solid fa-receipt me-2"></i>Thông Tin Chi Tiết Lịch Hẹn</h5>
                
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
                        <td class="text-muted ps-0">Tổng Tiền Thanh Toán:</td>
                        <td class="fw-bold text-warning fs-5 text-end">${appointment.totalPrice} VNĐ</td>
                    </tr>
                    <tr>
                        <td class="text-muted ps-0">Nội Dung Chuyển Khoản:</td>
                        <td class="fw-bold text-info text-end fs-6">${appointment.paymentContent}</td>
                    </tr>
                </table>

                <div class="d-grid gap-2">
                    <%-- Nút Mô Phỏng Thanh Toán SePay Trực Tiếp (Dành Cho Demo/Test Localhost) --%>
                    <button type="button" class="btn btn-success py-2 rounded-3 shadow-sm fw-bold mb-1" onclick="simulateSepayWebhook(${appointment.id})">
                        <i class="fa-solid fa-bolt me-2"></i>Mô Phỏng Webhook SePay (Test Thanh Toán)
                    </button>

                    <a href="${pageContext.request.contextPath}/MainController?action=history" class="btn btn-primary-gradient py-2">
                        <i class="fa-solid fa-clock-rotate-left me-2"></i>Xem Lịch Sử Đặt Lịch
                    </a>
                    <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-outline-glass py-2">
                        <i class="fa-solid fa-house me-2"></i>Quay về Trang Chủ
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
        // Tự động kiểm tra trạng thái thanh toán thời gian thực mỗi 3 giây
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
                        spinner.className = 'fa-solid fa-circle-check text-success';
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
