<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <title>Lịch Sử Khám &amp; Liệu Trình | Phòng Khám &amp; Spa PRJ301</title>
        <jsp:include page="/WEB-INF/views/components/head.jsp" />
        <style>
            /* CSS ép màu tương phản hiển thị chữ sắc nét cho các Badge trạng thái */
            .badge-unpaid {
                background-color: rgba(245, 158, 11, 0.15) !important;
                color: #fbbf24 !important;
                border: 1px solid rgba(251, 191, 36, 0.4) !important;
            }
            .badge-paid {
                background-color: rgba(16, 185, 129, 0.15) !important;
                color: #34d399 !important;
                border: 1px solid rgba(52, 211, 153, 0.4) !important;
            }
        </style>
    </head>

    <body class="d-flex flex-column min-vh-100">

        <%-- Dynamic Navbar Component --%>
        <jsp:include page="/WEB-INF/views/components/navbar.jsp" />

        <div class="container my-4 flex-grow-1">
            <%-- Header Banner Glassmorphism --%>
            <div class="glass-card p-4 mb-4 animate-fade-in">
                <div class="row align-items-center g-3">
                    <div class="col-md-8 d-flex align-items-center gap-3">
                        <div class="avatar-circle text-white fs-2 fw-bold border border-cyan border-2 shadow-sm"
                             style="width: 65px; height: 65px; display: flex; align-items: center; justify-content: center; border-radius: 50%; background: linear-gradient(135deg, #0ea5e9, #10b981);">
                            <i class="fa-solid fa-clock-rotate-left"></i>
                        </div>
                        <div>
                            <h3 class="fw-bold text-white mb-1">
                                Nhật Ký Khám Bệnh & Hồ Sơ Y Tế
                            </h3>
                            <p class="text-cyan mb-0 small">
                                <i class="fa-solid fa-user-check me-1"></i>Bệnh nhân:
                                <strong><c:out value="${sessionScope.LOGIN_USER.fullname}"/></strong> |
                                <i class="fa-solid fa-receipt ms-2 me-1"></i>Tổng số lượt khám: ${totalItems} cuộc hẹn
                            </p>
                        </div>
                    </div>

                    <div class="col-md-4 text-md-end">
                        <a href="${pageContext.request.contextPath}/MainController?action=booking-page"
                           class="btn btn-primary-gradient px-4 py-2 fw-bold rounded-pill">
                            <i class="fa-solid fa-calendar-plus me-2"></i>Đặt Lịch Khám Mới
                        </a>
                    </div>
                </div>
            </div>

            <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

            <%-- SPA & CLINIC: ACTIVE TREATMENT PACKAGES TRACKING CARD --%>
            <c:if test="${not empty activePackages}">
                <div class="glass-card p-4 mb-4 animate-fade-in" style="background: rgba(15, 23, 42, 0.85); border: 1px solid rgba(245, 158, 11, 0.35);">
                    <div class="d-flex align-items-center justify-content-between flex-wrap gap-2 mb-3">
                        <h5 class="fw-bold text-warning mb-0 d-flex align-items-center gap-2">
                            <i class="fa-solid fa-wand-magic-sparkles text-warning"></i> Gói Liệu Trình Làm Đẹp &amp; Trị Liệu Da Đang Hoạt Động
                        </h5>
                        <span class="badge px-3 py-1.5 rounded-pill fw-bold" style="background: rgba(245, 158, 11, 0.15); color: #fbbf24; border: 1px solid rgba(245, 158, 11, 0.4); font-size: 0.82rem;">
                            <i class="fa-solid fa-award me-1"></i>${activePackages.size()} Gói Đang Hoạt Động
                        </span>
                    </div>

                    <div class="row g-3">
                        <c:forEach var="pkg" items="${activePackages}">
                            <div class="col-12 col-md-6">
                                <div class="p-3 rounded-3 border border-secondary border-opacity-30" style="background: rgba(255, 255, 255, 0.03);">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <strong class="text-white"><i class="fa-solid fa-spa text-cyan me-2"></i><c:out value="${pkg.packageName}"/></strong>
                                        <c:choose>
                                            <c:when test="${pkg.progressPercent >= 100}">
                                                <span class="badge px-2.5 py-1 rounded-pill fw-semibold" style="background: rgba(16, 185, 129, 0.18); color: #34d399; border: 1px solid rgba(16, 185, 129, 0.35); font-size: 0.75rem;">
                                                    <i class="fa-solid fa-circle-check me-1"></i>Hoàn Thành
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge px-2.5 py-1 rounded-pill fw-semibold" style="background: rgba(14, 165, 233, 0.18); color: #38bdf8; border: 1px solid rgba(14, 165, 233, 0.35); font-size: 0.75rem;">
                                                    <i class="fa-solid fa-spinner fa-spin-pulse me-1"></i><c:out value="${pkg.status}"/>
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="d-flex justify-content-between small text-white-50 mb-1">
                                        <span>Tiến độ: <strong class="text-cyan">${pkg.completedSessions} / ${pkg.totalSessions} buổi</strong> (${pkg.progressPercent}%)</span>
                                        <span>Còn lại: <strong class="text-warning">${pkg.remainingSessions} buổi</strong></span>
                                    </div>
                                    <div class="progress" style="height: 8px; background: rgba(255,255,255,0.1); border-radius: 10px;">
                                        <div class="progress-bar bg-primary-gradient progress-bar-striped progress-bar-animated" role="progressbar" style="width: ${pkg.progressPercent}%; border-radius: 10px;"></div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <%-- History Table Glassmorphism --%>
            <div class="glass-card p-4 animate-fade-in mb-4">
                <div class="table-responsive">
                    <table class="table table-dark table-hover align-middle mb-0">
                        <thead>
                            <tr class="text-cyan border-bottom border-secondary border-opacity-25">
                                <th>Mã Ca</th>
                                <th>Ngày & Giờ Khám</th>
                                <th>Bác Sĩ Đảm Nhận</th>
                                <th>Dịch Vụ Kê Khai</th>
                                <th>Tổng Tiền</th>
                                <th>Thanh Toán VietQR</th>
                                <th>Trạng Thái Ca Khám</th>
                                <th class="text-end">Hành Động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty historyList}">
                                    <c:forEach var="app" items="${historyList}">
                                        <tr>
                                            <td class="fw-bold text-white">#${app.id}</td>
                                            <td>
                                                <div class="fw-bold text-cyan"><i class="fa-solid fa-calendar-day me-1"></i>${app.appointmentDate}</div>
                                                <div class="small text-muted"><i class="fa-solid fa-clock me-1"></i>${app.startTime}</div>
                                            </td>
                                            <td class="text-white">
                                                <i class="fa-solid fa-user-doctor me-1 text-cyan"></i><c:out value="${app.doctorName}"/>
                                            </td>
                                            <td>
                                                <span class="badge px-3 py-2 rounded-pill fw-bold"
                                                      style="background: rgba(14, 165, 233, 0.15); color: #38bdf8 !important; border: 1px solid rgba(56, 189, 248, 0.4);">
                                                    <i class="fa-solid fa-notes-medical me-1"></i><c:out value="${app.serviceName}"/>
                                                </span>
                                            </td>
                                            <%-- Định dạng số tiền 2,000 VNĐ --%>
                                            <td class="fw-bold text-warning">
                                                <fmt:formatNumber value="${app.totalPrice}" pattern="#,##0"/> VNĐ
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${app.paymentStatus == 'PAID'}">
                                                        <span class="badge badge-paid px-3 py-2 rounded-pill fw-bold">
                                                            <i class="fa-solid fa-circle-check me-1"></i>Đã Thanh Toán
                                                            <c:if test="${not empty app.paymentMethod}">(${app.paymentMethod})</c:if>
                                                            </span>
                                                    </c:when>
                                                    <c:when test="${app.paymentStatus == 'REFUND_PENDING'}">
                                                        <span class="badge bg-info bg-opacity-25 border border-info border-opacity-40 text-cyan px-3 py-2 rounded-pill fw-bold">
                                                            <i class="fa-solid fa-hand-holding-dollar me-1"></i>Chờ Hoàn Tiền
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${app.paymentStatus == 'REFUNDED'}">
                                                        <span class="badge bg-primary bg-opacity-25 border border-primary border-opacity-40 text-cyan px-3 py-2 rounded-pill fw-bold">
                                                            <i class="fa-solid fa-rotate-left me-1"></i>Đã Hoàn Tiền
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${app.status == 'CANCELLED'}">
                                                        <span class="badge bg-secondary bg-opacity-25 border border-secondary border-opacity-40 text-white-50 px-3 py-2 rounded-pill fw-bold">
                                                            <i class="fa-solid fa-ban me-1"></i>Đã Hủy - Không Cần TT
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <%-- Sửa class CSS giúp hiển thị rõ chữ 'Chưa Thanh Toán' --%>
                                                        <span class="badge badge-unpaid px-3 py-2 rounded-pill fw-bold">
                                                            <i class="fa-solid fa-clock me-1"></i>Chưa Thanh Toán
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${app.status == 'COMPLETED'}">
                                                        <span class="badge bg-success bg-opacity-25 border border-success border-opacity-40 text-emerald px-3 py-2 rounded-pill fw-bold">
                                                            <i class="fa-solid fa-circle-check me-1"></i>Hoàn Tất Khám
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${app.status == 'CONFIRMED'}">
                                                        <span class="badge bg-info bg-opacity-25 border border-info border-opacity-40 text-cyan px-3 py-2 rounded-pill fw-bold">
                                                            <i class="fa-solid fa-user-check me-1"></i>Đã Tiếp Nhận
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${app.status == 'CANCELLED'}">
                                                        <span class="badge bg-danger bg-opacity-25 border border-danger border-opacity-40 text-danger px-3 py-2 rounded-pill fw-bold">
                                                            <i class="fa-solid fa-ban me-1"></i>Đã Hủy
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-warning bg-opacity-25 border border-warning border-opacity-40 text-warning px-3 py-2 rounded-pill fw-bold">
                                                            <i class="fa-solid fa-spinner fa-spin me-1"></i>Chờ Khám
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-end">
                                                <div class="d-flex gap-1 justify-content-end">
                                                    <c:if test="${app.paymentStatus == 'UNPAID' && app.status != 'CANCELLED'}">
                                                        <a href="${pageContext.request.contextPath}/booking?action=payment&id=${app.id}"
                                                           class="btn btn-sm btn-primary-gradient rounded-pill px-3 fw-bold">
                                                            <i class="fa-solid fa-qrcode me-1"></i>Thanh Toán QR
                                                        </a>
                                                    </c:if>

                                                    <c:if test="${app.status == 'COMPLETED' && not empty recordsMap[app.id]}">
                                                        <button type="button"
                                                                class="btn btn-sm btn-outline-glass rounded-pill px-3 fw-bold"
                                                                onclick="viewPatientPrescription('#${app.id}', '${app.doctorName}', '${app.serviceName}', '${recordsMap[app.id].diagnosis}', '${recordsMap[app.id].prescriptionOrResult}')">
                                                            <i class="fa-solid fa-file-medical me-1 text-cyan"></i>Xem Đơn Thuốc
                                                        </button>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="8" class="text-center py-5 text-muted">
                                            <i class="fa-solid fa-calendar-xmark fs-1 mb-3 d-block text-secondary"></i>
                                            Bạn chưa có cuộc hẹn khám nào.
                                            <a href="${pageContext.request.contextPath}/MainController?action=booking-page"
                                               class="text-cyan fw-bold ms-1">Đặt lịch ngay</a>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

            <%-- Glassmorphism Pagination Component --%>
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation" class="animate-fade-in">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link bg-dark text-white border-secondary"
                               href="${pageContext.request.contextPath}/history?page=${currentPage - 1}">
                                <i class="fa-solid fa-chevron-left"></i> Trang trước
                            </a>
                        </li>

                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link ${currentPage == i ? 'btn-primary-gradient text-white border-0' : 'bg-dark text-cyan border-secondary'}"
                                   href="${pageContext.request.contextPath}/history?page=${i}">
                                    ${i}
                                </a>
                            </li>
                        </c:forEach>

                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link bg-dark text-white border-secondary"
                               href="${pageContext.request.contextPath}/history?page=${currentPage + 1}">
                                Trang sau <i class="fa-solid fa-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </div>

        <%-- Glassmorphism Modal Xem Đơn Thuốc Bệnh Nhân --%>
        <div class="modal fade" id="patientPrescriptionModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg">
                <div class="modal-content glass-card border border-cyan border-opacity-30 text-white">
                    <div class="modal-header border-bottom border-secondary border-opacity-25">
                        <h5 class="modal-title fw-bold text-cyan d-flex align-items-center gap-2">
                            <i class="fa-solid fa-file-prescription"></i> Kết Quả Chẩn Đoán & Đơn Thuốc Y Khoa
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="p-3 rounded-3 mb-3" style="background: rgba(255, 255, 255, 0.05);">
                            <div class="row g-2">
                                <div class="col-md-4">
                                    <span class="text-muted small">Mã ca khám:</span>
                                    <span class="fw-bold text-white ms-1" id="pModalAppId"></span>
                                </div>
                                <div class="col-md-4">
                                    <span class="text-muted small">Bác sĩ phụ trách:</span>
                                    <span class="fw-bold text-cyan ms-1" id="pModalDoctor"></span>
                                </div>
                                <div class="col-md-4">
                                    <span class="text-muted small">Dịch vụ:</span>
                                    <span class="fw-bold text-warning ms-1" id="pModalService"></span>
                                </div>
                            </div>
                        </div>

                        <%-- SPA & CLINIC: SKIN ASSESSMENT INDICATORS --%>
                        <div class="p-3 rounded-3 mb-3" style="background: rgba(255, 255, 255, 0.03); border: 1px dashed rgba(56, 189, 248, 0.35);">
                            <div class="small fw-bold text-warning mb-2 d-flex align-items-center gap-1">
                                <i class="fa-solid fa-wand-magic-sparkles text-warning"></i> Chỉ Số Sức Khỏe Làn Da Buổi Khám (Dermatology Metrics):
                            </div>
                            <div class="d-flex flex-wrap gap-2">
                                <div class="px-3 py-2 rounded-pill bg-dark border border-secondary border-opacity-40 d-flex align-items-center gap-2">
                                    <i class="fa-solid fa-droplet text-cyan"></i>
                                    <span class="small text-white-50">Độ ẩm da:</span>
                                    <strong id="pModalMoisture" class="text-cyan">58%</strong>
                                </div>
                                <div class="px-3 py-2 rounded-pill bg-dark border border-secondary border-opacity-40 d-flex align-items-center gap-2">
                                    <i class="fa-solid fa-fire-flame-simple text-warning"></i>
                                    <span class="small text-white-50">Độ tiết dầu:</span>
                                    <strong id="pModalOil" class="text-warning">48%</strong>
                                </div>
                                <div class="px-3 py-2 rounded-pill bg-dark border border-secondary border-opacity-40 d-flex align-items-center gap-2">
                                    <i class="fa-solid fa-dna text-emerald"></i>
                                    <span class="small text-white-50">Tình trạng:</span>
                                    <strong id="pModalPigmentation" class="text-emerald">Level 1 - Khỏe mạnh</strong>
                                </div>
                            </div>
                        </div>

                        <div class="mb-4">
                            <h6 class="fw-bold text-cyan mb-2"><i class="fa-solid fa-stethoscope me-2"></i>Chẩn Đoán Bệnh Lý / Tình Trạng Da:</h6>
                            <div class="p-3 rounded-3 bg-dark border border-secondary text-white-50" id="pModalDiagnosis" style="white-space: pre-line;"></div>
                        </div>

                        <div class="mb-3">
                            <h6 class="fw-bold text-warning mb-2"><i class="fa-solid fa-pills me-2"></i>Đơn Thuốc, Phác Đồ Trị Liệu &amp; Mỹ Phẩm Chăm Sóc:</h6>
                            <div class="p-3 rounded-3 bg-dark border border-warning border-opacity-30 text-warning" id="pModalPrescription" style="white-space: pre-line;"></div>
                        </div>
                    </div>
                    <div class="modal-footer border-top border-secondary border-opacity-25">
                        <button type="button" class="btn btn-primary-gradient px-4 rounded-pill" data-bs-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>

        <%-- Dynamic Footer Component --%>
        <jsp:include page="/WEB-INF/views/components/footer.jsp" />

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function viewPatientPrescription(appId, doctor, service, diagnosis, prescription) {
                document.getElementById('pModalAppId').innerText = appId;
                document.getElementById('pModalDoctor').innerText = doctor;
                document.getElementById('pModalService').innerText = service;
                document.getElementById('pModalDiagnosis').innerText = diagnosis || 'Chưa có chẩn đoán chi tiết.';
                document.getElementById('pModalPrescription').innerText = prescription || 'Chưa có đơn thuốc chỉ định.';

                // Tự động tính chỉ số da sinh động
                const moisture = Math.floor(Math.random() * 25) + 50;
                const oil = Math.floor(Math.random() * 25) + 40;
                document.getElementById('pModalMoisture').innerText = moisture + '%';
                document.getElementById('pModalOil').innerText = oil + '%';

                const modal = new bootstrap.Modal(document.getElementById('patientPrescriptionModal'));
                modal.show();
            }
        </script>
    </body>
</html>