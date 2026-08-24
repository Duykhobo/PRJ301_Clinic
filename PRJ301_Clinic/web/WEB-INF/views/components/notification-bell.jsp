<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- COMPONENT: TRUNG TÂM THÔNG BÁO THỜI GIAN THỰC (NOTIFICATION BELL DROPDOWN) --%>
<div class="dropdown noti-bell-wrapper d-inline-block position-relative">
    <button class="noti-bell-btn" type="button" id="notificationBellDropdown" aria-expanded="false" title="Thông báo hệ thống">
        <i class="fa-solid fa-bell"></i>
        <span class="noti-badge d-none" id="notification-badge">0</span>
    </button>
    <div class="dropdown-menu dropdown-menu-end noti-dropdown-menu shadow-lg" id="notificationDropdownMenu" aria-labelledby="notificationBellDropdown">
        <div class="noti-header">
            <div class="d-flex align-items-center gap-2">
                <i class="fa-solid fa-bell text-cyan"></i>
                <strong class="text-white small">Thông Báo Hệ Thống</strong>
            </div>
            <button type="button" class="btn btn-link p-0 text-cyan text-decoration-none small fs-8 fw-semibold" id="mark-all-read-btn" title="Đánh dấu tất cả thông báo là đã đọc">
                <i class="fa-solid fa-check-double me-1"></i>Đã đọc tất cả
            </button>
        </div>
        <div class="noti-scroll-body" id="notification-list">
            <div class="p-4 text-center text-white-50 small">
                <i class="fa-solid fa-spinner fa-spin text-cyan me-2"></i>Đang tải thông báo...
            </div>
        </div>
    </div>
</div>