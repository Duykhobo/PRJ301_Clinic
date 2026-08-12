<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Không Gian Bác Sĩ | PRJ301 Clinic Workspace</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
</head>
<body class="d-flex flex-column min-vh-100">

<%-- Dynamic Navbar Component --%>
<jsp:include page="/WEB-INF/views/components/navbar.jsp" />

<div class="container my-4 flex-grow-1">
    <%-- Header Doctor Info Banner --%>
    <div class="glass-card p-4 mb-4 animate-fade-in">
        <div class="row align-items-center g-3">
            <div class="col-md-8 d-flex align-items-center gap-3">
                <div class="avatar-circle bg-info-subtle text-info fs-3 fw-bold border border-info border-2" style="width: 60px; height: 60px; display: flex; align-items: center; justify-content: center; border-radius: 50%;">
                    <i class="fa-solid fa-user-doctor"></i>
                </div>
                <div>
                    <h4 class="fw-bold text-white mb-1">
                        Xin chào, ${sessionScope.LOGIN_USER.fullname}
                    </h4>
                    <p class="text-info mb-0 small">
                        <i class="fa-solid fa-stethoscope me-1"></i>Bác sĩ Chuyên Khoa | 
                        <i class="fa-solid fa-hospital-user ms-2 me-1"></i>PRJ301 Clinic Workspace
                    </p>
                </div>
            </div>

            <%-- Datepicker Filter Form --%>
            <div class="col-md-4">
                <form action="${pageContext.request.contextPath}/doctor/dashboard" method="GET" class="d-flex gap-2 justify-content-md-end">
                    <div class="position-relative flex-grow-1" style="max-width: 220px;">
                        <i class="fa-solid fa-calendar-days text-info position-absolute top-50 start-0 translate-middle-y ms-3 z-3"></i>
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
        <div class="col-md-6">
            <div class="glass-card p-3 d-flex align-items-center gap-3 border-start border-4 border-warning">
                <div class="p-3 rounded-circle bg-warning bg-opacity-20 text-warning fs-3">
                    <i class="fa-solid fa-clock"></i>
                </div>
                <div>
                    <span class="text-muted small">Bệnh Nhân Chờ Khám</span>
                    <h3 class="fw-bold text-white mb-0">${pendingCount} Bệnh nhân</h3>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="glass-card p-3 d-flex align-items-center gap-3 border-start border-4 border-success">
                <div class="p-3 rounded-circle bg-success bg-opacity-20 text-success fs-3">
                    <i class="fa-solid fa-circle-check"></i>
                </div>
                <div>
                    <span class="text-muted small">Ca Đã Hoàn Tất Chẩn Đoán</span>
                    <h3 class="fw-bold text-white mb-0">${completedCount} Bệnh nhân</h3>
                </div>
            </div>
        </div>
    </div>

    <%-- Table Appointments --%>
    <div class="glass-card p-4 animate-fade-in">
        <h5 class="fw-bold text-white mb-3 d-flex align-items-center gap-2">
            <i class="fa-solid fa-list-check text-info"></i>
            Danh Sách Ca Khám Bệnh Ngày ${selectedDate}
        </h5>

        <div class="table-responsive">
            <table class="table table-dark table-hover align-middle mb-0">
                <thead>
                    <tr class="text-info border-bottom border-secondary">
                        <th>Giờ Khám</th>
                        <th>Bệnh Nhân</th>
                        <th>Số Điện Thoại</th>
                        <th>Dịch Vụ Khám</th>
                        <th>Thanh Toán</th>
                        <th>Trạng Thái</th>
                        <th class="text-end">Thao Tác Bác Sĩ</th>
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
                                                    <i class="fa-solid fa-clock me-1"></i>Chưa Thu (Tiền mặt sảnh)
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.status == 'COMPLETED'}">
                                                <span class="badge bg-success"><i class="fa-solid fa-circle-check me-1"></i>Đã Khám Xong</span>
                                            </c:when>
                                            <c:when test="${app.status == 'CONFIRMED'}">
                                                <span class="badge bg-info text-dark"><i class="fa-solid fa-user-check me-1"></i>Đã Tiếp Nhận</span>
                                            </c:when>
                                            <c:when test="${app.status == 'CANCELLED'}">
                                                <span class="badge bg-danger"><i class="fa-solid fa-ban me-1"></i>Đã Hủy</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning text-dark"><i class="fa-solid fa-spinner fa-spin me-1"></i>Chờ Khám</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">
                                        <button type="button" class="btn btn-sm btn-primary-gradient px-3 rounded-pill fw-bold"
                                                onclick="openMedicalModal(${app.id}, '${app.patientName}', '${app.serviceName}', '${recordsMap[app.id].diagnosis}', '${recordsMap[app.id].prescriptionOrResult}')">
                                            <i class="fa-solid fa-notes-medical me-1"></i>Khám & Kê Đơn
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" class="text-center py-4 text-muted">
                                    <i class="fa-solid fa-calendar-xmark fs-2 mb-2 d-block"></i>
                                    Không có ca khám nào trong ngày ${selectedDate}
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%-- Glassmorphism Modal Chẩn Đoán & Kê Đơn Thuốc Y Khoa --%>
<div class="modal fade" id="medicalModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content glass-card border border-info border-opacity-30 text-white">
            <div class="modal-header border-bottom border-secondary">
                <h5 class="modal-title fw-bold text-info d-flex align-items-center gap-2">
                    <i class="fa-solid fa-stethoscope"></i> Hồ Sơ Khám Bệnh & Kê Đơn Thuốc
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/doctor/dashboard" method="POST">
                <input type="hidden" name="action" value="save-diagnosis">
                <input type="hidden" name="appointmentId" id="modalAppointmentId">
                <input type="hidden" name="date" value="${selectedDate}">

                <div class="modal-body space-y-3">
                    <div class="p-3 rounded-3 mb-3" style="background: rgba(255, 255, 255, 0.05);">
                        <div class="row g-2">
                            <div class="col-md-6">
                                <span class="text-muted small">Bệnh nhân:</span>
                                <span class="fw-bold text-white ms-2" id="modalPatientName"></span>
                            </div>
                            <div class="col-md-6">
                                <span class="text-muted small">Dịch vụ:</span>
                                <span class="fw-bold text-info ms-2" id="modalServiceName"></span>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="diagnosis" class="form-label fw-bold text-info">
                            <i class="fa-solid fa-notes-medical me-1"></i>Chẩn Đoán Bệnh Lý & Tình Trạng Sức Khỏe:
                        </label>
                        <textarea name="diagnosis" id="modalDiagnosis" class="form-control form-control-dark" rows="3" 
                                  placeholder="Nhập chi tiết chẩn đoán y khoa, tình trạng răng/da của bệnh nhân..." required></textarea>
                    </div>

                    <div class="mb-3">
                        <label for="prescription" class="form-label fw-bold text-warning">
                            <i class="fa-solid fa-pills me-1"></i>Chỉ Định Đơn Thuốc & Kết Quả Điều Trị:
                        </label>
                        <textarea name="prescription" id="modalPrescription" class="form-control form-control-dark" rows="4" 
                                  placeholder="Ví dụ: 1. Amoxicillin 500mg (2 viên/ngày)...&#10;2. Paracetamol 500mg khi đau...&#10;Dặn dò: Tái khám sau 7 ngày." required></textarea>
                    </div>
                </div>

                <div class="modal-footer border-top border-secondary">
                    <button type="button" class="btn btn-outline-glass" data-bs-dismiss="modal">Hủy Quả</button>
                    <button type="submit" class="btn btn-primary-gradient px-4 fw-bold">
                        <i class="fa-solid fa-floppy-disk me-2"></i>Lưu Đơn Thuốc & Hoàn Tất Ca Khám
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<%-- Dynamic Footer Component --%>
<jsp:include page="/WEB-INF/views/components/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
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
