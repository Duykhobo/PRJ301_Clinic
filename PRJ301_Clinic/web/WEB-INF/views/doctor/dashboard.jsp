<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <title>Không Gian Bác Sĩ | PRJ301 Clinic Workspace</title>
                <jsp:include page="/WEB-INF/views/components/head.jsp" />

            </head>

            <body>
                <%-- 1. IMPORT SIDEBAR COMPONENT (Bao gồm sidebar, topbar và thẻ mở div.ws-main) --%>
                    <jsp:include page="/WEB-INF/views/components/sidebar-doctor.jsp" />

                    <%-- 2. NỘI DUNG CHÍNH CỦA DASHBOARD (Nằm gọn trong ws-main) --%>
                        <div class="container-fluid px-4 py-4 flex-grow-1">

                            <%-- ── HERO BANNER ── --%>
                                <div class="doctor-hero mb-4 animate-fade-in">
                                    <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
                                        <div class="d-flex align-items-center gap-3">
                                            <div class="hero-avatar">
                                                <i class="fa-solid fa-user-doctor text-white"></i>
                                            </div>
                                            <div>
                                                <div class="d-flex align-items-center gap-2 mb-1">
                                                    <div class="live-dot"></div>
                                                    <span
                                                        style="font-size:.75rem; color:#60a5fa; font-weight:700; letter-spacing:.06em; text-transform:uppercase;">CLINIC
                                                        WORKSPACE</span>
                                                </div>
                                                <h4 class="fw-bold text-white mb-1" style="font-size:1.3rem;">
                                                    Xin chào, BS. ${sessionScope.LOGIN_USER.fullname}
                                                </h4>
                                                <p class="mb-0" style="color:rgba(255,255,255,.5); font-size:.83rem;">
                                                    <i class="fa-solid fa-stethoscope me-1"
                                                        style="color:#60a5fa;"></i>Chuyên khoa răng hàm mặt &amp; Da
                                                    liễu &nbsp;·&nbsp;
                                                    <i class="fa-solid fa-calendar-day me-1"
                                                        style="color:#a78bfa;"></i>${selectedDate}
                                                </p>
                                            </div>
                                        </div>

                                        <%-- DATE FILTER --%>
                                            <form action="${pageContext.request.contextPath}/doctor/dashboard"
                                                method="GET" class="filter-bar">
                                                <div class="filter-wrap">
                                                    <i class="fa-solid fa-calendar-days fi"></i>
                                                    <input type="text" name="date" class="filter-input flatpickr-date"
                                                        value="${selectedDate}" placeholder="Chọn ngày"
                                                        autocomplete="off">
                                                </div>
                                                <button type="submit" class="btn-filter">
                                                    <i class="fa-solid fa-filter"></i> Lọc
                                                </button>
                                            </form>
                                    </div>
                                </div>

                                <%-- ── STAT CARDS ── --%>
                                    <div class="row g-3 mb-4 animate-fade-in">
                                        <div class="col-12 col-sm-6 col-lg-3">
                                            <div class="stat-card blue h-100">
                                                <div class="stat-icon"><i class="fa-solid fa-list-check"></i></div>
                                                <div>
                                                    <div class="stat-value">${not empty totalCount ? totalCount : '0'}
                                                    </div>
                                                    <div class="stat-label">Tổng ca khám</div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-12 col-sm-6 col-lg-3">
                                            <div class="stat-card amber h-100">
                                                <div class="stat-icon"><i class="fa-solid fa-hourglass-half"></i></div>
                                                <div>
                                                    <div class="stat-value">${not empty pendingCount ? pendingCount :
                                                        '0'}</div>
                                                    <div class="stat-label">Bệnh nhân chờ</div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-12 col-sm-6 col-lg-3">
                                            <div class="stat-card green h-100">
                                                <div class="stat-icon"><i class="fa-solid fa-circle-check"></i></div>
                                                <div>
                                                    <div class="stat-value">${not empty completedCount ? completedCount
                                                        : '0'}</div>
                                                    <div class="stat-label">Đã hoàn tất</div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-12 col-sm-6 col-lg-3">
                                            <div class="stat-card red h-100">
                                                <div class="stat-icon"><i class="fa-solid fa-ban"></i></div>
                                                <div>
                                                    <div class="stat-value">${not empty cancelledCount ? cancelledCount
                                                        : '0'}</div>
                                                    <div class="stat-label">Ca bị hủy</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <%-- ── APPOINTMENTS TABLE ── --%>
                                        <div class="panel animate-fade-in mb-4">
                                            <div class="panel-header">
                                                <div class="panel-title">
                                                    <i class="fa-solid fa-bed-pulse"></i>
                                                    Lịch Khám Ngày <span
                                                        style="color:#60a5fa; margin-left:.4rem;">${selectedDate}</span>
                                                </div>
                                            </div>

                                            <div style="overflow-x:auto;">
                                                <table class="tbl">
                                                    <thead>
                                                        <tr>
                                                            <th style="padding-left:1.4rem;">Giờ Khám</th>
                                                            <th>Bệnh Nhân</th>
                                                            <th>Dịch Vụ Khám</th>
                                                            <th>Thanh Toán</th>
                                                            <th>Trạng Thái</th>
                                                            <th style="text-align:right; padding-right:1.4rem;">Thao Tác
                                                            </th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:choose>
                                                            <c:when test="${not empty appointments}">
                                                                <c:forEach var="app" items="${appointments}">
                                                                    <tr>
                                                                        <td style="padding-left:1.4rem;">
                                                                            <span class="time-bubble">
                                                                                <i
                                                                                    class="fa-solid fa-clock"></i>${app.startTime}
                                                                            </span>
                                                                        </td>
                                                                        <td>
                                                                            <div class="patient-name">${app.patientName}
                                                                            </div>
                                                                            <div class="patient-phone"><i
                                                                                    class="fa-solid fa-phone"
                                                                                    style="font-size:.7rem;"></i>
                                                                                ${app.patientPhone}</div>
                                                                        </td>
                                                                        <td>
                                                                            <span
                                                                                class="service-chip">${app.serviceName}</span>
                                                                        </td>
                                                                        <td>
                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${app.paymentStatus == 'PAID'}">
                                                                                    <span class="badge-paid"><i
                                                                                            class="fa-solid fa-check-circle"></i>Đã
                                                                                        Thanh Toán</span>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <span class="badge-unpaid"><i
                                                                                            class="fa-solid fa-hourglass-half"></i>Chưa
                                                                                        Thu</span>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </td>
                                                                        <td>
                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${app.status == 'COMPLETED'}">
                                                                                    <span class="badge-completed"><i
                                                                                            class="fa-solid fa-circle-check"></i>Khám
                                                                                        Xong</span>
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${app.status == 'CONFIRMED'}">
                                                                                    <span class="badge-confirmed"><i
                                                                                            class="fa-solid fa-user-check"></i>Đã
                                                                                        Tiếp Nhận</span>
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${app.status == 'CANCELLED'}">
                                                                                    <span class="badge-cancelled"><i
                                                                                            class="fa-solid fa-ban"></i>Đã
                                                                                        Hủy</span>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <span class="badge-pending"><i
                                                                                            class="fa-solid fa-spinner fa-spin"></i>Chờ
                                                                                        Khám</span>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </td>
                                                                        <td
                                                                            style="text-align:right; padding-right:1.4rem;">
                                                                            <button type="button" class="btn-examine"
                                                                                onclick="openMedicalModal(${app.id}, '${app.patientName}', '${app.serviceName}', '${recordsMap[app.id].diagnosis}', '${recordsMap[app.id].prescriptionOrResult}')">
                                                                                <i
                                                                                    class="fa-solid fa-notes-medical"></i>
                                                                                Khám & Kê Đơn
                                                                            </button>
                                                                        </td>
                                                                    </tr>
                                                                </c:forEach>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <tr>
                                                                    <td colspan="6">
                                                                        <div class="empty-state">
                                                                            <i class="fa-solid fa-bed-empty"></i>
                                                                            <p
                                                                                style="font-size:1rem; font-weight:600; color:rgba(255,255,255,.4);">
                                                                                Không có ca khám nào trong ngày
                                                                                ${selectedDate}</p>
                                                                        </div>
                                                                    </td>
                                                                </tr>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>

                                        <%-- ── MEDICAL MODAL (GLASSMORPHISM STYLE) ── --%>
                                            <div class="modal fade" id="medicalModal" tabindex="-1" aria-hidden="true">
                                                <div class="modal-dialog modal-dialog-centered modal-lg">
                                                    <div class="modal-content"
                                                        style="background: rgba(15,23,42,0.8); backdrop-filter: blur(25px); border: 1px solid rgba(59,130,246,0.3); border-radius: 20px;">
                                                        <div class="modal-header border-bottom border-secondary"
                                                            style="border-color: rgba(255,255,255,0.1) !important;">
                                                            <h5
                                                                class="modal-title fw-bold text-white d-flex align-items-center gap-2">
                                                                <i class="fa-solid fa-stethoscope"
                                                                    style="color:#60a5fa;"></i> Hồ Sơ Khám Bệnh & Kê Đơn
                                                            </h5>
                                                            <button type="button" class="btn-close btn-close-white"
                                                                data-bs-dismiss="modal" aria-label="Close"></button>
                                                        </div>
                                                        <form
                                                            action="${pageContext.request.contextPath}/doctor/dashboard"
                                                            method="POST">
                                                            <input type="hidden" name="action" value="save-diagnosis">
                                                            <input type="hidden" name="appointmentId"
                                                                id="modalAppointmentId">
                                                            <input type="hidden" name="date" value="${selectedDate}">

                                                            <div class="modal-body space-y-3 p-4">
                                                                <div class="p-3 rounded-3 mb-4"
                                                                    style="background: rgba(59,130,246,0.1); border: 1px solid rgba(59,130,246,0.2);">
                                                                    <div class="row g-2">
                                                                        <div class="col-md-6">
                                                                            <span class="text-muted small">Bệnh
                                                                                nhân:</span>
                                                                            <span class="fw-bold text-white ms-2"
                                                                                id="modalPatientName"></span>
                                                                        </div>
                                                                        <div class="col-md-6">
                                                                            <span class="text-muted small">Dịch
                                                                                vụ:</span>
                                                                            <span class="fw-bold ms-2"
                                                                                style="color:#93c5fd;"
                                                                                id="modalServiceName"></span>
                                                                        </div>
                                                                    </div>
                                                                </div>

                                                                <div class="mb-4">
                                                                    <label class="form-label fw-bold"
                                                                        style="color:#60a5fa;">
                                                                        <i class="fa-solid fa-heart-pulse me-1"></i>Chẩn
                                                                        Đoán Bệnh Lý:
                                                                    </label>
                                                                    <textarea name="diagnosis" id="modalDiagnosis"
                                                                        class="form-control" rows="3"
                                                                        style="background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #fff; border-radius: 12px;"
                                                                        placeholder="Nhập chi tiết chẩn đoán y khoa..."
                                                                        required></textarea>
                                                                </div>

                                                                <div class="mb-2">
                                                                    <label class="form-label fw-bold"
                                                                        style="color:#fcd34d;">
                                                                        <i class="fa-solid fa-pills me-1"></i>Chỉ Định &
                                                                        Đơn Thuốc:
                                                                    </label>
                                                                    <textarea name="prescription" id="modalPrescription"
                                                                        class="form-control" rows="4"
                                                                        style="background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #fff; border-radius: 12px;"
                                                                        placeholder="Nhập đơn thuốc và lời dặn..."
                                                                        required></textarea>
                                                                </div>
                                                            </div>

                                                            <div class="modal-footer"
                                                                style="border-top: 1px solid rgba(255,255,255,0.1);">
                                                                <button type="button" class="btn"
                                                                    data-bs-dismiss="modal"
                                                                    style="color:rgba(255,255,255,0.6);">Hủy Bỏ</button>
                                                                <button type="submit" class="btn-examine px-4">
                                                                    <i class="fa-solid fa-floppy-disk me-1"></i>Lưu Hồ
                                                                    Sơ Khám
                                                                </button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>

                                            <%-- Include Footer (Nằm gọn trong ws-main) --%>
                                                <jsp:include page="/WEB-INF/views/components/footer.jsp" />

                        </div>
                        <%-- 3. ĐÓNG THẺ DIV CỦA WS-MAIN TỪ SIDEBAR --%>

                            <script
                                src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                            <script>
                                function openMedicalModal(id, patientName, serviceName, diagnosis, prescription) {
                                    document.getElementById('modalAppointmentId').value = id;
                                    document.getElementById('modalPatientName').innerText = patientName;
                                    document.getElementById('modalServiceName').innerText = serviceName;
                                    document.getElementById('modalDiagnosis').value = diagnosis || '';
                                    document.getElementById('modalPrescription').value = prescription || '';

                                    const modal = new bootstrap.Modal(document.getElementById('medicalModal'));
                                    modal.show();
                                }
                            </script>
            </body>

            </html>