<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="footer-glass mt-auto py-5 border-top" style="border-color: rgba(56, 189, 248, 0.15) !important; background: rgba(11, 19, 43, 0.95);">
    <div class="container">
        <div class="row g-4">
            <%-- Cột 1: Thông tin phòng khám --%>
            <div class="col-lg-4">
                <div class="d-flex align-items-center gap-2 fw-bold text-white fs-4 mb-3">
                    <div class="brand-icon-box d-flex align-items-center justify-content-center rounded-3 text-white shadow-sm" style="width: 38px; height: 38px; background: linear-gradient(135deg, #0ea5e9 0%, #10b981 100%);">
                        <i class="fa-solid fa-heart-pulse fs-6"></i>
                    </div>
                    <span class="fw-bold text-white" data-i18n="nav_brand">Phòng Khám &amp; Spa PRJ301</span>
                </div>
                <p class="text-muted small mb-3" data-i18n="footer_clinic_desc">
                    Hệ thống Phòng Khám Nha Khoa &amp; Spa Y Khoa Quốc Tế Hàng Đầu. Đội ngũ y bác sĩ chứng chỉ hành nghề, ứng dụng trang thiết bị y khoa hiện đại.
                </p>
                <div class="d-flex align-items-center gap-2 mb-3">
                    <span class="badge bg-success bg-opacity-20 text-emerald border border-success border-opacity-30 rounded-pill px-3 py-2 fs-7" data-i18n="footer_jci">
                        <i class="fa-solid fa-shield-check me-1"></i>Đạt Chuẩn Y Tế Quốc Tế JCI
                    </span>
                </div>
                <div class="d-flex flex-wrap gap-2 pt-1">
                    <a href="https://facebook.com" target="_blank" rel="noopener noreferrer" class="btn btn-xs rounded-circle p-0 d-flex align-items-center justify-content-center border border-secondary border-opacity-30 text-white-hover" style="width: 36px; height: 36px; background: rgba(255,255,255,0.05);" title="Facebook">
                        <i class="fa-brands fa-facebook-f text-cyan"></i>
                    </a>
                    <a href="https://youtube.com" target="_blank" rel="noopener noreferrer" class="btn btn-xs rounded-circle p-0 d-flex align-items-center justify-content-center border border-secondary border-opacity-30 text-white-hover" style="width: 36px; height: 36px; background: rgba(255,255,255,0.05);" title="YouTube">
                        <i class="fa-brands fa-youtube text-danger"></i>
                    </a>
                    <a href="https://tiktok.com" target="_blank" rel="noopener noreferrer" class="btn btn-xs rounded-circle p-0 d-flex align-items-center justify-content-center border border-secondary border-opacity-30 text-white-hover" style="width: 36px; height: 36px; background: rgba(255,255,255,0.05);" title="TikTok">
                        <i class="fa-brands fa-tiktok text-white"></i>
                    </a>
                    <a href="https://instagram.com" target="_blank" rel="noopener noreferrer" class="btn btn-xs rounded-circle p-0 d-flex align-items-center justify-content-center border border-secondary border-opacity-30 text-white-hover" style="width: 36px; height: 36px; background: rgba(255,255,255,0.05);" title="Instagram">
                        <i class="fa-brands fa-instagram" style="color: #e4405f;"></i>
                    </a>
                    <a href="https://zalo.me" target="_blank" rel="noopener noreferrer" class="btn btn-xs rounded-circle p-0 d-flex align-items-center justify-content-center border border-secondary border-opacity-30 text-white-hover" style="width: 36px; height: 36px; background: rgba(255,255,255,0.05);" title="Zalo Chat">
                        <i class="fa-solid fa-comment-dots text-cyan"></i>
                    </a>
                    <a href="https://t.me" target="_blank" rel="noopener noreferrer" class="btn btn-xs rounded-circle p-0 d-flex align-items-center justify-content-center border border-secondary border-opacity-30 text-white-hover" style="width: 36px; height: 36px; background: rgba(255,255,255,0.05);" title="Telegram">
                        <i class="fa-brands fa-telegram" style="color: #24a1de;"></i>
                    </a>
                    <a href="https://linkedin.com" target="_blank" rel="noopener noreferrer" class="btn btn-xs rounded-circle p-0 d-flex align-items-center justify-content-center border border-secondary border-opacity-30 text-white-hover" style="width: 36px; height: 36px; background: rgba(255,255,255,0.05);" title="LinkedIn">
                        <i class="fa-brands fa-linkedin-in" style="color: #0a66c2;"></i>
                    </a>
                    <a href="mailto:support@clinic.vn" class="btn btn-xs rounded-circle p-0 d-flex align-items-center justify-content-center border border-secondary border-opacity-30 text-white-hover" style="width: 36px; height: 36px; background: rgba(255,255,255,0.05);" title="Email">
                        <i class="fa-solid fa-envelope text-emerald"></i>
                    </a>
                </div>
            </div>

            <%-- Cột 2: Điều hướng nhanh --%>
            <div class="col-6 col-lg-2">
                <h6 class="fw-bold text-white mb-3" data-i18n="footer_quick_links">Liên Kết Nhanh</h6>
                <ul class="list-unstyled small d-flex flex-column gap-2 mb-0">
                    <li><a href="${pageContext.request.contextPath}/MainController?action=home" class="text-white-50 text-white-hover text-decoration-none" data-i18n="nav_home">Trang Chủ</a></li>
                    <li><a href="${pageContext.request.contextPath}/MainController?action=home#services" class="text-white-50 text-white-hover text-decoration-none" data-i18n="nav_services">Dịch Vụ &amp; Spa</a></li>
                    <li><a href="${pageContext.request.contextPath}/MainController?action=home#doctors" class="text-white-50 text-white-hover text-decoration-none" data-i18n="nav_doctors">Đội Ngũ Bác Sĩ</a></li>
                    <li><a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="text-white-50 text-white-hover text-decoration-none" data-i18n="btn_booking">Đặt Lịch Khám</a></li>
                </ul>
            </div>

            <%-- Cột 3: Giờ làm việc --%>
            <div class="col-6 col-lg-3">
                <h6 class="fw-bold text-white mb-3" data-i18n="footer_working_hours">Giờ Làm Việc Y Tế</h6>
                <ul class="list-unstyled small d-flex flex-column gap-2 text-white-50 mb-0">
                    <li><i class="fa-regular fa-clock me-2 text-cyan"></i>${not empty clinicSettings['OPENING_HOURS'] ? clinicSettings['OPENING_HOURS'] : '08:00 - 20:00 (Từ Thứ 2 đến Chủ Nhật)'}</li>
                    <li class="text-emerald fw-semibold"><i class="fa-solid fa-circle-check me-2"></i><span data-i18n="footer_open_holidays">Mở cửa khám xuyên Tết &amp; Lễ</span></li>
                </ul>
            </div>

            <%-- Cột 4: Liên hệ --%>
            <div class="col-lg-3">
                <h6 class="fw-bold text-white mb-3" data-i18n="footer_contact">Liên Hệ Khám Bệnh</h6>
                <ul class="list-unstyled small d-flex flex-column gap-2 text-white-50 mb-0">
                    <li><i class="fa-solid fa-location-dot me-2 text-cyan"></i>${not empty clinicSettings['CLINIC_ADDRESS'] ? clinicSettings['CLINIC_ADDRESS'] : '123 Đường Nguyễn Văn Cừ, Quận 5, TP.HCM'}</li>
                    <li><i class="fa-solid fa-phone me-2 text-cyan"></i><span data-i18n="footer_hotline_label">Hotline Khám:</span> <strong class="text-white">${not empty clinicSettings['CLINIC_HOTLINE'] ? clinicSettings['CLINIC_HOTLINE'] : '0901 234 567'}</strong></li>
                    <li><i class="fa-solid fa-qrcode me-2 text-emerald"></i><span data-i18n="footer_sepay_auto">Thanh toán tự động VietQR SePay</span></li>
                </ul>
            </div>
        </div>

        <hr class="my-4 border-secondary opacity-25">

        <div class="d-flex flex-wrap justify-content-between align-items-center small text-white-50">
            <p class="mb-0" data-i18n="footer_copyright">&copy; 2026 PRJ301 Clinic &amp; Spa Y Khoa. Tất cả quyền được bảo lưu.</p>
            <p class="mb-0" data-i18n="footer_sub">Hệ Thống Đặt Lịch Y Tế Thông Minh - Đồ Án PRJ301 FPT University</p>
        </div>
    </div>
</footer>

