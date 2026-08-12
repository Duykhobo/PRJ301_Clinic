<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Sảnh Lễ Tân | PRJ301 Clinic Reception Workspace</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex flex-column min-vh-100">

<%-- Dynamic Navbar Component --%>
<jsp:include page="/WEB-INF/views/components/navbar.jsp" />

<div class="container my-4 flex-grow-1">
    <%-- Receptionist Banner Header --%>
    <div class="glass-card p-4 mb-4 animate-fade-in">
        <div class="row align-items-center g-3">
            <div class="col-md-8 d-flex align-items-center gap-3">
                <div class="avatar-circle bg-success-subtle text-success fs-3 fw-bold border border-success border-2" style="width: 60px; height: 60px; display: flex; align-items: center; justify-content: center; border-radius: 50%;">
                    <i class="fa-solid fa-headset"></i>
                </div>
                <div>
                    <h4 class="fw-bold text-white mb-1">
                        Sảnh Tiếp Đón Lễ Tân - ${sessionScope.LOGIN_USER.fullname}
                    </h4>
                    <p class="text-success mb-0 small">
                        <i class="fa-solid fa-hospital me-1"></i>Điều Phối Khách Hàng & Thu Tiền Mặt Sảnh | 
                        <i class="fa-solid fa-clock ms-2 me-1"></i>PRJ301 Clinic Reception
                    </p>
                </div>
            </div>

            <%-- Datepicker Filter --%>
            <div class="col-md-4">
                <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="GET" class="d-flex gap-2 justify-content-md-end">
                    <div class="position-relative flex-grow-1" style="max-width: 220px;">
                        <i class="fa-solid fa-calendar-days text-success position-absolute top-50 start-0 translate-middle-y ms-3 z-3"></i>
                        <input type="text" name="date" class="form-control form-control-dark flatpickr-date ps-5" value="${selectedDate}" placeholder="Chọn ngày khám">
                    </div>
                    <button type="submit" class="btn btn-primary-gradient px-3">
                        <i class="fa-solid fa-filter me-1"></i>Lọc
                    </button>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

    <%-- Summary Stats Cards --%>
    <div class="row g-3 mb-4 animate-fade-in">
        <div class="col-md-3">
            <div class="glass-card p-3 d-flex align-items-center gap-3 border-start border-4 border-info">
                <div class="p-3 rounded-circle bg-info bg-opacity-20 text-info fs-3">
                    <i class="fa-solid fa-users"></i>
                </div>
                <div>
                    <span class="text-muted small">Tổng Khách Đặt</span>
                    <h4 class="fw-bold text-white mb-0">${totalCount} Khách</h4>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="glass-card p-3 d-flex align-items-center gap-3 border-start border-4 border-success">
                <div class="p-3 rounded-circle bg-success bg-opacity-20 text-success fs-3">
                    <i class="fa-solid fa-qrcode"></i>
                </div>
                <div>
                    <span class="text-muted small">Đã Thanh Toán (SePay/Cash)</span>
                    <h4 class="fw-bold text-white mb-0">${paidCount} Ca</h4>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="glass-card p-3 d-flex align-items-center gap-3 border-start border-4 border-warning">
                <div class="p-3 rounded-circle bg-warning bg-opacity-20 text-warning fs-3">
                    <i class="fa-solid fa-money-bill-wave"></i>
                </div>
                <div>
                    <span class="text-muted small">Chưa Thu Tiền Sảnh</span>
                    <h4 class="fw-bold text-white mb-0">${cashUnpaidCount} Ca</h4>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="glass-card p-3 d-flex align-items-center gap-3 border-start border-4 border-primary">
                <div class="p-3 rounded-circle bg-primary bg-opacity-20 text-primary fs-3">
                    <i class="fa-solid fa-circle-check"></i>
                </div>
                <div>
                    <span class="text-muted small">Ca Hoàn Tất Khám</span>
                    <h4 class="fw-bold text-white mb-0">${completedCount} Ca</h4>
                </div>
            </div>
        </div>
    </div>

    <%-- Receptionist Table Overview --%>
    <div class="glass-card p-4 animate-fade-in">
        <h5 class="fw-bold text-white mb-3 d-flex align-items-center gap-2">
            <i class="fa-solid fa-clipboard-list text-success"></i>
            Danh Sách Đón Tiếp Bệnh Nhân Ngày ${selectedDate}
        </h5>

        <div class="table-responsive">
            <table class="table table-dark table-hover align-middle mb-0">
                <thead>
                    <tr class="text-success border-bottom border-secondary">
                        <th>Giờ Hen</th>
                        <th>Bệnh Nhân</th>
                        <th>Số Điện Thoại</th>
                        <th>Bác Sĩ Đảm Nhận</th>
                        <th>Dịch Vụ Khám</th>
                        <th>Trạng Thái Thanh Toán</th>
                        <th>Trạng Thái Khám</th>
                        <th class="text-end">Thao Tác Lễ Tân</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty appointments}">
                            <c:forEach var="app" items="${appointments}">
                                <tr>
                                    <td class="fw-bold text-info fs-6">
                                        <i class="fa-solid fa-clock me-1"></i>${app.startTime}
                                    </td>
                                    <td class="fw-bold text-white">${app.patientName}</td>
                                    <td class="text-muted">${app.patientPhone}</td>
                                    <td class="text-info-subtle"><i class="fa-solid fa-user-doctor me-1"></i>${app.doctorName}</td>
                                    <td><span class="badge bg-primary bg-opacity-20 text-primary border border-primary border-opacity-20">${app.serviceName}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.paymentStatus == 'PAID'}">
                                                <span class="badge bg-success bg-opacity-20 text-success border border-success border-opacity-25">
                                                    <i class="fa-solid fa-check me-1"></i>Đã Thanh Toán (${app.paymentMethod})
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning bg-opacity-20 text-warning border border-warning border-opacity-25">
                                                    <i class="fa-solid fa-clock me-1"></i>Chưa Thu (${app.totalPrice} VNĐ)
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.status == 'COMPLETED'}">
                                                <span class="badge bg-success"><i class="fa-solid fa-circle-check me-1"></i>Hoàn Tất</span>
                                            </c:when>
                                            <c:when test="${app.status == 'CONFIRMED'}">
                                                <span class="badge bg-info text-dark"><i class="fa-solid fa-user-check me-1"></i>Đã Check-in Sảnh</span>
                                            </c:when>
                                            <c:when test="${app.status == 'CANCELLED'}">
                                                <span class="badge bg-danger"><i class="fa-solid fa-ban me-1"></i>Đã Hủy</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning text-dark"><i class="fa-solid fa-spinner fa-spin me-1"></i>Chờ Đón Tiếp</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">
                                        <div class="d-flex gap-1 justify-content-end">
                                            <c:if test="${app.status == 'PENDING'}">
                                                <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline">
                                                    <input type="hidden" name="action" value="confirm-checkin">
                                                    <input type="hidden" name="appointmentId" value="${app.id}">
                                                    <input type="hidden" name="date" value="${selectedDate}">
                                                    <button type="submit" class="btn btn-sm btn-info fw-bold" title="Xác nhận bệnh nhân đã tới sảnh">
                                                        <i class="fa-solid fa-user-check me-1"></i>Check-in
                                                    </button>
                                                </form>
                                            </c:if>

                                            <c:if test="${app.paymentStatus == 'UNPAID' && app.status != 'CANCELLED'}">
                                                <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline">
                                                    <input type="hidden" name="action" value="collect-cash">
                                                    <input type="hidden" name="appointmentId" value="${app.id}">
                                                    <input type="hidden" name="date" value="${selectedDate}">
                                                    <button type="submit" class="btn btn-sm btn-success fw-bold" title="Xác nhận đã thu tiền mặt">
                                                        <i class="fa-solid fa-hand-holding-dollar me-1"></i>Thu Tiền
                                                    </button>
                                                </form>
                                            </c:if>

                                            <c:if test="${app.status != 'COMPLETED' && app.status != 'CANCELLED'}">
                                                <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline" onsubmit="return confirm('Bạn có chắc chắn muốn hủy cuộc hẹn #${app.id}?');">
                                                    <input type="hidden" name="action" value="cancel-appointment">
                                                    <input type="hidden" name="appointmentId" value="${app.id}">
                                                    <input type="hidden" name="date" value="${selectedDate}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Hủy cuộc hẹn">
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
                                <td colspan="8" class="text-center py-4 text-muted">
                                    <i class="fa-solid fa-calendar-xmark fs-2 mb-2 d-block"></i>
                                    Không có bệnh nhân nào đặt lịch trong ngày ${selectedDate}
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%-- Dynamic Footer Component --%>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
