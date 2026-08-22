<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- Google Fonts: Be Vietnam Pro, Plus Jakarta Sans & Inter -->
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:ital,wght@0,300;0,400;0,500;0,600;0,700;0,800;1,400;1,600;1,700&family=Inter:wght@300;400;500;600;700;800&family=Plus+Jakarta+Sans:ital,wght@0,400;0,500;0,600;0,700;0,800;1,400&display=swap" rel="stylesheet">

<!-- Bootstrap 5.3 CSS CDN -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

<!-- FontAwesome 6.4 CSS CDN -->
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">

<!-- Flatpickr Custom Datepicker CSS & Dark Theme -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/themes/dark.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/vn.js"></script>
<!-- SweetAlert2 CDN & Dark Theme -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<!-- Global Design System & Component Stylesheets -->
<link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/css/sidebar.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/css/dashboard.css" rel="stylesheet">

<!-- Context Path Global Config & Notifications -->
<script>
    window.CONTEXT_PATH = "${pageContext.request.contextPath}";
    window.APP_CONTEXT_PATH = "${pageContext.request.contextPath}";
    try { localStorage.removeItem("PRJ301_APP_LANG"); } catch (e) {}

    function confirmLogout(event) {
        if (event) event.preventDefault();
        const logoutUrl = (window.CONTEXT_PATH || '') + '/logout';
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                title: 'Xác Nhận Đăng Xuất',
                text: 'Bạn có chắc chắn muốn đăng xuất khỏi tài khoản không?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#ef4444',
                cancelButtonColor: '#475569',
                confirmButtonText: '<i class="fa-solid fa-right-from-bracket me-1"></i> Đăng Xuất',
                cancelButtonText: '<i class="fa-solid fa-xmark me-1"></i> Hủy Bỏ',
                background: '#0b1628',
                color: '#ffffff',
                customClass: {
                    popup: 'glass-card border border-secondary border-opacity-25 rounded-4 shadow-lg'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = logoutUrl;
                }
            });
        } else {
            if (confirm('Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?')) {
                window.location.href = logoutUrl;
            }
        }
        return false;
    }
</script>
<script src="${pageContext.request.contextPath}/assets/js/notifications.js" defer></script>


