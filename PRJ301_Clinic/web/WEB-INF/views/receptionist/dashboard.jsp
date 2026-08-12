<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Sảnh Lễ Tân | PRJ301 Clinic Reception</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
    <style>
        /* ── RECEPTIONIST DASHBOARD STYLES ── */
        .reception-hero {
            background: linear-gradient(135deg,
                rgba(16, 185, 129, 0.15) 0%,
                rgba(6, 182, 212, 0.10) 50%,
                rgba(15, 23, 42, 0.05) 100%);
            border: 1px solid rgba(16, 185, 129, 0.25);
            border-radius: 20px;
            padding: 2rem;
            position: relative;
            overflow: hidden;
            backdrop-filter: blur(20px);
        }
        .reception-hero::before {
            content: '';
            position: absolute;
            top: -60px; right: -60px;
            width: 200px; height: 200px;
            background: radial-gradient(circle, rgba(16,185,129,0.12) 0%, transparent 70%);
            border-radius: 50%;
        }
        .reception-hero::after {
            content: '';
            position: absolute;
            bottom: -40px; left: -40px;
            width: 150px; height: 150px;
            background: radial-gradient(circle, rgba(6,182,212,0.10) 0%, transparent 70%);
            border-radius: 50%;
        }
        .hero-avatar {
            width: 68px; height: 68px;
            border-radius: 18px;
            background: linear-gradient(135deg, #10b981, #06b6d4);
            display: flex; align-items: center; justify-content: center;
            font-size: 1.8rem;
            box-shadow: 0 8px 24px rgba(16,185,129,0.35);
            flex-shrink: 0;
        }
        /* ── STAT CARDS ── */
        .stat-card {
            border-radius: 16px;
            padding: 1.4rem 1.6rem;
            display: flex; align-items: center; gap: 1.1rem;
            position: relative; overflow: hidden;
            transition: transform .2s, box-shadow .2s;
            cursor: default;
        }
        .stat-card:hover { transform: translateY(-3px); box-shadow: 0 12px 32px rgba(0,0,0,.3); }
        .stat-card::after {
            content: '';
            position: absolute; top: 0; right: 0;
            width: 80px; height: 80px;
            border-radius: 50%;
            opacity: .08;
        }
        .stat-card.cyan  { background: rgba(6,182,212,.12);  border: 1px solid rgba(6,182,212,.3); }
        .stat-card.cyan::after  { background: #06b6d4; }
        .stat-card.green { background: rgba(16,185,129,.12); border: 1px solid rgba(16,185,129,.3); }
        .stat-card.green::after { background: #10b981; }
        .stat-card.amber { background: rgba(245,158,11,.12); border: 1px solid rgba(245,158,11,.3); }
        .stat-card.amber::after { background: #f59e0b; }
        .stat-card.blue  { background: rgba(99,102,241,.12); border: 1px solid rgba(99,102,241,.3); }
        .stat-card.blue::after  { background: #6366f1; }
        .stat-icon {
            width: 52px; height: 52px; border-radius: 14px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.4rem; flex-shrink: 0;
        }
        .stat-card.cyan  .stat-icon { background: rgba(6,182,212,.2);  color: #06b6d4; }
        .stat-card.green .stat-icon { background: rgba(16,185,129,.2); color: #10b981; }
        .stat-card.amber .stat-icon { background: rgba(245,158,11,.2); color: #f59e0b; }
        .stat-card.blue  .stat-icon { background: rgba(99,102,241,.2); color: #818cf8; }
        .stat-value { font-size: 1.9rem; font-weight: 800; line-height: 1; color: #fff; }
        .stat-label { font-size: .78rem; color: rgba(255,255,255,.55); margin-top: .25rem; text-transform: uppercase; letter-spacing: .05em; }
        /* ── TABLE PANEL ── */
        .panel {
            background: rgba(255,255,255,.04);
            border: 1px solid rgba(255,255,255,.1);
            border-radius: 20px;
            backdrop-filter: blur(16px);
            overflow: hidden;
        }
        .panel-header {
            padding: 1.2rem 1.6rem;
            border-bottom: 1px solid rgba(255,255,255,.08);
            display: flex; align-items: center; justify-content: space-between;
            background: rgba(16,185,129,.06);
        }
        .panel-title { font-weight: 700; font-size: 1rem; color: #fff; display:flex; align-items:center; gap:.6rem; }
        .panel-title i { color: #10b981; }
        .tbl { width: 100%; border-collapse: collapse; }
        .tbl thead th {
            padding: .85rem 1rem;
            font-size: .72rem; font-weight: 700; letter-spacing: .08em;
            text-transform: uppercase; color: rgba(255,255,255,.45);
            border-bottom: 1px solid rgba(255,255,255,.07);
            white-space: nowrap;
        }
        .tbl tbody tr {
            border-bottom: 1px solid rgba(255,255,255,.05);
            transition: background .15s;
        }
        .tbl tbody tr:last-child { border-bottom: none; }
        .tbl tbody tr:hover { background: rgba(255,255,255,.04); }
        .tbl tbody td { padding: 1rem; vertical-align: middle; }
        /* ── TIME BUBBLE ── */
        .time-bubble {
            display: inline-flex; align-items: center; gap: .4rem;
            background: rgba(6,182,212,.15);
            border: 1px solid rgba(6,182,212,.3);
            color: #67e8f9;
            padding: .3rem .75rem;
            border-radius: 30px;
            font-weight: 700; font-size: .85rem; white-space: nowrap;
        }
        /* ── PATIENT NAME ── */
        .patient-name { font-weight: 700; color: #f1f5f9; font-size: .92rem; }
        .patient-phone { font-size: .78rem; color: rgba(255,255,255,.4); margin-top: .15rem; }
        /* ── DOCTOR CHIP ── */
        .doctor-chip {
            display: inline-flex; align-items: center; gap: .4rem;
            background: rgba(99,102,241,.15);
            border: 1px solid rgba(99,102,241,.3);
            color: #a5b4fc;
            padding: .28rem .7rem; border-radius: 20px;
            font-size: .8rem; font-weight: 600;
        }
        /* ── SERVICE CHIP ── */
        .service-chip {
            display: inline-block;
            background: rgba(59,130,246,.12);
            border: 1px solid rgba(59,130,246,.25);
            color: #93c5fd;
            padding: .25rem .7rem; border-radius: 20px;
            font-size: .78rem;
        }
        /* ── STATUS BADGES ── */
        .badge-paid    { background: rgba(16,185,129,.15); border: 1px solid rgba(16,185,129,.35); color: #6ee7b7; padding: .3rem .8rem; border-radius: 20px; font-size: .78rem; font-weight: 600; display:inline-flex; align-items:center; gap:.35rem; }
        .badge-unpaid  { background: rgba(245,158,11,.15); border: 1px solid rgba(245,158,11,.35); color: #fcd34d; padding: .3rem .8rem; border-radius: 20px; font-size: .78rem; font-weight: 600; display:inline-flex; align-items:center; gap:.35rem; }
        .badge-completed { background: rgba(16,185,129,.2); border: 1px solid rgba(16,185,129,.4); color: #34d399; padding: .28rem .75rem; border-radius: 20px; font-size: .78rem; font-weight: 700; display:inline-flex; align-items:center; gap:.35rem; }
        .badge-confirmed { background: rgba(6,182,212,.15); border: 1px solid rgba(6,182,212,.35); color: #67e8f9; padding: .28rem .75rem; border-radius: 20px; font-size: .78rem; font-weight: 700; display:inline-flex; align-items:center; gap:.35rem; }
        .badge-pending   { background: rgba(245,158,11,.15); border: 1px solid rgba(245,158,11,.35); color: #fcd34d; padding: .28rem .75rem; border-radius: 20px; font-size: .78rem; font-weight: 700; display:inline-flex; align-items:center; gap:.35rem; }
        .badge-cancelled { background: rgba(239,68,68,.15); border: 1px solid rgba(239,68,68,.35); color: #fca5a5; padding: .28rem .75rem; border-radius: 20px; font-size: .78rem; font-weight: 700; display:inline-flex; align-items:center; gap:.35rem; }
        /* ── ACTION BUTTONS ── */
        .btn-checkin {
            display: inline-flex; align-items: center; gap: .35rem;
            background: rgba(6,182,212,.2); border: 1px solid rgba(6,182,212,.4);
            color: #67e8f9; border-radius: 10px;
            padding: .38rem .85rem; font-size: .8rem; font-weight: 700;
            cursor: pointer; transition: all .2s; white-space: nowrap;
        }
        .btn-checkin:hover { background: rgba(6,182,212,.35); transform: scale(1.04); color: #fff; }
        .btn-collect {
            display: inline-flex; align-items: center; gap: .35rem;
            background: rgba(16,185,129,.2); border: 1px solid rgba(16,185,129,.4);
            color: #6ee7b7; border-radius: 10px;
            padding: .38rem .85rem; font-size: .8rem; font-weight: 700;
            cursor: pointer; transition: all .2s; white-space: nowrap;
        }
        .btn-collect:hover { background: rgba(16,185,129,.35); transform: scale(1.04); color: #fff; }
        .btn-cancel {
            display: inline-flex; align-items: center; gap: .35rem;
            background: rgba(239,68,68,.12); border: 1px solid rgba(239,68,68,.3);
            color: #fca5a5; border-radius: 10px;
            padding: .38rem .65rem; font-size: .8rem; font-weight: 700;
            cursor: pointer; transition: all .2s;
        }
        .btn-cancel:hover { background: rgba(239,68,68,.28); transform: scale(1.04); color: #fff; }
        /* ── DATEPICKER OVERRIDE ── */
        .filter-bar { display: flex; align-items: center; gap: .75rem; }
        .filter-input {
            background: rgba(255,255,255,.07) !important;
            border: 1px solid rgba(255,255,255,.15) !important;
            color: #fff !important; border-radius: 12px !important;
            padding: .5rem 1rem .5rem 2.4rem !important;
            font-size: .88rem; width: 175px;
        }
        .filter-input::placeholder { color: rgba(255,255,255,.35) !important; }
        .btn-filter {
            background: linear-gradient(135deg, #10b981, #06b6d4);
            border: none; border-radius: 12px;
            color: #fff; font-weight: 700; font-size: .85rem;
            padding: .5rem 1.1rem; cursor: pointer; transition: opacity .2s;
            display: inline-flex; align-items: center; gap: .4rem;
        }
        .btn-filter:hover { opacity: .85; }
        /* ── EMPTY STATE ── */
        .empty-state { text-align: center; padding: 4rem 2rem; color: rgba(255,255,255,.3); }
        .empty-state i { font-size: 3rem; margin-bottom: 1rem; display: block; opacity: .4; }
        /* ── PULSE DOT ── */
        .live-dot {
            width: 9px; height: 9px; border-radius: 50%;
            background: #10b981;
            box-shadow: 0 0 0 0 rgba(16,185,129,.5);
            animation: livepulse 1.5s infinite;
            flex-shrink: 0;
        }
        @keyframes livepulse {
            0%   { box-shadow: 0 0 0 0 rgba(16,185,129,.6); }
            70%  { box-shadow: 0 0 0 8px rgba(16,185,129,0); }
            100% { box-shadow: 0 0 0 0 rgba(16,185,129,0); }
        }
        /* ── FLATPICKR INPUT ICON ── */
        .filter-wrap { position: relative; }
        .filter-wrap .fi { position: absolute; left: .8rem; top: 50%; transform: translateY(-50%); color: #10b981; font-size: .85rem; pointer-events: none; z-index: 2; }
    </style>
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
