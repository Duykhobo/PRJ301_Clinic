<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <title>Lịch Sử Khám & Thanh Toán | PRJ301 Clinic</title>
                <jsp:include page="/WEB-INF/views/components/head.jsp" />
            </head>

            <body class="d-flex flex-column min-vh-100">

                <%-- Dynamic Navbar Component --%>
                    <jsp:include page="/WEB-INF/views/components/navbar.jsp" />

                    <div class="container my-4 flex-grow-1">
                        <%-- Header Banner Glassmorphism --%>
                            <div class="glass-card p-4 mb-4 animate-fade-in">
                                <div class="row align-items-center g-3">
                                    <div class="col-md-8 d-flex align-items-center gap-3">
                                        <div class="avatar-circle bg-primary-subtle text-info fs-2 fw-bold border border-info border-2"
                                            style="width: 65px; height: 65px; display: flex; align-items: center; justify-content: center; border-radius: 50%;">
                                            <i class="fa-solid fa-clock-rotate-left"></i>
                                        </div>
                                        <div>
                                            <h3 class="fw-bold text-white mb-1">
                                                Lịch Sử Khám & Thanh Toán
                                            </h3>
                                            <p class="text-info mb-0 small">
                                                <i class="fa-solid fa-user-check me-1"></i>Bệnh nhân:
                                                ${sessionScope.LOGIN_USER.fullname} |
                                                <i class="fa-solid fa-receipt ms-2 me-1"></i>Tổng cộng: ${totalItems}
                                                cuộc hẹn
                                            </p>
                                        </div>
                                    </div>

                                    <div class="col-md-4 text-md-end">
                                        <a href="${pageContext.request.contextPath}/MainController?action=booking"
                                            class="btn btn-primary-gradient px-4 py-2 fw-bold">
                                            <i class="fa-solid fa-calendar-plus me-2"></i>Đặt Lịch Khám Mới
                                        </a>
                                    </div>
                                </div>
                            </div>

                            <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

                            <%-- History Table Glassmorphism --%>
                                <div class="glass-card p-4 animate-fade-in mb-4">
                                    <div class="table-responsive">
                                        <table class="table table-dark table-hover align-middle mb-0">
                                            <thead>
                                                <tr class="text-info border-bottom border-secondary">
                                                    <th>Mã Cuộc Hẹn</th>
                                                    <th>Ngày & Giờ Khám</th>
                                                    <th>Bác Sĩ Đảm Nhận</th>
                                                    <th>Dịch Vụ Kê Khai</th>
                                                    <th>Tổng Tiền</th>
                                                    <th>Trạng Thái Thanh Toán</th>
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
                                                                    <div class="fw-bold text-info"><i
                                                                            class="fa-solid fa-calendar-day me-1"></i>${app.appointmentDate}
                                                                    </div>
                                                                    <div class="small text-muted"><i
                                                                            class="fa-solid fa-clock me-1"></i>${app.startTime}
                                                                    </div>
                                                                </td>
                                                                <td class="text-white">
                                                                    <i
                                                                        class="fa-solid fa-user-doctor me-1 text-info"></i>${app.doctorName}
                                                                </td>
                                                                <td>
                                                                    <span class="badge px-3 py-2 rounded-pill fw-bold"
                                                                        style="background: rgba(13, 202, 240, 0.15); color: #0dcaf0 !important; border: 1px solid rgba(13, 202, 240, 0.4);">
                                                                        <i
                                                                            class="fa-solid fa-notes-medical me-1"></i>${app.serviceName}
                                                                    </span>
                                                                </td>
                                                                <td class="fw-bold text-warning">${app.totalPrice} VNĐ
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${app.paymentStatus == 'PAID'}">
                                                                            <span
                                                                                class="badge bg-success bg-opacity-20 border border-success border-opacity-25 px-3 py-2 rounded-pill fw-bold"
                                                                                style="color: #00ff88 !important;">
                                                                                <i
                                                                                    class="fa-solid fa-circle-check me-1"></i>Đã
                                                                                Thanh Toán<c:if
                                                                                    test="${not empty app.paymentMethod}">
                                                                                    (${app.paymentMethod})</c:if>
                                                                            </span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span
                                                                                class="badge border border-warning border-opacity-25 px-3 py-2 rounded-pill fw-bold"
                                                                                style="color: #ffc107 !important; background-color: rgba(255, 193, 7, 0.2) !important;">
                                                                                <i
                                                                                    class="fa-solid fa-clock me-1"></i>Chưa
                                                                                Thanh Toán
                                                                            </span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${app.status == 'COMPLETED'}">
                                                                            <span class="badge bg-success"><i
                                                                                    class="fa-solid fa-circle-check me-1"></i>Hoàn
                                                                                Tất Khám</span>
                                                                        </c:when>
                                                                        <c:when test="${app.status == 'CONFIRMED'}">
                                                                            <span class="badge bg-info text-dark"><i
                                                                                    class="fa-solid fa-user-check me-1"></i>Đã
                                                                                Tiếp Nhận</span>
                                                                        </c:when>
                                                                        <c:when test="${app.status == 'CANCELLED'}">
                                                                            <span class="badge bg-danger"><i
                                                                                    class="fa-solid fa-ban me-1"></i>Đã
                                                                                Hủy</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="badge bg-warning text-dark"><i
                                                                                    class="fa-solid fa-spinner fa-spin me-1"></i>Chờ
                                                                                Khám</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td class="text-end">
                                                                    <div class="d-flex gap-1 justify-content-end">
                                                                        <%-- Nút Thanh Toán VietQR SePay nếu chưa trả
                                                                            tiền --%>
                                                                            <c:if
                                                                                test="${app.paymentStatus == 'UNPAID' && app.status != 'CANCELLED'}">
                                                                                <a href="${pageContext.request.contextPath}/booking?action=payment&id=${app.id}"
                                                                                    class="btn btn-sm btn-success fw-bold">
                                                                                    <i
                                                                                        class="fa-solid fa-qrcode me-1"></i>Thanh
                                                                                    Toán QR
                                                                                </a>
                                                                            </c:if>

                                                                            <%-- Nút Xem Đơn Thuốc & Chẩn Đoán nếu ca
                                                                                khám đã hoàn tất --%>
                                                                                <c:if
                                                                                    test="${app.status == 'COMPLETED' && not empty recordsMap[app.id]}">
                                                                                    <button type="button"
                                                                                        class="btn btn-sm btn-info fw-bold"
                                                                                        onclick="viewPatientPrescription('#${app.id}', '${app.doctorName}', '${app.serviceName}', '${recordsMap[app.id].diagnosis}', '${recordsMap[app.id].prescriptionOrResult}')">
                                                                                        <i
                                                                                            class="fa-solid fa-file-medical me-1"></i>Xem
                                                                                        Đơn Thuốc
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
                                                                <i
                                                                    class="fa-solid fa-calendar-xmark fs-1 mb-3 d-block text-secondary"></i>
                                                                Bạn chưa có cuộc hẹn khám nào.
                                                                <a href="${pageContext.request.contextPath}/MainController?action=booking"
                                                                    class="text-info fw-bold ms-1">Đặt lịch ngay</a>
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
                                                <%-- Nút Trang Trước --%>
                                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                        <a class="page-link bg-dark text-white border-secondary"
                                                            href="${pageContext.request.contextPath}/history?page=${currentPage - 1}"
                                                            aria-label="Previous">
                                                            <i class="fa-solid fa-chevron-left"></i> Trang trước
                                                        </a>
                                                    </li>

                                                    <%-- Các Nút Số Trang --%>
                                                        <c:forEach var="i" begin="1" end="${totalPages}">
                                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                                <a class="page-link ${currentPage == i ? 'btn-primary-gradient text-white border-0' : 'bg-dark text-info border-secondary'}"
                                                                    href="${pageContext.request.contextPath}/history?page=${i}">
                                                                    ${i}
                                                                </a>
                                                            </li>
                                                        </c:forEach>

                                                        <%-- Nút Trang Sau --%>
                                                            <li
                                                                class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                                <a class="page-link bg-dark text-white border-secondary"
                                                                    href="${pageContext.request.contextPath}/history?page=${currentPage + 1}"
                                                                    aria-label="Next">
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
                                <div class="modal-content glass-card border border-info border-opacity-30 text-white">
                                    <div class="modal-header border-bottom border-secondary">
                                        <h5 class="modal-title fw-bold text-info d-flex align-items-center gap-2">
                                            <i class="fa-solid fa-file-prescription"></i> Kết Quả Chẩn Đoán & Đơn Thuốc
                                            Y Khoa
                                        </h5>
                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="p-3 rounded-3 mb-3" style="background: rgba(255, 255, 255, 0.05);">
                                            <div class="row g-2">
                                                <div class="col-md-4">
                                                    <span class="text-muted small">Mã ca:</span>
                                                    <span class="fw-bold text-white ms-1" id="pModalAppId"></span>
                                                </div>
                                                <div class="col-md-4">
                                                    <span class="text-muted small">Bác sĩ khám:</span>
                                                    <span class="fw-bold text-info ms-1" id="pModalDoctor"></span>
                                                </div>
                                                <div class="col-md-4">
                                                    <span class="text-muted small">Dịch vụ:</span>
                                                    <span class="fw-bold text-warning ms-1" id="pModalService"></span>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="mb-4">
                                            <h6 class="fw-bold text-info mb-2"><i
                                                    class="fa-solid fa-stethoscope me-2"></i>Chẩn Đoán Bệnh Lý:</h6>
                                            <div class="p-3 rounded-3 bg-dark border border-secondary text-white-50"
                                                id="pModalDiagnosis" style="white-space: pre-line;"></div>
                                        </div>

                                        <div class="mb-3">
                                            <h6 class="fw-bold text-warning mb-2"><i
                                                    class="fa-solid fa-pills me-2"></i>Đơn Thuốc & Chỉ Định Điều Trị:
                                            </h6>
                                            <div class="p-3 rounded-3 bg-dark border border-warning border-opacity-30 text-warning"
                                                id="pModalPrescription" style="white-space: pre-line;"></div>
                                        </div>
                                    </div>
                                    <div class="modal-footer border-top border-secondary">
                                        <button type="button" class="btn btn-primary-gradient px-4"
                                            data-bs-dismiss="modal">Đóng</button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <%-- Dynamic Footer Component --%>
                            <jsp:include page="/WEB-INF/views/components/footer.jsp" />

                            <script
                                src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                            <script>
                                function viewPatientPrescription(appId, doctor, service, diagnosis, prescription) {
                                    document.getElementById('pModalAppId').innerText = appId;
                                    document.getElementById('pModalDoctor').innerText = doctor;
                                    document.getElementById('pModalService').innerText = service;
                                    document.getElementById('pModalDiagnosis').innerText = diagnosis || 'Chưa có chẩn đoán chi tiết.';
                                    document.getElementById('pModalPrescription').innerText = prescription || 'Chưa có đơn thuốc chỉ định.';

                                    const modal = new bootstrap.Modal(document.getElementById('patientPrescriptionModal'));
                                    modal.show();
                                }
                            </script>
            </body>

            </html>