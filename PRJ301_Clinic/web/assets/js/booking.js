/**
 * booking.js - Logic dat lich kham benh nhan
 * Su dung Unicode escape cho tieng Viet de tranh loi encoding.
 * Context path inject: window.BOOKING_CTX
 * Phu thuoc: Flatpickr, SweetAlert2
 */

'use strict';

var fpInstance = null;

// =====================================================================
// 1. KHOI TAO FLATPICKR (Vo hieu hoa Chu Nhat)
// =====================================================================
document.addEventListener('DOMContentLoaded', function () {
    fpInstance = flatpickr('#appointmentDate', {
        locale: 'vn',
        dateFormat: 'Y-m-d',
        defaultDate: getNextWorkday(new Date()),
        altInput: true,
        altFormat: 'd/m/Y',
        altInputClass: 'form-control form-control-glass border-start-0 ps-0',
        minDate: 'today',
        disableMobile: true,
        disable: [
            function (date) {
                return date.getDay() === 0; // 0 = Sunday
            }
        ],
        onChange: function () {
            updateStepProgress();
            fetchSlots();
        },
        onReady: function () {
            updateStepProgress();
            fetchSlots();
        }
    });
});

// =====================================================================
// 2. LAY NGAY LAM VIEC KE TIEP (bo qua Chu Nhat)
// =====================================================================
function getNextWorkday(date) {
    var d = new Date(date);
    if (d.getDay() === 0) d.setDate(d.getDate() + 1);
    return d;
}

// =====================================================================
// 3. NUT CHON NHANH - tu skip Chu Nhat
// =====================================================================
function setQuickDate(daysToAdd) {
    var d = new Date();
    d.setDate(d.getDate() + daysToAdd);
    if (d.getDay() === 0) d.setDate(d.getDate() + 1);

    var dateStr = d.getFullYear() + '-'
        + String(d.getMonth() + 1).padStart(2, '0') + '-'
        + String(d.getDate()).padStart(2, '0');

    if (fpInstance) {
        fpInstance.setDate(dateStr, true);
    } else {
        document.getElementById('appointmentDate').value = dateStr;
        updateStepProgress();
        fetchSlots();
    }
}

// =====================================================================
// 4. CAP NHAT THANH TIEN TRINH 4 BUOC
// =====================================================================
function updateStepProgress() {
    var _s  = document.getElementById('serviceSelect');
    var _d  = document.getElementById('doctorSelect');
    var _dt = document.getElementById('appointmentDate');
    var _sl = document.getElementById('selectedScheduleId');

    var sVal    = _s  ? _s.value  : '';
    var dVal    = _d  ? _d.value  : '';
    var dateVal = _dt ? _dt.value : '';
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
// 5. CHON CA KHAM (slot pill)
// =====================================================================
function selectSlot(scheduleId, btnElement) {
    document.getElementById('selectedScheduleId').value = scheduleId;
    document.querySelectorAll('.slot-pill').forEach(function (btn) {
        if (!btn.disabled) btn.className = 'btn w-100 py-2 slot-pill slot-btn-available';
    });
    btnElement.className = 'btn w-100 py-2 slot-pill slot-btn-selected';
    updateStepProgress();
}

// =====================================================================
// 6. FETCH DANH SACH CA TRONG (AJAX)
//    Dung string concatenation - tranh JSP EL nuot bien JS trong template literal
// =====================================================================
function fetchSlots() {
    var _dr = document.getElementById('doctorSelect');
    var _da = document.getElementById('appointmentDate');
    var doctorId = _dr ? _dr.value : '';
    var date     = _da ? _da.value : '';
    var grid     = document.getElementById('slotMatrixGrid');
    var ctx      = window.BOOKING_CTX || '';

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
        slots.forEach(function (slot) {
            var isAvailable  = (slot.isAvailable === true || slot.isAvailable === 1);
            var btnClass     = isAvailable ? 'slot-btn-available' : 'slot-btn-booked';
            var icon         = isAvailable ? 'fa-regular fa-clock text-emerald' : 'fa-solid fa-lock';
            var disabledAttr = isAvailable ? '' : 'disabled';
            var statusText   = isAvailable ? '' : ' (\u0110\u00e3 \u0111\u01b0\u1ee3c \u0111\u1eb7t)';
            var startStr     = slot.startTime ? slot.startTime.substring(0, 5) : '';
            var endStr       = slot.endTime   ? slot.endTime.substring(0, 5)   : '';

            html += '<div class="col-6 col-md-4">'
                  + '<button type="button" class="btn w-100 py-2 slot-pill ' + btnClass + '" '
                  + 'onclick="selectSlot(' + slot.id + ', this)" ' + disabledAttr + '>'
                  + '<i class="' + icon + ' me-1"></i>' + startStr + ' - ' + endStr + statusText
                  + '</button></div>';
        });
        grid.innerHTML = html;
    })
    .catch(function () {
        grid.innerHTML = '<div class="col-12 text-center text-danger py-3">'
            + '<i class="fa-solid fa-triangle-exclamation me-1"></i>'
            + 'L\u1ed7i khi n\u1ea1p l\u1ecbch kh\u00e1m t\u1eeb CSDL.'
            + '</div>';
    });
}

// =====================================================================
// 7. VALIDATE FORM TRUOC KHI SUBMIT
// =====================================================================
function validateBookingForm(event) {
    var _svc = document.getElementById('serviceSelect');
    var _doc = document.getElementById('doctorSelect');
    var _dat = document.getElementById('appointmentDate');
    var _sch = document.getElementById('selectedScheduleId');

    var serviceId  = _svc ? _svc.value : '';
    var doctorId   = _doc ? _doc.value : '';
    var apptDate   = _dat ? _dat.value : '';
    var scheduleId = _sch ? _sch.value : '';

    if (!serviceId) {
        showToast('Vui l\u00f2ng ch\u1ecdn D\u1ecbch v\u1ee5 kh\u00e1m / Spa!');
        if (_svc) _svc.focus();
        event.preventDefault(); return false;
    }
    if (!doctorId) {
        showToast('Vui l\u00f2ng ch\u1ecdn B\u00e1c s\u0129 ph\u1ee5 tr\u00e1ch!');
        if (_doc) _doc.focus();
        event.preventDefault(); return false;
    }
    if (!apptDate) {
        showToast('Vui l\u00f2ng ch\u1ecdn Ng\u00e0y kh\u00e1m mong mu\u1ed1n!');
        if (_dat) _dat.focus();
        event.preventDefault(); return false;
    }
    if (!scheduleId) {
        showToast('Vui l\u00f2ng b\u1ea5m ch\u1ecdn m\u1ed9t Ca kh\u00e1m 60 ph\u00fat c\u00f2n tr\u1ed1ng (n\u00fat m\u00e0u xanh)!');
        event.preventDefault(); return false;
    }

    var chosen = new Date(apptDate);
    if (chosen.getDay() === 0) {
        showToast('Ph\u00f2ng kh\u00e1m kh\u00f4ng l\u00e0m vi\u1ec7c v\u00e0o Ch\u1ee7 Nh\u1eadt. Vui l\u00f2ng ch\u1ecdn ng\u00e0y kh\u00e1c!');
        event.preventDefault(); return false;
    }

    var submitBtn = document.getElementById('submitBookingBtn');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-2"></i>\u0110ang kh\u1edfi t\u1ea1o \u0111\u01a1n h\u1eb9n y t\u1ebf\u2026';
    }
    return true;
}

// =====================================================================
// 8. TOAST HELPER
// =====================================================================
if (typeof showToast === 'undefined') {
    window.showToast = function (msg) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                toast: true, position: 'top-end', showConfirmButton: false,
                timer: 3500, timerProgressBar: true, icon: 'warning',
                title: msg, background: '#1e293b', color: '#fff'
            });
        } else {
            alert(msg);
        }
    };
}
