<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="footer-glass mt-auto py-5 border-top" style="border-color: rgba(255, 255, 255, 0.1) !important;">
    <div class="container">
        <div class="row g-4">
            <%-- Cột 1: Thông tin phòng khám --%>
            <div class="col-lg-4">
                <div class="d-flex align-items-center gap-2 fw-bold text-white fs-4 mb-3">
                    <div class="brand-icon-box d-flex align-items-center justify-content-center rounded-3 text-white shadow-sm" style="width: 36px; height: 36px; background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);">
                        <i class="fa-solid fa-notes-medical fs-6"></i>
                    </div>
                    <span class="fw-bold text-white">PRJ301 <span class="text-info">Clinic & Spa</span></span>
                </div>
                <p class="text-muted small mb-3">
                    Hệ thống Phòng Khám Nha Khoa & Spa Y Khoa Quốc Tế Hàng Đầu. Ứng dụng công nghệ hiện đại mang lại nụ cười rạng rỡ và làn da căng mịn.
                </p>
                <div class="d-flex gap-3">
                    <a href="#" class="text-white-50 text-white-hover fs-5"><i class="fa-brands fa-facebook"></i></a>
                    <a href="#" class="text-white-50 text-white-hover fs-5"><i class="fa-brands fa-youtube"></i></a>
                    <a href="#" class="text-white-50 text-white-hover fs-5"><i class="fa-brands fa-tiktok"></i></a>
                    <a href="#" class="text-white-50 text-white-hover fs-5"><i class="fa-solid fa-envelope"></i></a>
                </div>
            </div>

            <%-- Cột 2: Điều hướng nhanh --%>
            <div class="col-6 col-lg-2">
                <h6 class="fw-bold text-white mb-3">Liên Kết Nhanh</h6>
                <ul class="list-unstyled small d-flex flex-column gap-2 mb-0">
                    <li><a href="${pageContext.request.contextPath}/MainController?action=home" class="text-white-50 text-white-hover text-decoration-none">Trang Chủ</a></li>
                    <li><a href="${pageContext.request.contextPath}/MainController?action=home#services" class="text-white-50 text-white-hover text-decoration-none">Dịch Vụ & Spa</a></li>
                    <li><a href="${pageContext.request.contextPath}/MainController?action=home#doctors" class="text-white-50 text-white-hover text-decoration-none">Đội Ngũ Bác Sĩ</a></li>
                    <li><a href="${pageContext.request.contextPath}/MainController?action=booking-page" class="text-white-50 text-white-hover text-decoration-none">Đặt Lịch Khám</a></li>
                </ul>
            </div>

            <%-- Cột 3: Giờ làm việc --%>
            <div class="col-6 col-lg-3">
                <h6 class="fw-bold text-white mb-3">Giờ Làm Việc</h6>
                <ul class="list-unstyled small d-flex flex-column gap-2 text-white-50 mb-0">
                    <li><i class="fa-regular fa-clock me-2 text-info"></i>Thứ 2 - Thứ 6: 08:00 - 20:00</li>
                    <li><i class="fa-regular fa-clock me-2 text-info"></i>Thứ 7 - Chủ Nhật: 08:00 - 17:00</li>
                    <li class="text-success fw-semibold"><i class="fa-solid fa-circle-check me-2"></i>Mở cửa tất cả ngày lễ</li>
                </ul>
            </div>

            <%-- Cột 4: Liên hệ --%>
            <div class="col-lg-3">
                <h6 class="fw-bold text-white mb-3">Liên Hệ Trực Tiếp</h6>
                <ul class="list-unstyled small d-flex flex-column gap-2 text-white-50 mb-0">
                    <li><i class="fa-solid fa-location-dot me-2 text-info"></i>123 Đường Nguyễn Văn Cừ, Quận 5, TP.HCM</li>
                    <li><i class="fa-solid fa-phone me-2 text-info"></i>Hotline: <strong class="text-white">0901 234 567</strong></li>
                    <li><i class="fa-solid fa-headset me-2 text-info"></i>Hỗ trợ 24/7 qua VietQR SePay</li>
                </ul>
            </div>
        </div>

        <hr class="my-4 border-secondary opacity-25">

        <div class="d-flex flex-wrap justify-content-between align-items-center small text-white-50">
            <p class="mb-0">&copy; 2026 PRJ301 Clinic & Spa. Tất cả quyền được bảo lưu.</p>
            <p class="mb-0">Đồ Án Môn PRJ301 - Đại Học FPT</p>
        </div>
    </div>
</footer>
