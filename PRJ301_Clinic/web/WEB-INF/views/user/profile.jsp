<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Hồ Sơ Cá Nhân — PRJ301 Clinic &amp; Spa</title>
    <jsp:include page="/WEB-INF/views/components/head.jsp" />
    <style>
        .profile-avatar {
            width: 90px;
            height: 90px;
            border-radius: 50%;
            background: linear-gradient(135deg, #0ea5e9, #10b981);
            color: #ffffff;
            font-size: 2.4rem;
            font-weight: 800;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 10px 25px rgba(14, 165, 233, 0.4);
            border: 3px solid rgba(56, 189, 248, 0.4);
        }
        .nav-pills .nav-link {
            color: #94a3b8;
            border-radius: 12px;
            padding: 0.75rem 1.25rem;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .nav-pills .nav-link.active {
            background: linear-gradient(135deg, #0ea5e9, #10b981);
            color: #ffffff;
            box-shadow: 0 6px 20px rgba(14, 165, 233, 0.35);
        }
        .form-control-custom {
            background: rgba(15, 23, 42, 0.75);
            border: 1px solid rgba(255, 255, 255, 0.15);
            color: #f8fafc;
            border-radius: 12px;
            padding: 0.75rem 1rem;
        }
        .form-control-custom:focus {
            background: rgba(15, 23, 42, 0.9);
            border-color: #0ea5e9;
            color: #ffffff;
            box-shadow: 0 0 0 0.25rem rgba(14, 165, 233, 0.25);
        }
    </style>
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
            <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-outline-glass rounded-pill px-4" style="font-size: .9rem;">
                <i class="fa-solid fa-arrow-left me-2"></i>Trang Chủ
            </a>
        </div>

        <%-- Alerts --%>
        <jsp:include page="/WEB-INF/views/components/alerts.jsp" />

        <div class="row g-4">
            <%-- Sidebar Card --%>
            <div class="col-md-4">
                <div class="glass-card p-4 text-center">
                    <div class="d-flex justify-content-center mb-3">
                        <div class="profile-avatar">
                            ${sessionScope.LOGIN_USER.fullname.substring(0,1).toUpperCase()}
                        </div>
                    </div>
                    <h5 class="fw-bold text-white mb-1"><c:out value="${sessionScope.LOGIN_USER.fullname}"/></h5>
                    <p class="text-cyan mb-3 small"><c:out value="${sessionScope.LOGIN_USER.email}"/></p>

                    <div class="d-flex justify-content-center gap-2 mb-4">
                        <span class="badge bg-cyan bg-opacity-20 text-cyan border border-cyan border-opacity-30 px-3 py-2 rounded-pill fw-bold">
                            <i class="fa-solid fa-user-shield me-1"></i><c:out value="${sessionScope.LOGIN_USER.role}"/>
                        </span>
                        <span class="badge bg-success bg-opacity-20 text-emerald border border-success border-opacity-30 px-3 py-2 rounded-pill fw-bold">
                            <i class="fa-solid fa-circle-check me-1"></i>Hoạt Động
                        </span>
                    </div>

                    <div class="border-top border-secondary border-opacity-25 pt-3 text-start text-white-50 small">
                        <div class="mb-2"><i class="fa-solid fa-user me-2 text-cyan"></i>Username: <strong class="text-white"><c:out value="${sessionScope.LOGIN_USER.username}"/></strong></div>
                        <div class="mb-2"><i class="fa-solid fa-phone me-2 text-warning"></i>SĐT: <strong class="text-white"><c:out value="${sessionScope.LOGIN_USER.phone}"/></strong></div>
                        <div><i class="fa-solid fa-calendar-alt me-2 text-emerald"></i>Thành viên từ: <strong class="text-white"><c:out value="${sessionScope.LOGIN_USER.createdAt}"/></strong></div>
                    </div>
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
                            <form action="${pageContext.request.contextPath}/profile" method="POST">
                                <input type="hidden" name="action" value="update-profile">

                                <div class="mb-3">
                                    <label class="form-label text-white-50 fw-semibold">Tên Đăng Nhập (Username)</label>
                                    <input type="text" class="form-control form-control-custom bg-dark text-muted" value="${sessionScope.LOGIN_USER.username}" disabled readonly>
                                    <div class="form-text text-muted" style="font-size: .78rem;">Username là định danh duy nhất không thể thay đổi.</div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label text-white fw-semibold">Họ và Tên <span class="text-cyan">*</span></label>
                                    <input type="text" name="fullname" class="form-control form-control-custom ${not empty errors.fullname ? 'is-invalid' : ''}" value="${not empty param.fullname ? param.fullname : sessionScope.LOGIN_USER.fullname}" required placeholder="Nhập họ và tên...">
                                    <c:if test="${not empty errors.fullname}">
                                        <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.fullname}</span></div>
                                    </c:if>
                                </div>

                                <div class="row g-3 mb-4">
                                    <div class="col-md-6">
                                        <label class="form-label text-white fw-semibold">Địa Chỉ Email <span class="text-cyan">*</span></label>
                                        <input type="email" name="email" class="form-control form-control-custom ${not empty errors.email ? 'is-invalid' : ''}" value="${not empty param.email ? param.email : sessionScope.LOGIN_USER.email}" required placeholder="nhapemail@gmail.com">
                                        <c:if test="${not empty errors.email}">
                                            <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.email}</span></div>
                                        </c:if>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label text-white fw-semibold">Số Điện Thoại <span class="text-cyan">*</span></label>
                                        <input type="text" name="phone" class="form-control form-control-custom ${not empty errors.phone ? 'is-invalid' : ''}" value="${not empty param.phone ? param.phone : sessionScope.LOGIN_USER.phone}" required placeholder="09xxxxxxxx">
                                        <c:if test="${not empty errors.phone}">
                                            <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.phone}</span></div>
                                        </c:if>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-primary-gradient rounded-pill px-4 py-2 fw-bold">
                                    <i class="fa-solid fa-floppy-disk me-2"></i>Lưu Thay Đổi
                                </button>
                            </form>
                        </div>

                        <%-- TAB 2: CHANGE PASSWORD --%>
                        <div class="tab-pane fade ${activeTab == 'password' ? 'show active' : ''}" id="password-pane" role="tabpanel">
                            <form action="${pageContext.request.contextPath}/profile" method="POST">
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

