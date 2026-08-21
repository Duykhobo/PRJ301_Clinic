<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- ═══════════════════════════════════════════ OVERLAY (mobile backdrop) ═══════════════════════════════════════════ --%>
<div class="sb-overlay" id="sbOverlay" onclick="closeSidebar()"></div>

<%-- ═══════════════════════════════════════════ SIDEBAR ═══════════════════════════════════════════ --%>
<aside class="ws-sidebar" id="wsSidebar">

  <%-- Brand --%>
  <div class="sb-brand">
    <div class="sb-brand-icon" style="background: linear-gradient(135deg, #ef4444, #f59e0b);"><i class="fa-solid fa-user-shield"></i></div>
    <div>
      <div class="sb-brand-text">PRJ301 <span style="color:#ef4444;">Clinic &amp; Spa</span></div>
      <div class="sb-brand-sub">Admin Control Center</div>
    </div>
  </div>

  <%-- User Profile --%>
  <div class="sb-profile" style="background: linear-gradient(135deg, rgba(239, 68, 68, 0.12), rgba(245, 158, 11, 0.08)); border-color: rgba(239, 68, 68, 0.25);">
    <div class="sb-avatar" style="background: linear-gradient(135deg, #ef4444, #f59e0b); box-shadow: 0 4px 14px rgba(239, 68, 68, 0.35);">
      ${sessionScope.LOGIN_USER.fullname.substring(0,1).toUpperCase()}
    </div>
    <div style="min-width:0;">
      <div class="sb-user-name"><c:out value="${sessionScope.LOGIN_USER.fullname}"/></div>
      <div class="sb-user-role" style="background: rgba(239, 68, 68, 0.2); border-color: rgba(239, 68, 68, 0.35); color: #fca5a5;">
        <i class="fa-solid fa-shield-halved"></i> Quản Trị Viên
      </div>
    </div>
  </div>

  <%-- Main Menu --%>
  <div class="sb-section-label">Quản Trị Hệ Thống</div>
  <ul class="sb-menu">
    <li>
      <a href="${pageContext.request.contextPath}/admin/dashboard?tab=users" class="${empty param.tab || param.tab == 'users' ? 'active' : ''}">
        <span class="sb-icon"><i class="fa-solid fa-users"></i></span>
        Quản Lý Người Dùng
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/admin/dashboard?tab=services" class="${param.tab == 'services' ? 'active' : ''}">
        <span class="sb-icon"><i class="fa-solid fa-hand-holding-medical"></i></span>
        Quản Lý Dịch Vụ
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/admin/dashboard?tab=settings" class="${param.tab == 'settings' ? 'active' : ''}">
        <span class="sb-icon"><i class="fa-solid fa-sliders"></i></span>
        Cấu Hình Hệ Thống
      </a>
    </li>
  </ul>

  <div class="sb-spacer"></div>
  <hr class="sb-divider">

  <%-- Bottom Menu --%>
  <div class="sb-section-label">Khác</div>
  <ul class="sb-menu" style="margin-bottom:1rem;">
    <li>
      <a href="${pageContext.request.contextPath}/profile">
        <span class="sb-icon"><i class="fa-solid fa-id-card"></i></span>
        Hồ Sơ Cá Nhân
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/MainController?action=home">
        <span class="sb-icon"><i class="fa-solid fa-house"></i></span>
        Trang Chủ
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/MainController?action=logout" class="danger-link">
        <span class="sb-icon"><i class="fa-solid fa-right-from-bracket"></i></span>
        Đăng Xuất
      </a>
    </li>
  </ul>
</aside>

<%-- ═══════════════════════════════════════════ MAIN WRAPPER ═══════════════════════════════════════════ --%>
<div class="ws-main">

  <%-- TOPBAR --%>
  <div class="ws-topbar">
    <div class="ws-topbar-left">
      <%-- Hamburger (mobile only) --%>
      <button class="sb-hamburger" id="sbHamburger" onclick="openSidebar()" aria-label="Mở menu">
        <i class="fa-solid fa-bars"></i>
      </button>
      <div>
        <div class="topbar-page-title">
          <i class="fa-solid fa-sliders me-2" style="color:#ef4444;"></i>Admin Dashboard
        </div>
        <div class="topbar-breadcrumb">PRJ301 Clinic &rsaquo; Control Center &rsaquo; Quản Trị Hệ Thống</div>
      </div>
    </div>
    <div class="ws-topbar-right d-flex align-items-center gap-2">
      <%-- Notification Bell Component --%>
      <jsp:include page="/WEB-INF/views/components/notification-bell.jsp" />

      <div class="topbar-date d-none d-sm-flex">
        <i class="fa-solid fa-calendar-day"></i>
        <span id="topbarDate">--/--/----</span>
      </div>
      <div class="topbar-clock d-none d-sm-flex">
        <i class="fa-solid fa-clock"></i>
        <span id="topbarClock">--:--:--</span>
      </div>
    </div>
  </div>

  <%-- Alerts --%>
  <div style="padding: 0 1.5rem;">
    <jsp:include page="/WEB-INF/views/components/alerts.jsp" />
  </div>

  <script>
    // ── Clock ──
    (function tick() {
      const now = new Date();
      document.getElementById('topbarClock').textContent = now.toLocaleTimeString('vi-VN');
      document.getElementById('topbarDate').textContent = now.toLocaleDateString('vi-VN', { weekday: 'short', day: '2-digit', month: '2-digit', year: 'numeric' });
      setTimeout(tick, 1000);
    })();

    // ── Sidebar toggle ──
    function openSidebar() {
      document.getElementById('wsSidebar').classList.add('open');
      document.getElementById('sbOverlay').classList.add('active');
      document.body.style.overflow = 'hidden';
    }
    function closeSidebar() {
      document.getElementById('wsSidebar').classList.remove('open');
      document.getElementById('sbOverlay').classList.remove('active');
      document.body.style.overflow = '';
    }

    // ── Swipe to close ──
    let touchStartX = 0;
    document.getElementById('wsSidebar').addEventListener('touchstart', e => {
      touchStartX = e.changedTouches[0].screenX;
    }, { passive: true });
    document.getElementById('wsSidebar').addEventListener('touchend', e => {
      if (touchStartX - e.changedTouches[0].screenX > 60) closeSidebar();
    }, { passive: true });
  </script>
