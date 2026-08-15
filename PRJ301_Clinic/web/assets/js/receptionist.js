/**
 * receptionist.js - Logic Sanh Le Tan
 * Context path inject boi JSP: window.RECEPTIONIST_CTX
 * web.xml da khai bao mime-type: text/javascript;charset=UTF-8
 * Phu thuoc: Bootstrap 5, SweetAlert2
 */

'use strict';

// =====================================================================
// 1. HUY LICH HEN - SweetAlert2 Confirm
// =====================================================================
function confirmCancelAppointment(id, patientName) {
    Swal.fire({
        title: 'Huy Cuoc Hen #' + id + '?',
        html: 'Ban co chac chan muon huy cuoc hen cua benh nhan <strong style="color: #38bdf8;">' + patientName + '</strong>?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ef4444',
        cancelButtonColor: '#475569',
        confirmButtonText: '<i class="fa-solid fa-ban me-1"></i> Dong Y Huy',
        cancelButtonText: 'Giu Lai',
        background: '#0b132b',
        color: '#ffffff',
        customClass: {
            popup: 'rounded-4 border border-cyan border-opacity-30 shadow-lg'
        }
    }).then(function (result) {
        if (result.isConfirmed) {
            document.getElementById('cancelForm_' + id).submit();
        }
    });
}

// =====================================================================
// 2. WALK-IN: LOAD CA KHAM THEO BAC SI + NGAY (AJAX)
//    Dung string concatenation - tranh JSP EL nuot bien JS trong template literal
// =====================================================================
function loadWalkInSlots() {
    var doctorIdEl = document.getElementById('wi_doctorId');
    var dateEl     = document.getElementById('wi_date');
    var sel        = document.getElementById('wi_scheduleId');
    var alertBox   = document.getElementById('walkInAlert');

    var doctorId = doctorIdEl ? doctorIdEl.value : '';
    var date     = dateEl     ? dateEl.value     : '';

    sel.innerHTML = '<option value="">-- Dang tai... --</option>';
    alertBox.style.display = 'none';

    if (!doctorId || !date) {
        sel.innerHTML = '<option value="">-- Chon Bac Si va Ngay truoc --</option>';
        return;
    }

    var ctx = window.RECEPTIONIST_CTX || '';
    var url = ctx + '/receptionist/dashboard?action=get-slots'
            + '&doctorId=' + encodeURIComponent(doctorId)
            + '&date='     + encodeURIComponent(date);

    fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
    .then(function (r) {
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.json();
    })
    .then(function (data) {
        var allSlots = data.slots || [];

        if (allSlots.length === 0) {
            sel.innerHTML = '<option value="">-- Bac si chua co ca trong ngay nay --</option>';
            _showAlert(alertBox, 'warning', 'Bac si chua co ca lam viec trong ngay da chon. Vui long sinh ca hoac chon ngay khac.');
            return;
        }

        sel.innerHTML = '<option value="">-- Chon Ca Kham --</option>';
        var hasAvailable = false;

        allSlots.forEach(function (s) {
            var isOk = (s.isAvailable === true || s.isAvailable === 1 || s.isAvailable === 'true');
            var opt  = document.createElement('option');
            opt.value = s.id;

            var startStr = s.startTime ? s.startTime.substring(0, 5) : '';
            var endStr   = s.endTime   ? s.endTime.substring(0, 5)   : '';
            opt.textContent = startStr + ' -> ' + endStr + (isOk ? '' : ' (Da co nguoi dat)');

            if (!isOk) {
                opt.disabled = true;
            } else {
                hasAvailable = true;
            }
            sel.appendChild(opt);
        });

        if (!hasAvailable) {
            _showAlert(alertBox, 'warning', 'Tat ca ca trong ngay nay da duoc dat. Vui long chon ngay khac.');
        }
    })
    .catch(function (err) {
        console.error('[loadWalkInSlots] Loi:', err, '| URL:', url);
        sel.innerHTML = '<option value="">-- Loi ket noi server --</option>';
        _showAlert(alertBox, 'danger', 'Khong the tai danh sach ca. Kiem tra Console (F12) de xem loi chi tiet.');
    });
}

function _showAlert(box, type, msg) {
    box.style.display = 'block';
    box.innerHTML = '<div class="alert alert-' + type + ' py-2 mb-0" style="font-size:.85rem;">' + msg + '</div>';
}
