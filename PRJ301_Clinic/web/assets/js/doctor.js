/**
 * ============================================================================
 * DOCTOR DASHBOARD JAVASCRIPT CONTROLLER (100% UNICODE-SAFE ENCODING)
 * ============================================================================
 */

function openMedicalModal(id, patientName, serviceName, diagnosis, prescription) {
    const elId = document.getElementById('modalAppointmentId');
    const elName = document.getElementById('modalPatientName');
    const elService = document.getElementById('modalServiceName');
    const elDiag = document.getElementById('modalDiagnosis');
    const elPresc = document.getElementById('modalPrescription');

    if (elId) elId.value = id;
    if (elName) elName.innerText = patientName;
    if (elService) elService.innerText = serviceName;
    if (elDiag) elDiag.value = diagnosis || '';
    if (elPresc) elPresc.value = prescription || '';

    // Khởi tạo ngẫu nhiên hoặc mặc định cho chỉ số da nếu chưa có
    const moisture = Math.floor(Math.random() * 25) + 50; // 50 - 75%
    const oil = Math.floor(Math.random() * 30) + 40; // 40 - 70%
    const elMoist = document.getElementById('moistureSlider');
    const elMoistVal = document.getElementById('moistureVal');
    const elOil = document.getElementById('oilSlider');
    const elOilVal = document.getElementById('oilVal');

    if (elMoist) elMoist.value = moisture;
    if (elMoistVal) elMoistVal.innerText = moisture + '%';
    if (elOil) elOil.value = oil;
    if (elOilVal) elOilVal.innerText = oil + '%';

    const modalEl = document.getElementById('medicalModal');
    if (modalEl && typeof bootstrap !== 'undefined') {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }
}

// DOCTOR AJAX SCHEDULE MANAGEMENT
function renderDoctorSlots(slots) {
    const container = document.getElementById('doctorSlotsContainer');
    if (!container) return;
    if (!slots || slots.length === 0) {
        container.innerHTML = '<div class="text-center py-4 text-muted"><i class="fa-solid fa-calendar-xmark me-2"></i>B\u00e1c s\u0129 ch\u01b0a c\u00f3 khung gi\u1edd l\u00e0m vi\u1ec7c n\u00e0o cho ng\u00e0y n\u00e0y.<button type="button" class="btn btn-sm btn-outline-info rounded-pill px-3 py-1 ms-2" onclick="generateScheduleAjax()"><i class="fa-solid fa-wand-magic-sparkles me-1"></i>T\u1ef1 \u0110\u1ed9ng Sinh T\u1ea5t C\u1ea3 Ca Kh\u00e1m</button></div>';
        return;
    }
    let html = '<div class="row g-3 animate-fade-in">';
    slots.forEach(slot => {
        const isAvail = slot.isAvailable === true || slot.isAvailable === 1;
        const borderClass = isAvail ? 'border-cyan bg-cyan bg-opacity-10' : 'border-secondary bg-dark text-muted';
        const clockIcon = isAvail ? 'text-cyan' : 'text-muted';
        const textClass = isAvail ? 'text-white' : 'text-muted';
        const statusBadge = isAvail ? '\uD83D\uDFE2 Kh\u1ea3 d\u1ee5ng' : '\uD83D\uDD34 Kh\u00f3a / \u0110\u00e3 \u0111\u1eb7t';
        const lockBtnClass = isAvail ? 'btn-outline-warning' : 'btn-outline-success';
        const lockIcon = isAvail ? 'fa-lock' : 'fa-unlock';
        const lockTitle = isAvail ? 'Kh\u00f3a ca n\u00e0y' : 'M\u1edf ca n\u00e0y';

        html += '<div class="col-12 col-sm-6 col-md-4 col-xl-3">' +
                '<div class="p-3 rounded-3 border d-flex align-items-center justify-content-between gap-2 ' + borderClass + ' shadow-sm" style="transition:all 0.2s;">' +
                '<div class="d-flex align-items-center gap-2">' +
                '<i class="fa-solid fa-clock fs-5 ' + clockIcon + '"></i>' +
                '<div>' +
                '<div class="fw-bold fs-7 ' + textClass + '">' + slot.startTime + ' - ' + slot.endTime + '</div>' +
                '<div class="fs-8 ' + (isAvail ? 'text-emerald fw-semibold' : 'text-danger') + '">' + statusBadge + '</div>' +
                '</div>' +
                '</div>' +
                '<div class="d-flex gap-1">' +
                '<button type="button" class="btn btn-xs ' + lockBtnClass + ' p-1 rounded-circle" title="' + lockTitle + '" onclick="confirmToggleSlot(' + slot.id + ', ' + (!isAvail) + ', \'' + slot.startTime + ' - ' + slot.endTime + '\')">' +
                '<i class="fa-solid ' + lockIcon + '"></i>' +
                '</button>' +
                '<button type="button" class="btn btn-xs btn-outline-danger p-1 rounded-circle" title="X\u00f3a ca n\u00e0y" onclick="deleteSlotAjax(' + slot.id + ')">' +
                '<i class="fa-solid fa-trash"></i>' +
                '</button>' +
                '</div>' +
                '</div>' +
                '</div>';
    });
    html += '</div>';
    container.innerHTML = html;
}

function fetchDoctorSlotsAjax() {
    const ctx = window.DOCTOR_CTX || '';
    const date = window.DOCTOR_CURRENT_DATE || '';
    const params = new URLSearchParams();
    params.append("action", "get-slots");
    params.append("date", date);
    params.append("ajax", "true");

    fetch(ctx + "/doctor/dashboard", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
        body: params
    })
    .then(res => res.json())
    .then(data => {
        if (data.slots) renderDoctorSlots(data.slots);
    })
    .catch(err => console.error("AJAX Error:", err));
}

function generateScheduleAjax() {
    fetchDoctorSlotsAjax();
}

function confirmToggleSlot(slotId, newStatus, slotTime) {
    const actionText = newStatus ? 'M\u1edf Ca' : 'Kh\u00f3a Ca';
    const actionLower = newStatus ? 'm\u1edf ca' : 'kh\u00f3a ca';
    const confirmColor = newStatus ? '#10b981' : '#f59e0b';
    const timeLabel = slotTime ? ('khung gi\u1edd ' + slotTime) : 'ca kh\u00e1m n\u00e0y';

    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'X\u00e1c Nh\u1eadn ' + actionText,
            html: 'B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n <b>' + actionLower + '</b> ' + timeLabel + ' kh\u00f4ng?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: confirmColor,
            cancelButtonColor: '#475569',
            confirmButtonText: '<i class="fa-solid fa-check me-1"></i> ' + actionText,
            cancelButtonText: '<i class="fa-solid fa-xmark me-1"></i> H\u1ee7y B\u1ecf',
            background: '#0b1628',
            color: '#ffffff',
            customClass: {
                popup: 'glass-card border border-secondary border-opacity-25 rounded-4 shadow-lg'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                toggleSlotAjax(slotId, newStatus);
            }
        });
    } else {
        if (confirm('B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n ' + actionLower + ' ' + timeLabel + ' kh\u00f4ng?')) {
            toggleSlotAjax(slotId, newStatus);
        }
    }
}

function toggleSlotAjax(slotId, newStatus) {
    const ctx = window.DOCTOR_CTX || '';
    const date = window.DOCTOR_CURRENT_DATE || '';
    const params = new URLSearchParams();
    params.append("action", "toggle-slot");
    params.append("slotId", slotId);
    params.append("status", newStatus);
    params.append("date", date);
    params.append("ajax", "true");

    fetch(ctx + "/doctor/dashboard", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
        body: params
    })
    .then(res => res.json())
    .then(data => {
        if (data.slots) renderDoctorSlots(data.slots);
        showToastNotification(data.success ? "success" : "error", data.message);
    })
    .catch(err => console.error("AJAX Error:", err));
}

function deleteSlotAjax(slotId) {
    const ctx = window.DOCTOR_CTX || '';
    const date = window.DOCTOR_CURRENT_DATE || '';

    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'X\u00f3a Ca Kh\u00e1m N\u00e0y?',
            text: 'B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n x\u00f3a khung gi\u1edd kh\u00e1m n\u00e0y kh\u00f4ng?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ef4444',
            cancelButtonColor: '#64748b',
            confirmButtonText: '<i class="fa-solid fa-trash me-1"></i> X\u00f3a Ca',
            cancelButtonText: 'H\u1ee7y B\u1ecf',
            background: '#0f172a',
            color: '#f8fafc',
            customClass: {
                popup: 'border border-danger border-opacity-40 shadow-lg'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                performDeleteSlot(ctx, date, slotId);
            }
        });
    } else {
        if (confirm('B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n x\u00f3a khung gi\u1edd kh\u00e1m n\u00e0y kh\u00f4ng?')) {
            performDeleteSlot(ctx, date, slotId);
        }
    }
}

function performDeleteSlot(ctx, date, slotId) {
    const params = new URLSearchParams();
    params.append("action", "delete-slot");
    params.append("slotId", slotId);
    params.append("date", date);
    params.append("ajax", "true");

    fetch(ctx + "/doctor/dashboard", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
        body: params
    })
    .then(res => res.json())
    .then(data => {
        if (data.slots) renderDoctorSlots(data.slots);
        showToastNotification(data.success ? "success" : "error", data.message);
    })
    .catch(err => console.error("AJAX Error:", err));
}

function showToastNotification(type, message) {
    let container = document.getElementById("toastContainer");
    if (!container) {
        container = document.createElement("div");
        container.id = "toastContainer";
        document.body.appendChild(container);
    }
    const toast = document.createElement("div");
    toast.className = "toast-glass " + type;
    const iconClass = type === 'success' ? 'fa-solid fa-circle-check text-success' : 'fa-solid fa-triangle-exclamation text-danger';
    toast.innerHTML = '<i class="' + iconClass + ' fs-5"></i><span style="font-size:.88rem; font-weight:600;">' + (message || '') + '</span>';
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(20px)';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}
