<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Sảnh Lễ Tân | PRJ301 Clinic Reception</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>

<body class="d-flex flex-column min-vh-100">

<jsp:include page="/WEB-INF/views/components/navbar.jsp" />

<div class="container my-4 flex-grow-1">

    <%-- ── HERO BANNER ── --%>
    <div class="reception-hero mb-4 animate-fade-in">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
            <div class="d-flex align-items-center gap-3">
                <div class="hero-avatar">
                    <i class="fa-solid fa-headset text-white"></i>
                </div>
                <div>
                    <div class="d-flex align-items-center gap-2 mb-1">
                        <div class="live-dot"></div>
                        <span style="font-size:.75rem; color:#10b981; font-weight:700; letter-spacing:.06em; text-transform:uppercase;">LIVE · Reception Workspace</span>
                    </div>
                    <h4 class="fw-bold text-white mb-1" style="font-size:1.3rem;">
                        Sảnh Tiếp Đón — ${sessionScope.LOGIN_USER.fullname}
                    </h4>
                    <p class="mb-0" style="color:rgba(255,255,255,.5); font-size:.83rem;">
                        <i class="fa-solid fa-hospital me-1" style="color:#10b981;"></i>Điều phối khách hàng &amp; Thu tiền mặt &nbsp;·&nbsp;
                        <i class="fa-solid fa-calendar-day me-1" style="color:#06b6d4;"></i>${selectedDate}
                    </p>
                </div>
            </div>

            <%-- DATE FILTER --%>
            <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="GET" class="filter-bar">
                <div class="filter-wrap">
                    <i class="fa-solid fa-calendar-days fi"></i>
                    <input type="text" name="date" class="filter-input flatpickr-date" value="${selectedDate}" placeholder="Chọn ngày" autocomplete="off">
                </div>
                <button type="submit" class="btn-filter">
                    <i class="fa-solid fa-magnifying-glass"></i> Lọc
                </button>
            </form>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

    <%-- ── STAT CARDS ── --%>
    <div class="row g-3 mb-4 animate-fade-in">
        <div class="col-6 col-md-3">
            <div class="stat-card cyan">
                <div class="stat-icon"><i class="fa-solid fa-users"></i></div>
                <div>
                    <div class="stat-value">${totalCount}</div>
                    <div class="stat-label">Tổng khách đặt</div>
                </div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-card green">
                <div class="stat-icon"><i class="fa-solid fa-circle-check"></i></div>
                <div>
                    <div class="stat-value">${paidCount}</div>
                    <div class="stat-label">Đã thanh toán</div>
                </div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-card amber">
                <div class="stat-icon"><i class="fa-solid fa-money-bill-wave"></i></div>
                <div>
                    <div class="stat-value">${cashUnpaidCount}</div>
                    <div class="stat-label">Chưa thu tiền</div>
                </div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-card blue">
                <div class="stat-icon"><i class="fa-solid fa-stethoscope"></i></div>
                <div>
                    <div class="stat-value">${completedCount}</div>
                    <div class="stat-label">Ca hoàn tất khám</div>
                </div>
            </div>
        </div>
    </div>

    <%-- ── APPOINTMENTS TABLE ── --%>
    <div class="panel animate-fade-in">
        <div class="panel-header">
            <div class="panel-title">
                <i class="fa-solid fa-clipboard-list"></i>
                Danh Sách Bệnh Nhân Ngày <span style="color:#06b6d4; margin-left:.4rem;">${selectedDate}</span>
            </div>
            <span style="font-size:.78rem; color:rgba(255,255,255,.4);">
                <i class="fa-solid fa-table me-1"></i>${totalCount} lịch hẹn
            </span>
        </div>

        <div style="overflow-x:auto;">
            <table class="tbl">
                <thead>
                    <tr>
                        <th style="padding-left:1.4rem;">Giờ Hẹn</th>
                        <th>Bệnh Nhân</th>
                        <th>Bác Sĩ</th>
                        <th>Dịch Vụ</th>
                        <th>Thanh Toán</th>
                        <th>Trạng Thái</th>
                        <th style="text-align:right; padding-right:1.4rem;">Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty appointments}">
                            <c:forEach var="app" items="${appointments}">
                                <tr>
                                    <td style="padding-left:1.4rem;">
                                        <span class="time-bubble">
                                            <i class="fa-solid fa-clock"></i>${app.startTime}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="patient-name">${app.patientName}</div>
                                        <div class="patient-phone"><i class="fa-solid fa-phone" style="font-size:.7rem;"></i> ${app.patientPhone}</div>
                                    </td>
                                    <td>
                                        <span class="doctor-chip">
                                            <i class="fa-solid fa-user-doctor"></i>${app.doctorName}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="service-chip">${app.serviceName}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.paymentStatus == 'PAID'}">
                                                <span class="badge-paid">
                                                    <i class="fa-solid fa-check-circle"></i>Đã Thanh Toán
                                                </span>
                                                <div style="font-size:.7rem; color:rgba(255,255,255,.35); margin-top:.2rem;">${app.paymentMethod}</div>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-unpaid">
                                                    <i class="fa-solid fa-hourglass-half"></i>Chưa Thu
                                                </span>
                                                <div style="font-size:.72rem; color:#fcd34d; margin-top:.2rem; font-weight:600;">
                                                    <fmt:formatNumber value="${app.totalPrice}" type="number" groupingUsed="true"/> đ
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.status == 'COMPLETED'}">
                                                <span class="badge-completed"><i class="fa-solid fa-circle-check"></i>Hoàn Tất</span>
                                            </c:when>
                                            <c:when test="${app.status == 'CONFIRMED'}">
                                                <span class="badge-confirmed"><i class="fa-solid fa-user-check"></i>Đã Check-in</span>
                                            </c:when>
                                            <c:when test="${app.status == 'CANCELLED'}">
                                                <span class="badge-cancelled"><i class="fa-solid fa-ban"></i>Đã Hủy</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-pending"><i class="fa-solid fa-spinner fa-spin"></i>Chờ Đón</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="text-align:right; padding-right:1.4rem;">
                                        <div class="d-flex gap-2 justify-content-end">
                                            <c:if test="${app.status == 'PENDING'}">
                                                <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline">
                                                    <input type="hidden" name="action" value="confirm-checkin">
                                                    <input type="hidden" name="appointmentId" value="${app.id}">
                                                    <input type="hidden" name="date" value="${selectedDate}">
                                                    <button type="submit" class="btn-checkin">
                                                        <i class="fa-solid fa-user-check"></i>Check-in
                                                    </button>
                                                </form>
                                            </c:if>

                                            <c:if test="${app.paymentStatus == 'UNPAID' && app.status != 'CANCELLED'}">
                                                <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline">
                                                    <input type="hidden" name="action" value="collect-cash">
                                                    <input type="hidden" name="appointmentId" value="${app.id}">
                                                    <input type="hidden" name="date" value="${selectedDate}">
                                                    <button type="submit" class="btn-collect">
                                                        <i class="fa-solid fa-hand-holding-dollar"></i>Thu Tiền
                                                    </button>
                                                </form>
                                            </c:if>

                                            <c:if test="${app.status != 'COMPLETED' && app.status != 'CANCELLED'}">
                                                <form action="${pageContext.request.contextPath}/receptionist/dashboard" method="POST" class="d-inline"
                                                      onsubmit="return confirm('Hủy cuộc hẹn #${app.id} của ${app.patientName}?');">
                                                    <input type="hidden" name="action" value="cancel-appointment">
                                                    <input type="hidden" name="appointmentId" value="${app.id}">
                                                    <input type="hidden" name="date" value="${selectedDate}">
                                                    <button type="submit" class="btn-cancel" title="Hủy cuộc hẹn">
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
                                <td colspan="7">
                                    <div class="empty-state">
                                        <i class="fa-solid fa-calendar-xmark"></i>
                                        <p style="font-size:1rem; font-weight:600; color:rgba(255,255,255,.4);">Không có lịch hẹn nào trong ngày ${selectedDate}</p>
                                        <p style="font-size:.83rem; color:rgba(255,255,255,.25);">Chọn ngày khác để xem lịch tiếp đón</p>
                                    </div>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

</div>

<jsp:include page="/WEB-INF/views/components/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
