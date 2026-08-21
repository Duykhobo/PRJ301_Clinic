<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- sidebar-receptionist.jsp - Dùng cho sảnh tiếp đón Lễ tân / Thu ngân --%>

<%-- ═══════════════════════════════════════════ OVERLAY (mobile backdrop) ═══════════════════════════════════════════ --%>
<div class="sb-overlay" id="sbOverlay" onclick="closeSidebar()"></div>

<%-- ═══════════════════════════════════════════ SIDEBAR ═══════════════════════════════════════════ --%>
<aside class="ws-sidebar" id="wsSidebar">

  <%-- Brand --%>
  <div class="sb-brand">
    <div class="sb-brand-icon" style="background: linear-gradient(135deg, #10b981, #0ea5e9);"><i class="fa-solid fa-headset"></i></div>
    <div>
      <div class="sb-brand-text">PRJ301 <span style="color:#10b981;">Clinic &amp; Spa</span></div>
      <div class="sb-brand-sub">Reception Workspace</div>
    </div>
  </div>

  <%-- User Profile --%>
  <div class="sb-profile" style="background: linear-gradient(135deg, rgba(16, 185, 129, 0.12), rgba(14, 165, 233, 0.08)); border-color: rgba(16, 185, 129, 0.25);">
    <div class="sb-avatar" style="background: linear-gradient(135deg, #10b981, #0ea5e9); box-shadow: 0 4px 14px rgba(16, 185, 129, 0.35);">
      ${sessionScope.LOGIN_USER.fullname.substring(0,1).toUpperCase()}
    </div>
    <div style="min-width:0;">
      <div class="sb-user-name"><c:out value="${sessionScope.LOGIN_USER.fullname}"/></div>
      <div class="sb-user-role" style="background: rgba(16, 185, 129, 0.2); border-color: rgba(16, 185, 129, 0.35); color: #6ee7b7;">
        <i class="fa-solid fa-user-gear"></i> Lễ Tân / Thu Ngân
      </div>
    </div>
  </div>

  <%-- Main Menu --%>
  <div class="sb-section-label">Quản Lý Sảnh Tiếp Đón</div>
  <ul class="sb-menu">
    <li>
      <a href="${pageContext.request.contextPath}/receptionist/dashboard" class="active">
        <span class="sb-icon"><i class="fa-solid fa-hospital-user"></i></span>
        Sảnh Tiếp Đón Bệnh Nhân
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
          <i class="fa-solid fa-hospital-user me-2" style="color:#10b981;"></i>Sảnh Tiếp Đón Lễ Tân
        </div>
        <div class="topbar-breadcrumb">PRJ301 Clinic &rsaquo; Reception Workspace &rsaquo; Sảnh Tiếp Đón</div>
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
