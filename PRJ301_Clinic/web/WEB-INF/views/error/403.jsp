<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 - Truy cập bị từ chối | PRJ301 Clinic</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
            color: #f8fafc;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .error-card {
            background: rgba(30, 41, 59, 0.7);
            backdrop-filter: blur(16px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 24px;
            padding: 3rem;
            max-width: 550px;
            width: 90%;
            text-align: center;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
        }
        .error-code {
            font-size: 6rem;
            font-weight: 800;
            background: linear-gradient(to right, #ef4444, #f97316);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            line-height: 1;
            margin-bottom: 1rem;
        }
        .btn-custom {
            border-radius: 12px;
            padding: 0.75rem 1.5rem;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-home {
            background: #3b82f6;
            color: white;
            border: none;
        }
        .btn-home:hover {
            background: #2563eb;
            color: white;
            transform: translateY(-2px);
        }
        .btn-login {
            background: rgba(255, 255, 255, 0.1);
            color: #f8fafc;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        .btn-login:hover {
            background: rgba(255, 255, 255, 0.2);
            color: white;
            transform: translateY(-2px);
        }
    </style>
</head>
<body>

<div class="error-card">
    <div class="error-code">403</div>
    <h3 class="mb-3 font-semibold">Truy cập bị Từ chối!</h3>
    <p class="text-slate-400 mb-4">
        Bạn không có quyền hạn truy cập vào khu vực nội bộ này. Vui lòng quay về trang chủ hoặc đăng nhập tài khoản có quyền truy cập phù hợp.
    </p>

    <div class="d-flex flex-column flex-sm-row gap-3 justify-content-center mt-4">
        <a href="${pageContext.request.contextPath}/MainController?action=home" class="btn btn-custom btn-home">
            <i class="fa-solid fa-house me-2"></i>Quay về Trang Chủ
        </a>
        <a href="${pageContext.request.contextPath}/MainController?action=login-page" class="btn btn-custom btn-login">
            <i class="fa-solid fa-right-to-bracket me-2"></i>Đăng Nhập Tài Khoản Khác
        </a>
    </div>
</div>

</body>
</html>
