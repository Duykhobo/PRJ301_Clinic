<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Lịch Sử Đặt Lịch Khám | PRJ301 Clinic</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex flex-column min-vh-100 py-5">

<div class="container my-auto">
    <div class="glass-card animate-fade-in mx-auto p-4 p-md-5" style="max-width: 1000px;">
        
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
            <h3 class="fw-bold mb-0">
                <i class="fa-solid fa-clock-rotate-left text-info me-2"></i>Lịch Sử Đặt Lịch Khám
            </h3>
            <a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="btn btn-primary-gradient">
                <i class="fa-solid fa-plus me-1"></i>Đặt Lịch Khám Mới
            </a>
        </div>

        <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

        <div class="table-responsive">
            <table class="table table-hover align-middle text-white mb-0" style="background: transparent;">
                <thead>
                    <tr class="text-muted border-bottom" style="border-color: rgba(255,255,255,0.1) !important;">
                        <th scope="col">#Mã</th>
                        <th scope="col">Dịch Vụ</th>
                        <th scope="col">Bác Sĩ</th>
                        <th scope="col">Ngày Khám</th>
                        <th scope="col">Tổng Tiền</th>
                        <th scope="col">Trạng Thái</th>
                        <th scope="col">Thanh Toán</th>
                        <th scope="col" class="text-end">Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty historyList}">
                            <c:forEach items="${historyList}" var="app">
                                <tr class="border-bottom" style="border-color: rgba(255,255,255,0.05) !important;">
                                    <td class="fw-bold text-info">#${app.id}</td>
                                    <td>${app.serviceName}</td>
                                    <td>${app.doctorName}</td>
                                    <td>${app.appointmentDate}</td>
                                    <td class="fw-bold text-warning">${app.totalPrice} VNĐ</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.status == 'PENDING'}">
                                                <span class="badge bg-warning text-dark px-3 py-2 rounded-pill">Chờ khám</span>
                                            </c:when>
                                            <c:when test="${app.status == 'CONFIRMED'}">
                                                <span class="badge bg-info text-dark px-3 py-2 rounded-pill">Đã xác nhận</span>
                                            </c:when>
                                            <c:when test="${app.status == 'COMPLETED'}">
                                                <span class="badge bg-success px-3 py-2 rounded-pill">Đã hoàn thành</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger px-3 py-2 rounded-pill">Đã hủy</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.paymentStatus == 'PAID'}">
                                                <span class="badge slot-btn-available px-3 py-2 rounded-pill">
                                                    <i class="fa-solid fa-circle-check me-1"></i>Đã thanh toán
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge slot-btn-booked px-3 py-2 rounded-pill">
                                                    <i class="fa-solid fa-circle-xmark me-1"></i>Chưa thanh toán
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">
                                        <c:if test="${app.paymentStatus == 'UNPAID'}">
                                            <a href="${pageContext.request.contextPath}/booking?action=payment&id=${app.id}" 
                                               class="btn btn-sm btn-outline-info rounded-pill px-3">
                                                <i class="fa-solid fa-qrcode me-1"></i>Thanh Toán VietQR
                                            </a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" class="text-center py-5 text-muted">
                                    <i class="fa-solid fa-inbox fs-1 d-block mb-3 opacity-50"></i>
                                    Bạn chưa có lịch hẹn khám nào trong hệ thống.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-outline-glass">
                <i class="fa-solid fa-house me-2"></i>Quay về Trang Chủ
            </a>
        </div>

    </div>
</div>

</body>
</html>
