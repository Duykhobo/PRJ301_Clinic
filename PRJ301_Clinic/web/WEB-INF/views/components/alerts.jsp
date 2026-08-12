<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Alert Banner Thông báo Lỗi từ Controller --%>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show mb-4 glass-card p-3 border-danger border-opacity-50 text-danger" role="alert">
        <i class="fa-solid fa-circle-exclamation me-2 fs-5"></i>${errorMessage}
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<%-- Alert Banner Thông báo Thành công từ Controller --%>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible fade show mb-4 glass-card p-3 border-success border-opacity-50 text-success" role="alert">
        <i class="fa-solid fa-circle-check me-2 fs-5"></i>${successMessage}
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<%-- Alert Banner Thông báo Đăng ký thành công từ URL Param --%>
<c:if test="${param.registered eq 'success'}">
    <div class="alert alert-success alert-dismissible fade show mb-4 glass-card p-3 border-success border-opacity-50 text-success" role="alert">
        <i class="fa-solid fa-circle-check me-2 fs-5"></i>Đăng ký tài khoản thành công! Vui lòng đăng nhập.
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<%-- Alert Banner Thông báo Đăng xuất thành công từ URL Param --%>
<c:if test="${param.logout eq 'success'}">
    <div class="alert alert-info alert-dismissible fade show mb-4 glass-card p-3 border-info border-opacity-50 text-info" role="alert">
        <i class="fa-solid fa-circle-info me-2 fs-5"></i>Bạn đã đăng xuất tài khoản an toàn.
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<%-- TOAST NOTIFICATION CONTAINER (GÓC TRÊN BÊN PHẢI) --%>
<div class="toast-container position-fixed top-0 end-0 p-4" style="z-index: 9999;">
    <div id="customToast" class="toast align-items-center text-white border-0 glass-card p-2 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex align-items-center justify-content-between">
            <div class="toast-body d-flex align-items-center gap-2" id="toastMessage">
                <i id="toastIcon" class="fa-solid fa-circle-exclamation fs-5 text-warning"></i>
                <span id="toastText" class="fw-semibold">Thông báo...</span>
            </div>
            <button type="button" class="btn-close btn-close-white me-2" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    </div>
</div>

<script>
    function showToast(message, isSuccess = false) {
        const toastEl = document.getElementById('customToast');
        const toastText = document.getElementById('toastText');
        const toastIcon = document.getElementById('toastIcon');

        if (!toastEl || !toastText || !toastIcon) return;

        toastText.innerText = message;
        if (isSuccess) {
            toastEl.style.borderColor = 'rgba(74, 222, 128, 0.5)';
            toastIcon.className = 'fa-solid fa-circle-check fs-5 text-success';
        } else {
            toastEl.style.borderColor = 'rgba(248, 113, 113, 0.5)';
            toastIcon.className = 'fa-solid fa-triangle-exclamation fs-5 text-danger';
        }

        const bsToast = new bootstrap.Toast(toastEl, { delay: 4000 });
        bsToast.show();
    }
</script>
