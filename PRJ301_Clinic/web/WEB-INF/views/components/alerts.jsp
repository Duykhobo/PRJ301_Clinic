<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Alert Thông báo Lỗi từ Controller --%>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show mb-4 glass-card p-3 border-danger border-opacity-50 text-danger" role="alert">
        <i class="fa-solid fa-circle-exclamation me-2"></i>${errorMessage}
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<%-- Alert Thông báo Thành công từ Controller --%>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible fade show mb-4 glass-card p-3 border-success border-opacity-50 text-success" role="alert">
        <i class="fa-solid fa-circle-check me-2"></i>${successMessage}
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<%-- Alert Thông báo Đăng ký thành công từ URL Param --%>
<c:if test="${param.registered eq 'success'}">
    <div class="alert alert-success alert-dismissible fade show mb-4 glass-card p-3 border-success border-opacity-50 text-success" role="alert">
        <i class="fa-solid fa-circle-check me-2"></i>Đăng ký tài khoản thành công! Vui lòng đăng nhập.
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<%-- Alert Thông báo Đăng xuất thành công từ URL Param --%>
<c:if test="${param.logout eq 'success'}">
    <div class="alert alert-info alert-dismissible fade show mb-4 glass-card p-3 border-info border-opacity-50 text-info" role="alert">
        <i class="fa-solid fa-circle-info me-2"></i>Bạn đã đăng xuất tài khoản an toàn.
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
