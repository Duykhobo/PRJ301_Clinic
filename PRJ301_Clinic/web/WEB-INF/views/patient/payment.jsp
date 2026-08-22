<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <title>Thanh Toán SePay VietQR | <c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Phòng Khám & Spa')}"/></title>
        <jsp:include page="/WEB-INF/views/components/head.jsp" />
        <!-- File CSS tách riêng -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/payment.css">
    </head>
    <body class="d-flex flex-column min-vh-100">

        <%-- Dynamic Navbar Component --%>
        <jsp:include page="/WEB-INF/views/components/navbar.jsp" />

        <div class="container my-auto py-4">
            <div class="glass-card animate-fade-in mx-auto" style="max-width: 900px; width: 100%;">
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
                    <%-- Cột bên trái: Mã VietQR SePay --%>
                    <div class="col-lg-5 text-center">
                        <div class="p-3 payment-box shadow-sm d-inline-block w-100" style="max-width: 320px;">
                            <img src="https://vietqr.app/img?bank=${not empty clinicSettings['SEPAY_BANK_NAME'] ? clinicSettings['SEPAY_BANK_NAME'] : 'Sacombank'}&acc=${not empty clinicSettings['SEPAY_BANK_ACC'] ? clinicSettings['SEPAY_BANK_ACC'] : '070148520060'}&amount=${appointment.totalPrice}&des=${appointment.paymentContent}&template=compact&showinfo=true&holder=${not empty clinicSettings['SEPAY_ACCOUNT_HOLDER'] ? clinicSettings['SEPAY_ACCOUNT_HOLDER'] : 'NGUYEN%20THANH%20DUY'}"
                                 alt="Mã VietQR SePay"
                                 class="img-fluid rounded-3 mb-3 shadow w-100">

                            <div id="paymentStatusBadge" class="badge px-3 py-2 rounded-pill slot-btn-available mb-2 d-inline-flex align-items-center justify-content-center gap-2 w-100">
                                <i class="fa-solid fa-spinner fa-spin text-cyan" id="paymentStatusSpinner"></i>
                                <span id="paymentStatusText" class="fs-8">Đang chờ hệ thống ghi nhận...</span>
                            </div>

                            <p class="small text-muted mb-0 fs-8"><i class="fa-solid fa-shield-halved me-1 text-cyan"></i>Tự động xác nhận trong 2 giây sau khi chuyển khoản</p>
                        </div>
                    </div>

                    <%-- Cột bên phải: Chi Tiết Đơn Khám --%>
                    <div class="col-lg-7">
                        <div class="p-4 payment-box shadow">
                            <h5 class="fw-bold text-cyan mb-3 border-bottom border-secondary border-opacity-25 pb-2">
                                <i class="fa-solid fa-receipt me-2"></i>Chi Tiết Đơn Khám Y Tế
                            </h5>

                            <div class="d-flex flex-column gap-3 mb-2">
                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="payment-label">Mã Ca Khám:</span>
                                    <span class="badge bg-cyan bg-opacity-20 text-cyan px-3 py-1 fs-6 fw-bold">#${appointment.id}</span>
                                </div>

                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="payment-label">Tên Dịch Vụ:</span>
                                    <span class="payment-value">${appointment.serviceName}</span>
                                </div>

                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="payment-label">Bác Sĩ Đảm Nhận:</span>
                                    <span class="payment-value text-cyan">${appointment.doctorName}</span>
                                </div>

                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="payment-label">Ngày Khám:</span>
                                    <span class="payment-value">${appointment.appointmentDate}</span>
                                </div>

                                <div class="d-flex justify-content-between align-items-center border-top border-bottom border-secondary border-opacity-25 py-2 my-1">
                                    <span class="payment-label fw-bold">Tổng Tiền Thanh Toán:</span>
                                    <span class="fs-4 fw-bold text-warning">
                                        <fmt:formatNumber value="${appointment.totalPrice}" pattern="#,##0" maxFractionDigits="0"/> VNĐ
                                    </span>
                                </div>

                                <div class="d-flex justify-content-between align-items-center bg-black bg-opacity-40 p-2.5 rounded-3 border border-secondary border-opacity-25">
                                    <span class="payment-label small">Nội Dung Chuyển Khoản:</span>
                                    <code class="fs-6 fw-bold text-emerald tracking-wider">${appointment.paymentContent}</code>
                                </div>
                            </div>
                        </div>

                        <%-- Các Nút Chức Năng --%>
                        <div class="d-flex flex-column gap-2 mt-4">
                            <%-- Nút Mô phỏng Webhook dành cho Demo / Đồ án --%>
                            <button type="button" class="btn btn-success-gradient py-2.5 rounded-pill shadow-sm fw-bold" onclick="simulateSepayWebhook(${appointment.id})">
                                <i class="fa-solid fa-bolt me-2"></i>Mô Phỏng Webhook SePay (Test Chuyển Khoản)
                            </button>

                            <%-- Nút Chuyển sang Tiền mặt khi đến khám (Fallback Option) --%>
                            <button type="button" class="btn btn-outline-glass w-100 py-2.5 rounded-pill shadow-sm text-center fw-medium" onclick="confirmPayCash(${appointment.id})">
                                <i class="fa-solid fa-money-bill-wave text-warning me-2"></i>Chọn Thanh Toán Tiền Mặt Khi Đến Khám
                            </button>

                            <div class="row g-2 mt-1">
                                <div class="col-6">
                                    <a href="${pageContext.request.contextPath}/MainController?action=history" class="btn btn-primary-gradient w-100 py-2 rounded-pill fs-7 text-center">
                                        <i class="fa-solid fa-clock-rotate-left me-1"></i>Nhật Ký Khám
                                    </a>
                                </div>
                                <div class="col-6">
                                    <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-outline-glass w-100 py-2 rounded-pill fs-7 text-center">
                                        <i class="fa-solid fa-house me-1"></i>Trang Chủ
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            window.PAYMENT_CTX = '${pageContext.request.contextPath}';
            window.PAYMENT_APPOINTMENT_ID = '${appointment.id}';
        </script>
        <script src="${pageContext.request.contextPath}/assets/js/payment.js" charset="UTF-8"></script>
    </body>
</html>