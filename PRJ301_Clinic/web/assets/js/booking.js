/**
 * booking.js - Logic Dat Lich Kham Benh Nhan & AJAX Khung Gio Kham (Slots)
 * Context path inject: window.BOOKING_CTX
 * Phu thuoc: Flatpickr, SweetAlert2
 * Chu y: 100% Tieng Viet duoc escape sang Unicode de khong bi loi font tren bat ky Browser/Server nao.
 */

'use strict';

var fpInstance = null;

// =====================================================================
// 1. KHOI TAO FLATPICKR (Vo hieu hoa Chu Nhat)
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
                    return date.getDay() === 0; // 0 = Chu Nhat (Phong kham nghi)
                }
            ],
            onChange: function () {
                updateStepProgress();
                fetchSlots();
            }
        });
    }

    // Nap trang thai ban dau
    updateStepProgress();
    fetchSlots();
});

// =====================================================================
// 2. LAY NGAY LAM VIEC KE TIEP (Bo qua Chu Nhat)
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
// 4. NUT CHON NHANH NGAY (Hom nay / Ngay mai / Ngay kia)
// =====================================================================
function setQuickDate(daysToAdd) {
    var d = new Date();
    d.setDate(d.getDate() + daysToAdd);
    if (d.getDay() === 0) {
        d.setDate(d.getDate() + 1); // Bo qua Chu Nhat sang Thu Hai
    }

    var dateStr = formatDateISO(d);

    var input = document.getElementById('appointmentDate');
    if (input) {
        input.value = dateStr;
    }

    if (fpInstance) {
        fpInstance.setDate(dateStr, true); // true = kich hoat event onChange
    } else {
        updateStepProgress();
        fetchSlots();
    }
}

// =====================================================================
// 5. LAY GIA TRI NGAY DANG CHON (An toan voi Flatpickr)
// =====================================================================
function getSelectedDateStr() {
    if (fpInstance && fpInstance.selectedDates && fpInstance.selectedDates.length > 0) {
        return formatDateISO(fpInstance.selectedDates[0]);
    }
    var input = document.getElementById('appointmentDate');
    return input ? input.value.trim() : '';
}

// =====================================================================
// 6. CAP NHAT THANH TIEN TRINH 4 BUOC (STEP PROGRESS)
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
// 7. CHON CA KHAM (SLOT BUTTON)
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
// 8. FETCH DANH SACH CA KHAM CUA BAC SI (AJAX)
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
            + 'Vui l\u00f2ng ch\u1ecdn B\u00e1c s\u0129 v\u00e0 Ng\u00e0y kh\u00e1m \u0111\u1ec3 n\u1ea1p s\u01a1 \u0111\u1ed3 ca kh\u00e1m kh\u1ea3 d\u1ee5ng.'
            + '</div>';
        return;
    }

    grid.innerHTML = '<div class="col-12 text-center text-cyan py-3">'
        + '<i class="fa-solid fa-spinner fa-spin me-2"></i>'
        + '\u0110ang n\u1ea1p danh s\u00e1ch ca kh\u00e1m t\u1eeb CSDL y t\u1ebf\u2026'
        + '</div>';

    var url = ctx + '/booking?action=get-slots&doctorId=' + encodeURIComponent(doctorId) + '&date=' + encodeURIComponent(date);

    fetch(url)
        .then(function (res) { return res.json(); })
        .then(function (slots) {
            if (!slots || slots.length === 0) {
                grid.innerHTML = '<div class="col-12 text-center text-warning py-3 border border-dashed rounded-3"'
                    + ' style="border-color:rgba(255,255,255,0.1)!important;">'
                    + '<i class="fa-solid fa-triangle-exclamation me-1"></i>'
                    + 'B\u00e1c s\u0129 ch\u01b0a c\u00f3 ca l\u00e0m vi\u1ec7c n\u00e0o trong ng\u00e0y n\u00e0y.'
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
                var statusText = isAvailable ? '' : ' (\u0110\u00e3 \u0111\u1eb7t)';
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
                var hiddenInput = document.getElementById('selectedScheduleId');
                if (hiddenInput) hiddenInput.value = '';
                updateStepProgress();
            }
        })
        .catch(function (err) {
            console.error('Loi khi fetch slots:', err);
            grid.innerHTML = '<div class="col-12 text-center text-danger py-3 border border-dashed rounded-3" style="border-color:rgba(239,68,68,0.3)!important;">'
                + '<i class="fa-solid fa-triangle-exclamation me-1"></i>'
                + 'Kh\u00f4ng th\u1ec3 n\u1ea1p l\u1ecbch kh\u00e1m. Vui l\u00f2ng th\u1eed l\u1ea1i sau.'
                + '</div>';
        });
}

// =====================================================================
// 9. VALIDATE FORM TRUOC KHI SUBMIT (CUSTOM UI VALIDATION)
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
        showToast('Vui l\u00f2ng ch\u1ecdn D\u1ecbch v\u1ee5 kh\u00e1m ho\u1eb7c Spa!');
        if (_svc) _svc.focus();
        if (event) event.preventDefault();
        return false;
    }
    if (!doctorId) {
        showToast('Vui l\u00f2ng ch\u1ecdn B\u00e1c s\u0129 ph\u1ee5 tr\u00e1ch!');
        if (_doc) _doc.focus();
        if (event) event.preventDefault();
        return false;
    }
    if (!apptDate) {
        showToast('Vui l\u00f2ng ch\u1ecdn Ng\u00e0y kh\u00e1m mong mu\u1ed1n!');
        if (event) event.preventDefault();
        return false;
    }
    if (!scheduleId) {
        showToast('Vui l\u00f2ng b\u1ea5m ch\u1ecdn m\u1ed9t Ca kh\u00e1m 60 ph\u00fat kh\u1ea3 d\u1ee5ng (m\u00e0u xanh)!');
        if (event) event.preventDefault();
        return false;
    }

    var chosen = new Date(apptDate);
    if (chosen.getDay() === 0) {
        showToast('Ph\u00f2ng kh\u00e1m kh\u00f4ng l\u00e0m vi\u1ec7c v\u00e0o Ch\u1ee7 Nh\u1eadt. Vui l\u00f2ng ch\u1ecdn ng\u00e0y kh\u00e1c!');
        if (event) event.preventDefault();
        return false;
    }

    var submitBtn = document.getElementById('submitBookingBtn');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-2"></i>\u0110ang kh\u1edfi t\u1ea1o \u0111\u01a1n h\u1eb9n y t\u1ebf\u2026';
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
