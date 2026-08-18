/**
 * booking.js - Logic Đặt Lịch Khám Bệnh Nhân & AJAX Khung Giờ Khám (Slots)
 * Context path inject: window.BOOKING_CTX
 * Phụ thuộc: Flatpickr, SweetAlert2
 */

'use strict';

var fpInstance = null;

// =====================================================================
// 1. KHỞI TẠO FLATPICKR (Vô hiệu hóa Chủ Nhật)
// =====================================================================
document.addEventListener('DOMContentLoaded', function () {
    var dateInput = document.getElementById('appointmentDate');
    var initialDate = (dateInput && dateInput.value) ? dateInput.value : getNextWorkday(new Date());

    if (typeof flatpickr !== 'undefined' && dateInput) {
        var localeOption = (flatpickr.l10ns && flatpickr.l10ns.vn) ? flatpickr.l10ns.vn : 'default';
        fpInstance = flatpickr('#appointmentDate', {
            locale: localeOption,
            dateFormat: 'Y-m-d',
            defaultDate: initialDate,
            altInput: true,
            altFormat: 'd/m/Y',
            altInputClass: 'form-control form-control-glass border-start-0 ps-0',
            minDate: 'today',
            disableMobile: true,
            disable: [
                function (date) {
                    return date.getDay() === 0; // 0 = Chủ Nhật (Phòng khám nghỉ)
                }
            ],
            onChange: function (selectedDates, dateStr) {
                updateStepProgress();
                fetchSlots();
            }
        });
    }

    // Nạp trạng thái ban đầu
    updateStepProgress();
    fetchSlots();
});

// =====================================================================
// 2. LẤY NGÀY LÀM VIỆC KẾ TIẾP (Bỏ qua Chủ Nhật)
// =====================================================================
function getNextWorkday(date) {
    var d = new Date(date);
    if (d.getDay() === 0) {
        d.setDate(d.getDate() + 1);
    }
    return formatDateISO(d);
}

// =====================================================================
// 3. HELPER FORMAT DATE YYYY-MM-DD
// =====================================================================
function formatDateISO(d) {
    var year = d.getFullYear();
    var month = String(d.getMonth() + 1).padStart(2, '0');
    var day = String(d.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
}

// =====================================================================
// 4. NÚT CHỌN NHANH NGÀY (Hôm nay / Ngày mai / Ngày kia)
// =====================================================================
function setQuickDate(daysToAdd) {
    var d = new Date();
    d.setDate(d.getDate() + daysToAdd);
    if (d.getDay() === 0) {
        d.setDate(d.getDate() + 1); // Bỏ qua Chủ Nhật sang Thứ Hai
    }

    var dateStr = formatDateISO(d);

    var input = document.getElementById('appointmentDate');
    if (input) {
        input.value = dateStr;
    }

    if (fpInstance) {
        fpInstance.setDate(dateStr, true); // true = kích hoạt event onChange
    } else {
        updateStepProgress();
        fetchSlots();
    }
}

// =====================================================================
// 5. LẤY GIÁ TRỊ NGÀY ĐANG CHỌN (An toàn với Flatpickr)
// =====================================================================
function getSelectedDateStr() {
    if (fpInstance && fpInstance.selectedDates && fpInstance.selectedDates.length > 0) {
        return formatDateISO(fpInstance.selectedDates[0]);
    }
    var input = document.getElementById('appointmentDate');
    return input ? input.value.trim() : '';
}

// =====================================================================
// 6. CẬP NHẬT THANH TIẾN TRÌNH 4 BƯỚC (STEP PROGRESS)
// =====================================================================
function updateStepProgress() {
    var _s = document.getElementById('serviceSelect');
    var _d = document.getElementById('doctorSelect');
    var _sl = document.getElementById('selectedScheduleId');

    var sVal = _s ? _s.value : '';
    var dVal = _d ? _d.value : '';
    var dateVal = getSelectedDateStr();
    var slotVal = _sl ? _sl.value : '';

    function setStep(id, active) {
        var el = document.getElementById(id);
        if (!el) return;
        el.className = active
            ? 'p-2 rounded-3 bg-cyan bg-opacity-20 text-cyan fw-bold border border-cyan border-opacity-30'
            : 'p-2 rounded-3 bg-dark bg-opacity-50 text-muted border border-secondary border-opacity-25';
    }

    setStep('step1Box', !!sVal);
    setStep('step2Box', !!dVal);
    setStep('step3Box', !!dateVal);
    setStep('step4Box', !!slotVal);
}

// =====================================================================
// 7. CHỌN CA KHÁM (SLOT BUTTON)
// =====================================================================
function selectSlot(scheduleId, btnElement) {
    var hiddenInput = document.getElementById('selectedScheduleId');
    if (hiddenInput) {
        hiddenInput.value = scheduleId;
    }

    document.querySelectorAll('.slot-pill').forEach(function (btn) {
        if (!btn.disabled) {
            btn.className = 'btn w-100 py-2.5 slot-pill slot-btn-available';
        }
    });

    if (btnElement) {
        btnElement.className = 'btn w-100 py-2.5 slot-pill slot-btn-selected';
    }

    updateStepProgress();
}

// =====================================================================
// 8. FETCH DANH SÁCH CA KHÁM CỦA BÁC SĨ (AJAX)
// =====================================================================
function fetchSlots() {
    var _dr = document.getElementById('doctorSelect');
    var doctorId = _dr ? _dr.value : '';
    var date = getSelectedDateStr();
    var grid = document.getElementById('slotMatrixGrid') || document.getElementById('slotsContainer');
    var ctx = window.BOOKING_CTX || '';
    var currentScheduleId = document.getElementById('selectedScheduleId') ? document.getElementById('selectedScheduleId').value : '';

    if (!grid) return;

    if (!doctorId || !date) {
        grid.innerHTML = '<div class="col-12 text-center text-muted py-3 border border-dashed rounded-3"'
            + ' style="border-color:rgba(255,255,255,0.1)!important;">'
            + '<i class="fa-solid fa-info-circle me-1 text-cyan"></i>'
            + 'Vui lòng chọn Bác sĩ và Ngày khám để nạp sơ đồ ca khám khả dụng.'
            + '</div>';
        return;
    }

    grid.innerHTML = '<div class="col-12 text-center text-cyan py-3">'
        + '<i class="fa-solid fa-spinner fa-spin me-2"></i>'
        + 'Đang nạp danh sách ca khám từ CSDL y tế…'
        + '</div>';

    var url = ctx + '/booking?action=get-slots&doctorId=' + encodeURIComponent(doctorId) + '&date=' + encodeURIComponent(date);

    fetch(url)
        .then(function (res) { return res.json(); })
        .then(function (slots) {
            if (!slots || slots.length === 0) {
                grid.innerHTML = '<div class="col-12 text-center text-warning py-3 border border-dashed rounded-3"'
                    + ' style="border-color:rgba(255,255,255,0.1)!important;">'
                    + '<i class="fa-solid fa-triangle-exclamation me-1"></i>'
                    + 'Bác sĩ chưa có ca làm việc nào trong ngày này hoặc phòng khám chưa mở ca.'
                    + '</div>';
                return;
            }

            var html = '';
            var hasSelected = false;

            slots.forEach(function (slot) {
                var isAvailable = (slot.isAvailable === true || slot.isAvailable === 1 || slot.isAvailable === 'true');
                var isSelected = (currentScheduleId && String(slot.id) === String(currentScheduleId));
                if (isSelected) hasSelected = true;

                var btnClass = isSelected
                    ? 'slot-btn-selected'
                    : (isAvailable ? 'slot-btn-available' : 'slot-btn-booked');

                var icon = isSelected
                    ? 'fa-solid fa-circle-check text-cyan'
                    : (isAvailable ? 'fa-regular fa-clock text-emerald' : 'fa-solid fa-lock text-muted');

                var disabledAttr = (isAvailable || isSelected) ? '' : 'disabled';
                var statusText = isAvailable ? '' : ' (Đã đặt)';
                var startStr = slot.startTime ? slot.startTime.substring(0, 5) : '';
                var endStr = slot.endTime ? slot.endTime.substring(0, 5) : '';

                html += '<div class="col-6 col-md-4 col-lg-3">'
                    + '<button type="button" class="btn w-100 py-2.5 slot-pill ' + btnClass + '" '
                    + 'onclick="selectSlot(' + slot.id + ', this)" ' + disabledAttr + '>'
                    + '<i class="' + icon + ' me-1.5"></i><strong>' + startStr + '</strong> - ' + endStr + '<small class="d-block opacity-75">' + statusText + '</small>'
                    + '</button></div>';
            });

            grid.innerHTML = html;

            if (!hasSelected && currentScheduleId) {
                // Ca đã chọn trước đó không khả dụng trong ngày/bác sĩ mới
                var hiddenInput = document.getElementById('selectedScheduleId');
                if (hiddenInput) hiddenInput.value = '';
                updateStepProgress();
            }
        })
        .catch(function (err) {
            console.error('Lỗi khi fetch slots:', err);
            grid.innerHTML = '<div class="col-12 text-center text-danger py-3 border border-dashed rounded-3" style="border-color:rgba(239,68,68,0.3)!important;">'
                + '<i class="fa-solid fa-triangle-exclamation me-1"></i>'
                + 'Không thể nạp lịch khám. Vui lòng thử lại sau.'
                + '</div>';
        });
}

// =====================================================================
// 9. VALIDATE FORM TRƯỚC KHI SUBMIT (CUSTOM UI VALIDATION)
// =====================================================================
function validateBookingForm(event) {
    var _svc = document.getElementById('serviceSelect');
    var _doc = document.getElementById('doctorSelect');
    var _sch = document.getElementById('selectedScheduleId');

    var serviceId = _svc ? _svc.value.trim() : '';
    var doctorId = _doc ? _doc.value.trim() : '';
    var apptDate = getSelectedDateStr();
    var scheduleId = _sch ? _sch.value.trim() : '';

    if (!serviceId) {
        showToast('Vui lòng chọn Dịch vụ khám hoặc Spa!');
        if (_svc) _svc.focus();
        if (event) event.preventDefault();
        return false;
    }
    if (!doctorId) {
        showToast('Vui lòng chọn Bác sĩ phụ trách!');
        if (_doc) _doc.focus();
        if (event) event.preventDefault();
        return false;
    }
    if (!apptDate) {
        showToast('Vui lòng chọn Ngày khám mong muốn!');
        if (event) event.preventDefault();
        return false;
    }
    if (!scheduleId) {
        showToast('Vui lòng bấm chọn một Ca khám 60 phút khả dụng (màu xanh)!');
        if (event) event.preventDefault();
        return false;
    }

    var chosen = new Date(apptDate);
    if (chosen.getDay() === 0) {
        showToast('Phòng khám không làm việc vào Chủ Nhật. Vui lòng chọn ngày khác!');
        if (event) event.preventDefault();
        return false;
    }

    var submitBtn = document.getElementById('submitBookingBtn');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-2"></i>Đang khởi tạo đơn hẹn y tế…';
    }
    return true;
}

// =====================================================================
// 10. TOAST NOTIFICATION HELPER
// =====================================================================
if (typeof showToast === 'undefined') {
    window.showToast = function (msg) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3500,
                timerProgressBar: true,
                icon: 'warning',
                title: msg,
                background: '#1e293b',
                color: '#fff'
            });
        } else {
            alert(msg);
        }
    };
}
