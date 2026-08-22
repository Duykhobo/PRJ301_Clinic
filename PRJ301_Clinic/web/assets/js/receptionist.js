/**
 * ============================================================================
 * RECEPTIONIST JAVASCRIPT CONTROLLER (100% UNICODE-SAFE ENCODING)
 * ============================================================================
 */

'use strict';

// 1. HUY LICH HEN - SweetAlert2 Confirm
function confirmCancelAppointment(id, patientName) {
    Swal.fire({
        title: 'H\u1ee7y Cu\u1ed9c H\u1eb9n #' + id + '?',
        html: 'B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n h\u1ee7y cu\u1ed9c h\u1eb9n c\u1ee7a b\u1ec7nh nh\u00e2n <strong style="color: #38bdf8;">' + (patientName || '') + '</strong>?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ef4444',
        cancelButtonColor: '#475569',
        confirmButtonText: '<i class="fa-solid fa-ban me-1"></i> \u0110\u1ed3ng \u00dd H\u1ee7y',
        cancelButtonText: '<i class="fa-solid fa-xmark me-1"></i> Gi\u1eef L\u1ea1i',
        background: '#0b1628',
        color: '#ffffff',
        customClass: {
            popup: 'glass-card border border-secondary border-opacity-25 rounded-4 shadow-lg'
        }
    }).then(function (result) {
        if (result.isConfirmed) {
            const form = document.getElementById('cancelForm_' + id);
            if (form) form.submit();
        }
    });
}

function confirmCollectCash(form, patientName, amount) {
    const formattedAmount = amount ? (' <b>' + amount + ' VN\u0110</b>') : '';
    Swal.fire({
        title: 'X\u00e1c Nh\u1eadn Thu Ti\u1ec1n M\u1eb7t',
        html: 'X\u00e1c nh\u1eadn \u0111\u00e3 nh\u1eadn \u0111\u1ee7 ti\u1ec1n m\u1eb7t' + formattedAmount + ' t\u1eeb b\u1ec7nh nh\u00e2n <b>' + (patientName || '') + '</b>?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#10b981',
        cancelButtonColor: '#475569',
        confirmButtonText: '<i class="fa-solid fa-hand-holding-dollar me-1"></i> \u0110\u00e3 Nh\u1eadn \u0110\u1ee7',
        cancelButtonText: '<i class="fa-solid fa-xmark me-1"></i> H\u1ee7y B\u1ecf',
        background: '#0b1628',
        color: '#ffffff',
        customClass: {
            popup: 'glass-card border border-secondary border-opacity-25 rounded-4 shadow-lg'
        }
    }).then(function (result) {
        if (result.isConfirmed && form) {
            form.submit();
        }
    });
}

function confirmCheckin(form, patientName) {
    Swal.fire({
        title: 'X\u00e1c Nh\u1eadn Ti\u1ebfp \u0110\u00f3n',
        html: 'X\u00e1c nh\u1eadn ti\u1ebfp \u0111\u00f3n v\u00e0 chuy\u1ec3n b\u1ec7nh nh\u00e2n <b>' + (patientName || '') + '</b> v\u00e0o s\u1ea3nh ch\u1edd kh\u00e1m?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#0ea5e9',
        cancelButtonColor: '#475569',
        confirmButtonText: '<i class="fa-solid fa-user-check me-1"></i> Ti\u1ebfp \u0110\u00f3n',
        cancelButtonText: '<i class="fa-solid fa-xmark me-1"></i> H\u1ee7y B\u1ecf',
        background: '#0b1628',
        color: '#ffffff',
        customClass: {
            popup: 'glass-card border border-secondary border-opacity-25 rounded-4 shadow-lg'
        }
    }).then(function (result) {
        if (result.isConfirmed && form) {
            form.submit();
        }
    });
}

function confirmRefund(form, patientName) {
    Swal.fire({
        title: 'X\u00e1c Nh\u1eadn Ho\u00e0n Ti\u1ec1n',
        html: 'X\u00e1c nh\u1eadn \u0111\u00e3 ho\u00e0n tr\u1ea3 ti\u1ec1n l\u1ea1i cho b\u1ec7nh nh\u00e2n <b>' + (patientName || '') + '</b>?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ef4444',
        cancelButtonColor: '#475569',
        confirmButtonText: '<i class="fa-solid fa-money-bill-transfer me-1"></i> X\u00e1c Nh\u1eadn Ho\u00e0n Ti\u1ec1n',
        cancelButtonText: '<i class="fa-solid fa-xmark me-1"></i> H\u1ee7y B\u1ecf',
        background: '#0b1628',
        color: '#ffffff',
        customClass: {
            popup: 'glass-card border border-secondary border-opacity-25 rounded-4 shadow-lg'
        }
    }).then(function (result) {
        if (result.isConfirmed && form) {
            form.submit();
        }
    });
}

// 2. WALK-IN: LOAD CA KHAM THEO BAC SI + NGAY (AJAX)
function loadWalkInSlots() {
    var doctorIdEl = document.getElementById('wi_doctorId');
    var dateEl     = document.getElementById('wi_date');
    var sel        = document.getElementById('wi_scheduleId');
    var alertBox   = document.getElementById('walkInAlert');

    var doctorId = doctorIdEl ? doctorIdEl.value : '';
    var date     = dateEl     ? dateEl.value     : '';

    if (!sel || !alertBox) return;

    sel.innerHTML = '<option value="">-- \u0110ang t\u1ea3i... --</option>';
    alertBox.style.display = 'none';

    if (!doctorId || !date) {
        sel.innerHTML = '<option value="">-- Ch\u1ecdn B\u00e1c S\u0129 v\u00e0 Ng\u00e0y tr\u01b0\u1edbc --</option>';
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
            sel.innerHTML = '<option value="">-- B\u00e1c s\u0129 ch\u01b0a c\u00f3 ca trong ng\u00e0y n\u00e0y --</option>';
            _showAlert(alertBox, 'warning', 'B\u00e1c s\u0129 ch\u01b0a c\u00f3 ca l\u00e0m vi\u1ec7c trong ng\u00e0y \u0111\u00e3 ch\u1ecdn. Vui l\u00f2ng sinh ca ho\u1eb7c ch\u1ecdn ng\u00e0y kh\u00e1c.');
            return;
        }

        sel.innerHTML = '<option value="">-- Ch\u1ecdn Ca Kh\u00e1m --</option>';
        var hasAvailable = false;

        allSlots.forEach(function (s) {
            var isOk = (s.isAvailable === true || s.isAvailable === 1 || s.isAvailable === 'true');
            var opt  = document.createElement('option');
            opt.value = s.id;

            var startStr = s.startTime ? s.startTime.substring(0, 5) : '';
            var endStr   = s.endTime   ? s.endTime.substring(0, 5)   : '';
            opt.textContent = startStr + ' -> ' + endStr + (isOk ? '' : ' (\u0110\u00e3 c\u00f3 ng\u01b0\u1eddi \u0111\u1eb7t)');

            if (!isOk) {
                opt.disabled = true;
            } else {
                hasAvailable = true;
            }
            sel.appendChild(opt);
        });

        if (!hasAvailable) {
            _showAlert(alertBox, 'warning', 'T\u1ea5t c\u1ea3 ca trong ng\u00e0y n\u00e0y \u0111\u00e3 \u0111\u01b0\u1ee3c \u0111\u1eb7t. Vui l\u00f2ng ch\u1ecdn ng\u00e0y kh\u00e1c.');
        }
    })
    .catch(function (err) {
        console.error('[loadWalkInSlots] L\u1ed7i:', err, '| URL:', url);
        sel.innerHTML = '<option value="">-- L\u1ed7i k\u1ebft n\u1ed1i server --</option>';
        _showAlert(alertBox, 'danger', 'Kh\u00f4ng th\u1ec3 t\u1ea3i danh s\u00e1ch ca. Ki\u1ec3m tra Console (F12) \u0111\u1ec3 xem l\u1ed7i chi ti\u1ebft.');
    });
}

function _showAlert(box, type, msg) {
    if (!box) return;
    box.style.display = 'block';
    box.innerHTML = '<div class="alert alert-' + type + ' py-2 mb-0" style="font-size:.85rem;">' + msg + '</div>';
}

// 3. REAL-TIME SEARCH & FILTER FOR RECEPTION TABLE
var selectedReceptionFilter = 'ALL';

function setReceptionFilter(filterType, btn) {
    selectedReceptionFilter = filterType;
    var group = document.getElementById('receptionStatusFilterGroup');
    if (group) {
        Array.from(group.children).forEach(function(b) {
            b.classList.remove('active');
        });
        btn.classList.add('active');
    }
    filterReceptionTable();
}

function filterReceptionTable() {
    var searchInput = document.getElementById('searchReception');
    var query = searchInput ? searchInput.value.toLowerCase().trim() : '';
    var table = document.getElementById('receptionTable');
    if (!table) return;

    var tbody = table.getElementsByTagName('tbody')[0];
    if (!tbody) return;
    var rows = tbody.getElementsByTagName('tr');
    var visibleCount = 0;
    var totalCount = 0;

    for (var i = 0; i < rows.length; i++) {
        var r = rows[i];
        if (r.classList.contains('no-result-row')) continue;
        totalCount++;

        var rowText = r.innerText.toLowerCase();
        var status = r.getAttribute('data-status') || '';
        var payment = r.getAttribute('data-payment') || '';

        var matchesQuery = !query || rowText.includes(query);
        var matchesStatus = true;

        if (selectedReceptionFilter === 'UNPAID') {
            matchesStatus = (payment === 'UNPAID' && status !== 'CANCELLED');
        } else if (selectedReceptionFilter === 'CONFIRMED') {
            matchesStatus = (status === 'CONFIRMED');
        } else if (selectedReceptionFilter === 'COMPLETED') {
            matchesStatus = (status === 'COMPLETED');
        } else if (selectedReceptionFilter === 'REFUND') {
            matchesStatus = (payment === 'REFUND_PENDING' || payment === 'REFUNDED');
        }

        if (matchesQuery && matchesStatus) {
            r.style.display = '';
            visibleCount++;
        } else {
            r.style.display = 'none';
        }
    }

    var countBadge = document.getElementById('receptionFilterCount');
    if (countBadge) {
        countBadge.textContent = 'Hi\u1ec3n th\u1ecb: ' + visibleCount + ' / ' + totalCount;
    }
}

document.addEventListener('DOMContentLoaded', function() {
    filterReceptionTable();
});
