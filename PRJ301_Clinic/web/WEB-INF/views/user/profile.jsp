<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ Sơ Cá Nhân — PRJ301 Clinic &amp; Spa</title>
    
    <%-- Bootstrap 5 & FontAwesome 6 & Fonts --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/dashboard.css" rel="stylesheet">
    
    <style>
        body {
            font-family: 'Plus Jakarta Sans', sans-serif;
            background: #090d16;
            color: #f8fafc;
            min-height: 100vh;
        }
        .glass-card {
            background: rgba(30, 41, 59, 0.65);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid rgba(255, 255, 255, 0.12);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
        }
        .profile-avatar {
            width: 90px;
            height: 90px;
            border-radius: 50%;
            background: linear-gradient(135deg, #0ea5e9, #6366f1);
            color: #ffffff;
            font-size: 2.4rem;
            font-weight: 800;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 10px 25px rgba(14, 165, 233, 0.4);
            border: 3px solid rgba(255, 255, 255, 0.2);
        }
        .nav-pills .nav-link {
            color: #94a3b8;
            border-radius: 12px;
            padding: 0.75rem 1.25rem;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .nav-pills .nav-link.active {
            background: linear-gradient(135deg, #0ea5e9, #3b82f6);
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
<body class="py-5">

    <div class="container" style="max-width: 900px;">
        <%-- Back Link --%>
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-outline-light rounded-pill px-4" style="font-size: .9rem;">
                <i class="fa-solid fa-arrow-left me-2"></i>Quay Về Trang Chủ
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
                    <p class="text-white-50 mb-3" style="font-size: .88rem;"><c:out value="${sessionScope.LOGIN_USER.email}"/></p>

                    <div class="d-flex justify-content-center gap-2 mb-4">
                        <span class="badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-2 rounded-pill fw-bold">
                            <i class="fa-solid fa-user-shield me-1"></i><c:out value="${sessionScope.LOGIN_USER.role}"/>
                        </span>
                        <span class="badge bg-success-subtle text-success border border-success-subtle px-3 py-2 rounded-pill fw-bold">
                            <i class="fa-solid fa-check-circle me-1"></i>Active
                        </span>
                    </div>

                    <div class="border-top border-secondary pt-3 text-start text-white-50" style="font-size: .84rem;">
                        <div class="mb-2"><i class="fa-solid fa-user me-2 text-info"></i>Username: <strong class="text-white"><c:out value="${sessionScope.LOGIN_USER.username}"/></strong></div>
                        <div class="mb-2"><i class="fa-solid fa-phone me-2 text-warning"></i>SĐT: <strong class="text-white"><c:out value="${sessionScope.LOGIN_USER.phone}"/></strong></div>
                        <div><i class="fa-solid fa-calendar-alt me-2 text-success"></i>Thành viên từ: <strong class="text-white"><c:out value="${sessionScope.LOGIN_USER.createdAt}"/></strong></div>
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
                                    <div class="form-text text-muted" style="font-size: .78rem;">Username là cố định không thể thay đổi.</div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label text-white fw-semibold">Họ và Tên <span class="text-danger">*</span></label>
                                    <input type="text" name="fullname" class="form-control form-control-custom" value="${sessionScope.LOGIN_USER.fullname}" required placeholder="Nhập họ và tên...">
                                </div>

                                <div class="row g-3 mb-4">
                                    <div class="col-md-6">
                                        <label class="form-label text-white fw-semibold">Địa Chỉ Email <span class="text-danger">*</span></label>
                                        <input type="email" name="email" class="form-control form-control-custom" value="${sessionScope.LOGIN_USER.email}" required placeholder="nhapemail@gmail.com">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label text-white fw-semibold">Số Điện Thoại <span class="text-danger">*</span></label>
                                        <input type="text" name="phone" class="form-control form-control-custom" value="${sessionScope.LOGIN_USER.phone}" required placeholder="09xxxxxxxx">
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-primary rounded-pill px-4 py-2 fw-bold" style="background: linear-gradient(135deg, #0ea5e9, #3b82f6); border: none;">
                                    <i class="fa-solid fa-floppy-disk me-2"></i>Lưu Thay Đổi
                                </button>
                            </form>
                        </div>

                        <%-- TAB 2: CHANGE PASSWORD --%>
                        <div class="tab-pane fade ${activeTab == 'password' ? 'show active' : ''}" id="password-pane" role="tabpanel">
                            <form action="${pageContext.request.contextPath}/profile" method="POST">
                                <input type="hidden" name="action" value="change-password">

                                <div class="mb-3">
                                    <label class="form-label text-white fw-semibold">Mật Khẩu Hiện Tại <span class="text-danger">*</span></label>
                                    <input type="password" name="oldPassword" class="form-control form-control-custom" required placeholder="Nhập mật khẩu cũ...">
                                </div>

                                <div class="mb-3">
                                    <label class="form-label text-white fw-semibold">Mật Khẩu Mới <span class="text-danger">*</span></label>
                                    <input type="password" name="newPassword" class="form-control form-control-custom" required placeholder="Ít nhất 6 ký tự...">
                                </div>

                                <div class="mb-4">
                                    <label class="form-label text-white fw-semibold">Xác Nhận Mật Khẩu Mới <span class="text-danger">*</span></label>
                                    <input type="password" name="confirmPassword" class="form-control form-control-custom" required placeholder="Nhập lại mật khẩu mới...">
                                </div>

                                <button type="submit" class="btn btn-warning rounded-pill px-4 py-2 fw-bold text-dark">
                                    <i class="fa-solid fa-key me-2"></i>Cập Nhật Mật Khẩu
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- Bootstrap JS --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
