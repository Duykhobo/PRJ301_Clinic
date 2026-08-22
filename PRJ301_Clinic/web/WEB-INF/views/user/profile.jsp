<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Hồ Sơ Cá Nhân — <c:out value="${not empty clinicSettings['CLINIC_NAME'] ? clinicSettings['CLINIC_NAME'] : (not empty settingsMap['CLINIC_NAME'] ? settingsMap['CLINIC_NAME'] : 'Phòng Khám & Spa')}"/></title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/profile.css">
</head>
<body class="d-flex flex-column min-vh-100">

    <%-- Dynamic Navbar --%>
    <jsp:include page="/WEB-INF/views/components/navbar.jsp" />

    <div class="container my-auto py-5" style="max-width: 960px;">
        <%-- Header / Back Link --%>
        <div class="d-flex align-items-center justify-content-between mb-4">
            <h4 class="fw-bold text-white mb-0">
                <i class="fa-solid fa-id-card-clip text-cyan me-2"></i>Hồ Sơ &amp; Cấu Hình Tài Khoản
            </h4>
            <c:choose>
                <c:when test="${sessionScope.LOGIN_USER.role == 'ADMIN'}">
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-glass rounded-pill px-4" style="font-size: .9rem;">
                        <i class="fa-solid fa-arrow-left me-2"></i>Bảng Điều Khiển
                    </a>
                </c:when>
                <c:when test="${sessionScope.LOGIN_USER.role == 'DOCTOR'}">
                    <a href="${pageContext.request.contextPath}/doctor/dashboard" class="btn btn-outline-glass rounded-pill px-4" style="font-size: .9rem;">
                        <i class="fa-solid fa-arrow-left me-2"></i>Bàn Khám Bác Sĩ
                    </a>
                </c:when>
                <c:when test="${sessionScope.LOGIN_USER.role == 'RECEPTIONIST'}">
                    <a href="${pageContext.request.contextPath}/receptionist/dashboard" class="btn btn-outline-glass rounded-pill px-4" style="font-size: .9rem;">
                        <i class="fa-solid fa-arrow-left me-2"></i>Sảnh Tiếp Đón
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-outline-glass rounded-pill px-4" style="font-size: .9rem;">
                        <i class="fa-solid fa-arrow-left me-2"></i>Trang Chủ
                    </a>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- Alerts --%>
        <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

        <div class="row g-4">
            <%-- Sidebar Card --%>
            <div class="col-md-4">
                <div class="glass-card p-4 text-center">
                    <div class="d-flex justify-content-center mb-3">
                        <div class="profile-avatar">
                            ${(not empty user ? user.fullname : sessionScope.LOGIN_USER.fullname).substring(0,1).toUpperCase()}
                        </div>
                    </div>
                    <h5 class="fw-bold text-white mb-1"><c:out value="${not empty user ? user.fullname : sessionScope.LOGIN_USER.fullname}"/></h5>
                    <p class="text-cyan mb-3 small"><c:out value="${not empty user ? user.email : sessionScope.LOGIN_USER.email}"/></p>

                    <div class="d-flex justify-content-center gap-2 mb-4">
                        <span class="badge bg-cyan bg-opacity-20 text-cyan border border-cyan border-opacity-30 px-3 py-2 rounded-pill fw-bold">
                            <i class="fa-solid fa-user-shield me-1"></i><c:out value="${not empty user ? user.role : sessionScope.LOGIN_USER.role}"/>
                        </span>
                        <span class="badge bg-success bg-opacity-20 text-emerald border border-success border-opacity-30 px-3 py-2 rounded-pill fw-bold">
                            <i class="fa-solid fa-circle-check me-1"></i>Hoạt Động
                        </span>
                    </div>

                    <div class="border-top border-secondary border-opacity-25 pt-3 text-start text-white-50 small">
                        <div class="mb-2"><i class="fa-solid fa-user me-2 text-cyan"></i>Username: <strong class="text-white"><c:out value="${not empty user ? user.username : sessionScope.LOGIN_USER.username}"/></strong></div>
                        <div class="mb-2"><i class="fa-solid fa-phone me-2 text-warning"></i>SĐT: <strong class="text-white"><c:out value="${not empty user ? user.phone : sessionScope.LOGIN_USER.phone}"/></strong></div>
                        <div><i class="fa-solid fa-calendar-alt me-2 text-emerald"></i>Thành viên từ: <strong class="text-white"><c:out value="${not empty user ? user.createdAt : sessionScope.LOGIN_USER.createdAt}"/></strong></div>
                    </div>

                    <c:if test="${sessionScope.LOGIN_USER.role == 'PATIENT' && not empty loyaltyProfile}">
                        <%-- SPA & CLINIC: VIP LOYALTY CARD (100% REAL DATA VIA JSTL & EL) --%>
                        <div class="p-3 mt-3 rounded-3 text-start" style="background: linear-gradient(135deg, rgba(245, 158, 11, 0.15), rgba(16, 185, 129, 0.15)); border: 1px solid rgba(245, 158, 11, 0.4);">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge ${loyaltyProfile.tierBadgeClass} px-2 py-1 rounded-pill fw-bold">
                                    <i class="fa-solid fa-crown me-1 text-warning"></i><c:out value="${loyaltyProfile.tierName}"/>
                                </span>
                                <small class="text-white-50"><i class="fa-solid fa-star text-warning me-1"></i><fmt:formatNumber value="${loyaltyProfile.totalPoints}" pattern="#,##0" maxFractionDigits="0"/> Điểm</small>
                            </div>
                            <div class="fs-7 text-white fw-semibold mb-1">Đặc quyền Hội viên Spa:</div>
                            <ul class="list-unstyled mb-0 text-white-50 fs-8">
                                <c:if test="${loyaltyProfile.discountPercent > 0}">
                                    <li><i class="fa-solid fa-check text-emerald me-1"></i>Ưu đãi giảm <strong class="text-warning">${loyaltyProfile.discountPercent}%</strong> tất cả hóa đơn</li>
                                </c:if>
                                <li><i class="fa-solid fa-check text-emerald me-1"></i>Tổng chi tiêu: <strong class="text-cyan"><fmt:formatNumber value="${loyaltyProfile.totalSpent}" pattern="#,##0" maxFractionDigits="0"/> VNĐ</strong></li>
                                <li><i class="fa-solid fa-check text-emerald me-1"></i><c:out value="${loyaltyProfile.specialBenefit}"/></li>
                            </ul>
                        </div>
                    </c:if>
                </div>
            </div>

            <%-- Main Content Tabs Card --%>
            <div class="col-md-8">
                <div class="glass-card p-4">
                    <%-- Nav Pills --%>
                    <ul class="nav nav-pills mb-4" id="profileTabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link ${activeTab == 'password' ? '' : 'active'}" id="info-tab" data-bs-toggle="pill" data-bs-target="#info-pane" type="button" role="tab">
                                <i class="fa-solid fa-user-pen me-2"></i>Thông Tin Cá Nhân
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link ${activeTab == 'password' ? 'active' : ''}" id="password-tab" data-bs-toggle="pill" data-bs-target="#password-pane" type="button" role="tab">
                                <i class="fa-solid fa-key me-2"></i>Đổi Mật Khẩu
                            </button>
                        </li>
                    </ul>

                    <%-- Tab Panes --%>
                    <div class="tab-content" id="profileTabsContent">
                        <%-- TAB 1: EDIT PROFILE --%>
                        <div class="tab-pane fade ${activeTab == 'password' ? '' : 'show active'}" id="info-pane" role="tabpanel">
                            <form action="${pageContext.request.contextPath}/profile" method="POST" novalidate="true">
                                <input type="hidden" name="action" value="update-profile">

                                <div class="mb-3">
                                    <label class="form-label text-white-50 fw-semibold">Tên Đăng Nhập (Username)</label>
                                    <input type="text" class="form-control form-control-custom bg-dark text-muted" value="${not empty user ? user.username : sessionScope.LOGIN_USER.username}" disabled readonly>
                                    <div class="form-text text-muted" style="font-size: .78rem;">Username là định danh duy nhất không thể thay đổi.</div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label text-white fw-semibold">Họ và Tên <span class="text-cyan">*</span></label>
                                    <input type="text" name="fullname" class="form-control form-control-custom ${not empty errors.fullname ? 'is-invalid' : ''}" value="${not empty param.fullname ? param.fullname : (not empty user ? user.fullname : sessionScope.LOGIN_USER.fullname)}" required placeholder="Nhập họ và tên...">
                                    <c:if test="${not empty errors.fullname}">
                                        <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.fullname}</span></div>
                                    </c:if>
                                </div>

                                <div class="row g-3 mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label text-white fw-semibold">Địa Chỉ Email <span class="text-cyan">*</span></label>
                                        <input type="email" name="email" class="form-control form-control-custom ${not empty errors.email ? 'is-invalid' : ''}" value="${not empty param.email ? param.email : (not empty user ? user.email : sessionScope.LOGIN_USER.email)}" required placeholder="nhapemail@gmail.com">
                                        <c:if test="${not empty errors.email}">
                                            <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.email}</span></div>
                                        </c:if>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label text-white fw-semibold">Số Điện Thoại <span class="text-cyan">*</span></label>
                                        <input type="text" name="phone" class="form-control form-control-custom ${not empty errors.phone ? 'is-invalid' : ''}" value="${not empty param.phone ? param.phone : (not empty user ? user.phone : sessionScope.LOGIN_USER.phone)}" required placeholder="09xxxxxxxx">
                                        <c:if test="${not empty errors.phone}">
                                            <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.phone}</span></div>
                                        </c:if>
                                    </div>
                                </div>

                                <%-- DOCTOR SPECIFIC PROFESSIONAL PROFILE --%>
                                <c:if test="${sessionScope.LOGIN_USER.role == 'DOCTOR'}">
                                    <div class="p-3 rounded-3 mb-3 border border-secondary border-opacity-25" style="background: rgba(15, 23, 42, 0.5);">
                                        <h6 class="text-cyan fw-bold mb-3 d-flex align-items-center gap-2">
                                            <i class="fa-solid fa-user-doctor text-cyan"></i>Thông Tin Chuyên Môn Bác Sĩ
                                        </h6>
                                        <div class="row g-3 mb-3">
                                            <div class="col-md-6">
                                                <label class="form-label text-white-50 fw-semibold">Chuyên Khoa Điều Trị <span class="text-cyan">*</span></label>
                                                <input type="text" name="specialty" class="form-control form-control-custom ${not empty errors.specialty ? 'is-invalid' : ''}" value="${not empty param.specialty ? param.specialty : (not empty doctorProfile ? doctorProfile.specialty : '')}" placeholder="VD: Da liễu, Trị liệu Spa...">
                                                <c:if test="${not empty errors.specialty}">
                                                    <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.specialty}</span></div>
                                                </c:if>
                                            </div>
                                            <div class="col-md-3">
                                                <label class="form-label text-white-50 fw-semibold">Số Năm KN</label>
                                                <input type="number" name="experienceYears" min="0" max="60" class="form-control form-control-custom ${not empty errors.experienceYears ? 'is-invalid' : ''}" value="${not empty param.experienceYears ? param.experienceYears : (not empty doctorProfile ? doctorProfile.experienceYears : 1)}">
                                                <c:if test="${not empty errors.experienceYears}">
                                                    <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.experienceYears}</span></div>
                                                </c:if>
                                            </div>
                                            <div class="col-md-3">
                                                <label class="form-label text-white-50 fw-semibold">Phòng Khám</label>
                                                <input type="text" name="roomNumber" class="form-control form-control-custom" value="${not empty param.roomNumber ? param.roomNumber : (not empty doctorProfile ? doctorProfile.roomNumber : '')}" placeholder="Phòng P.102">
                                            </div>
                                        </div>
                                        <div class="mb-1">
                                            <label class="form-label text-white-50 fw-semibold">Giới Thiệu &amp; Tiểu Sử Chuyên Môn</label>
                                            <textarea name="bio" rows="3" class="form-control form-control-custom" placeholder="Giới thiệu về kinh nghiệm, chứng chỉ chuyên môn của Bác sĩ...">${not empty param.bio ? param.bio : (not empty doctorProfile ? doctorProfile.bio : '')}</textarea>
                                        </div>
                                    </div>
                                </c:if>

                                <button type="submit" class="btn btn-primary-gradient rounded-pill px-4 py-2 fw-bold">
                                    <i class="fa-solid fa-floppy-disk me-2"></i>Lưu Thay Đổi
                                </button>
                            </form>
                        </div>

                        <%-- TAB 2: CHANGE PASSWORD --%>
                        <div class="tab-pane fade ${activeTab == 'password' ? 'show active' : ''}" id="password-pane" role="tabpanel">
                            <form action="${pageContext.request.contextPath}/profile" method="POST" novalidate="true">
                                <input type="hidden" name="action" value="change-password">

                                <div class="mb-3">
                                    <label class="form-label text-white fw-semibold">Mật Khẩu Hiện Tại <span class="text-cyan">*</span></label>
                                    <input type="password" name="oldPassword" class="form-control form-control-custom ${not empty errors.oldPassword ? 'is-invalid' : ''}" required placeholder="Nhập mật khẩu hiện tại...">
                                    <c:if test="${not empty errors.oldPassword}">
                                        <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.oldPassword}</span></div>
                                    </c:if>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label text-white fw-semibold">Mật Khẩu Mới <span class="text-cyan">*</span></label>
                                    <input type="password" name="newPassword" class="form-control form-control-custom ${not empty errors.newPassword ? 'is-invalid' : ''}" required placeholder="Ít nhất 6 ký tự...">
                                    <c:if test="${not empty errors.newPassword}">
                                        <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.newPassword}</span></div>
                                    </c:if>
                                </div>

                                <div class="mb-4">
                                    <label class="form-label text-white fw-semibold">Xác Nhận Mật Khẩu Mới <span class="text-cyan">*</span></label>
                                    <input type="password" name="confirmPassword" class="form-control form-control-custom ${not empty errors.confirmPassword ? 'is-invalid' : ''}" required placeholder="Nhập lại mật khẩu mới...">
                                    <c:if test="${not empty errors.confirmPassword}">
                                        <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.confirmPassword}</span></div>
                                    </c:if>
                                </div>

                                <button type="submit" class="btn btn-primary-gradient rounded-pill px-4 py-2 fw-bold">
                                    <i class="fa-solid fa-key me-2"></i>Cập Nhật Mật Khẩu
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- Dynamic Footer --%>
    <jsp:include page="/WEB-INF/views/components/footer.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

