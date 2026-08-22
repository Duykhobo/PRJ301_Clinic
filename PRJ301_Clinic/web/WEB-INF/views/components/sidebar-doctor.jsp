<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%-- sidebar-doctor.jsp Dùng ở đầu doctor/dashboard.jsp thay cho navbar.jsp --%>




      <%-- ═══════════════════════════════════════════ OVERLAY (mobile backdrop)
        ═══════════════════════════════════════════ --%>
        <div class="sb-overlay" id="sbOverlay" onclick="closeSidebar()"></div>

        <%-- ═══════════════════════════════════════════ SIDEBAR ═══════════════════════════════════════════ --%>
          <aside class="ws-sidebar" id="wsSidebar">

            <%-- Brand --%>
              <a href="${pageContext.request.contextPath}/doctor/dashboard" class="sb-brand" style="text-decoration:none; color:inherit;">
                <div class="sb-brand-icon"><i class="fa-solid fa-notes-medical"></i></div>
                <div>
                  <div class="sb-brand-text"><c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Clinic & Spa')}"/></div>
                  <div class="sb-brand-sub">Doctor Workspace</div>
                </div>
              </a>

              <%-- User Profile --%>
                <div class="sb-profile">
                  <div class="sb-avatar">
                    ${sessionScope.LOGIN_USER.fullname.substring(0,1).toUpperCase()}
                  </div>
                  <div style="min-width:0;">
                    <div class="sb-user-name"><c:out value="${sessionScope.LOGIN_USER.fullname}"/></div>
                    <div class="sb-user-role">
                      <i class="fa-solid fa-stethoscope"></i> Bác Sĩ
                    </div>
                  </div>
                </div>

                  <%-- Main Menu --%>
                  <div class="sb-section-label">Workspace Bác Sĩ</div>
                  <ul class="sb-menu">
                    <li>
                      <a href="${pageContext.request.contextPath}/doctor/dashboard?tab=appointments" class="${empty param.tab || param.tab == 'appointments' ? 'active' : ''}">
                        <span class="sb-icon"><i class="fa-solid fa-clipboard-list"></i></span>
                        Danh Sách Ca Khám
                      </a>
                    </li>
                    <li>
                      <a href="${pageContext.request.contextPath}/doctor/dashboard?tab=packages" class="${param.tab == 'packages' ? 'active' : ''}">
                        <span class="sb-icon"><i class="fa-solid fa-wand-magic-sparkles"></i></span>
                        Gói Liệu Trình Trọn Gói
                      </a>
                    </li>
                    <li>
                      <a href="${pageContext.request.contextPath}/doctor/dashboard?tab=schedules" class="${param.tab == 'schedules' ? 'active' : ''}">
                        <span class="sb-icon"><i class="fa-solid fa-clock-rotate-left"></i></span>
                        Quản Lý Lịch Làm Việc
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
                        <a href="${pageContext.request.contextPath}/doctor/dashboard">
                          <span class="sb-icon"><i class="fa-solid fa-stethoscope"></i></span>
                          Bàn Khám Bác Sĩ
                        </a>
                      </li>
                      <li>
                        <a href="${pageContext.request.contextPath}/logout" onclick="return confirmLogout(event);" class="danger-link">
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
                          <c:choose>
                            <c:when test="${param.tab == 'schedules'}">
                              <i class="fa-solid fa-clock-rotate-left me-2" style="color:#60a5fa;"></i>Quản Lý Lịch Làm Việc Y Tế
                            </c:when>
                            <c:when test="${param.tab == 'packages'}">
                              <i class="fa-solid fa-wand-magic-sparkles me-2" style="color:#fbbf24;"></i>Quản Lý Gói Liệu Trình Trọn Gói
                            </c:when>
                            <c:otherwise>
                              <i class="fa-solid fa-clipboard-list me-2" style="color:#60a5fa;"></i>Danh Sách Ca Khám Bệnh
                            </c:otherwise>
                          </c:choose>
                        </div>
                        <div class="topbar-breadcrumb">
                          PRJ301 Clinic &rsaquo; Doctor Workspace &rsaquo; ${param.tab == 'schedules' ? 'Quản Lý Lịch Làm Việc' : (param.tab == 'packages' ? 'Gói Liệu Trình Trọn Gói' : 'Danh Sách Ca Khám')}
                        </div>
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

                  <%-- NOTE: ws-main div is closed in dashboard.jsp before footer --%>

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