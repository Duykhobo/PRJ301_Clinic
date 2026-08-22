/**
 * ============================================================================
 * ADMIN DASHBOARD JAVASCRIPT CONTROLLER (100% UNICODE-SAFE ENCODING)
 * ============================================================================
 */

// 1. TOAST NOTIFICATION
function showToast(type, message) {
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

// 2. LIVE SEARCH / CLIENT FILTERING DEBOUNCE
let userSearchTimeout = null;
function liveSearchUsers() {
    clearTimeout(userSearchTimeout);
    userSearchTimeout = setTimeout(() => {
        const input = document.getElementById("adminUserSearch");
        const query = input ? input.value.toLowerCase().trim() : "";
        const rows = document.querySelectorAll("#adminUserTable tbody tr");
        let visibleCount = 0;
        let totalCount = 0;

        rows.forEach(r => {
            if (r.classList.contains("no-data-row")) return;
            totalCount++;
            const text = r.innerText.toLowerCase();
            if (!query || text.includes(query)) {
                r.style.display = "";
                visibleCount++;
            } else {
                r.style.display = "none";
            }
        });

        const countBadge = document.getElementById("userFilterCountBadge");
        if (countBadge) {
            countBadge.textContent = "Kh\u1edbp: " + visibleCount + " / " + totalCount;
        }
    }, 200);
}

let serviceSearchTimeout = null;
function liveSearchServices() {
    clearTimeout(serviceSearchTimeout);
    serviceSearchTimeout = setTimeout(() => {
        const input = document.getElementById("adminServiceSearch");
        const query = input ? input.value.toLowerCase().trim() : "";
        const rows = document.querySelectorAll("#adminServiceTable tbody tr");
        let visibleCount = 0;
        let totalCount = 0;

        rows.forEach(r => {
            if (r.classList.contains("no-data-row")) return;
            totalCount++;
            const text = r.innerText.toLowerCase();
            if (!query || text.includes(query)) {
                r.style.display = "";
                visibleCount++;
            } else {
                r.style.display = "none";
            }
        });

        const countBadge = document.getElementById("serviceFilterCountBadge");
        if (countBadge) {
            countBadge.textContent = "Kh\u1edbp: " + visibleCount + " / " + totalCount;
        }
    }, 200);
}

// 2.1 SERVER-SIDE FILTER NAVIGATION (LỌC TỨC THÌ TỪ TRANG 1)
function applyUserFilter(newRole, newStatus) {
    const ctx = window.ADMIN_CTX || '';
    const currentRole = (newRole !== null) ? newRole : (window.ADMIN_CURRENT_ROLE_USER || '');
    const currentStatus = (newStatus !== null) ? newStatus : (window.ADMIN_CURRENT_STATUS_USER || '');
    const searchVal = document.getElementById('adminUserSearch') ? document.getElementById('adminUserSearch').value.trim() : '';

    let url = ctx + "/admin/dashboard?tab=users&pageUser=1";
    if (currentRole) url += "&roleUser=" + encodeURIComponent(currentRole);
    if (currentStatus) url += "&statusUser=" + encodeURIComponent(currentStatus);
    if (searchVal) url += "&searchUser=" + encodeURIComponent(searchVal);

    window.location.href = url;
}

function applyServiceFilter(newStatus) {
    const ctx = window.ADMIN_CTX || '';
    const currentStatus = (newStatus !== null) ? newStatus : (window.ADMIN_CURRENT_STATUS_SERVICE || '');
    const searchVal = document.getElementById('adminServiceSearch') ? document.getElementById('adminServiceSearch').value.trim() : '';

    let url = ctx + "/admin/dashboard?tab=services&pageService=1";
    if (currentStatus) url += "&statusService=" + encodeURIComponent(currentStatus);
    if (searchVal) url += "&searchService=" + encodeURIComponent(searchVal);

    window.location.href = url;
}

// 3. CONFIRM & AJAX TOGGLE USER STATUS
function confirmToggleUserStatus(userId, currentStatus, username) {
    const actionText = currentStatus ? 'Kh\u00f3a' : 'M\u1edf Kh\u00f3a';
    const actionLower = currentStatus ? 'kh\u00f3a' : 'm\u1edf kh\u00f3a';
    const confirmColor = currentStatus ? '#ef4444' : '#10b981';
    const confirmIcon = currentStatus ? 'warning' : 'question';
    const userLabel = username ? username : ('ID #' + userId);

    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'X\u00e1c Nh\u1eadn ' + actionText + ' T\u00e0i Kho\u1ea3n',
            html: 'B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n <b>' + actionLower + '</b> t\u00e0i kho\u1ea3n <b>' + userLabel + '</b> kh\u00f4ng?',
            icon: confirmIcon,
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
                toggleUserStatusAjax(userId, userLabel);
            }
        });
    } else {
        if (confirm('B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n ' + actionLower + ' t\u00e0i kho\u1ea3n ' + userLabel + ' kh\u00f4ng?')) {
            toggleUserStatusAjax(userId, userLabel);
        }
    }
}

function toggleUserStatusAjax(userId, userLabel) {
    const ctx = window.ADMIN_CTX || '';
    const formData = new URLSearchParams();
    formData.append("action", "toggle-user-status");
    formData.append("userId", userId);
    formData.append("ajax", "true");

    fetch(ctx + "/admin/dashboard", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: formData
    })
    .then(res => res.text())
    .then(text => {
        const data = JSON.parse(text);
        if (data.success) {
            showToast("success", data.message);
            const statusTd = document.getElementById("user-status-td-" + userId);
            const actionTd = document.getElementById("user-action-td-" + userId);
            const safeLabel = (userLabel || '').replace(/'/g, "\\'");

            if (data.newStatus) {
                if (statusTd) statusTd.innerHTML = '<span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-check-circle me-1"></i>Active</span>';
                if (actionTd) actionTd.innerHTML = '<button type="button" class="btn btn-sm btn-outline-danger rounded-pill px-3 fw-semibold" style="font-size:.78rem;" onclick="confirmToggleUserStatus(' + userId + ', true, \'' + safeLabel + '\')"><i class="fa-solid fa-lock me-1"></i><span>Kh\u00f3a</span></button>';
            } else {
                if (statusTd) statusTd.innerHTML = '<span class="badge bg-danger-subtle text-danger border border-danger-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-ban me-1"></i>Banned</span>';
                if (actionTd) actionTd.innerHTML = '<button type="button" class="btn btn-sm btn-outline-success rounded-pill px-3 fw-semibold" style="font-size:.78rem;" onclick="confirmToggleUserStatus(' + userId + ', false, \'' + safeLabel + '\')"><i class="fa-solid fa-unlock me-1"></i><span>M\u1edf Kh\u00f3a</span></button>';
            }
        } else {
            showToast("error", data.message || "Kh\u00f4ng th\u1ec3 c\u1eadp nh\u1eadt tr\u1ea1ng th\u00e1i ng\u01b0\u1eddi d\u00f9ng.");
        }
    })
    .catch(err => {
        console.error("AJAX Error:", err);
        showToast("error", "L\u1ed7i k\u1ebft n\u1ed1i m\u00e1y ch\u1ee7!");
    });
}

// 4. CONFIRM & AJAX UPDATE USER ROLE
function confirmChangeUserRole(selectElem, userId, oldRole, username) {
    const newRole = selectElem.value;
    const userLabel = username ? username : ('ID #' + userId);
    if (newRole === oldRole) return;

    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'X\u00e1c Nh\u1eadn \u0110\u1ed5i Vai Tr\u00f2',
            html: 'B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n ph\u00e2n quy\u1ec1n t\u00e0i kho\u1ea3n <b>' + userLabel + '</b> th\u00e0nh <b>' + newRole + '</b> kh\u00f4ng?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#0ea5e9',
            cancelButtonColor: '#475569',
            confirmButtonText: '<i class="fa-solid fa-user-shield me-1"></i> \u0110\u1ed5i Quy\u1ec1n',
            cancelButtonText: '<i class="fa-solid fa-xmark me-1"></i> H\u1ee7y B\u1ecf',
            background: '#0b1628',
            color: '#ffffff',
            customClass: {
                popup: 'glass-card border border-secondary border-opacity-25 rounded-4 shadow-lg'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                selectElem.dataset.currentRole = newRole;
                submitRoleAjax(selectElem, userId);
            } else {
                selectElem.value = oldRole;
            }
        });
    } else {
        if (confirm('B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n ph\u00e2n quy\u1ec1n t\u00e0i kho\u1ea3n ' + userLabel + ' th\u00e0nh ' + newRole + ' kh\u00f4ng?')) {
            submitRoleAjax(selectElem, userId);
        } else {
            selectElem.value = oldRole;
        }
    }
}

function submitRoleAjax(selectElem, userId) {
    const ctx = window.ADMIN_CTX || '';
    const newRole = selectElem.value;
    const formData = new URLSearchParams();
    formData.append("action", "update-user-role");
    formData.append("userId", userId);
    formData.append("role", newRole);
    formData.append("ajax", "true");

    fetch(ctx + "/admin/dashboard", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: formData
    })
    .then(res => res.text())
    .then(text => {
        const data = JSON.parse(text);
        if (data.success) {
            showToast("success", data.message);
        } else {
            showToast("error", data.message || "L\u1ed7i c\u1eadp nh\u1eadt vai tr\u00f2!");
            if (selectElem.dataset.currentRole) {
                selectElem.value = selectElem.dataset.currentRole;
            }
        }
    })
    .catch(err => {
        console.error("AJAX Error:", err);
        showToast("error", "L\u1ed7i k\u1ebft n\u1ed1i m\u00e1y ch\u1ee7!");
        if (selectElem.dataset.currentRole) {
            selectElem.value = selectElem.dataset.currentRole;
        }
    });
}

// 5. CONFIRM & AJAX TOGGLE SERVICE STATUS
function confirmToggleService(serviceId, currentStatus, serviceName) {
    const actionText = currentStatus ? '\u1ea8n' : 'Hi\u1ec3n Th\u1ecb';
    const actionLower = currentStatus ? '\u1ea9n' : 'hi\u1ec3n th\u1ecb';
    const confirmColor = currentStatus ? '#f59e0b' : '#10b981';
    const sName = serviceName ? serviceName : ('D\u1ecbch v\u1ee5 #' + serviceId);

    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'X\u00e1c Nh\u1eadn ' + actionText + ' D\u1ecbch V\u1ee5',
            html: 'B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n <b>' + actionLower + '</b> d\u1ecbch v\u1ee5 <b>' + sName + '</b> tr\u00ean c\u1ed5ng \u0111\u1eb7t l\u1ecbch c\u1ee7a kh\u00e1ch kh\u00f4ng?',
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
                toggleServiceStatusAjax(serviceId, sName);
            }
        });
    } else {
        if (confirm('B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n ' + actionLower + ' d\u1ecbch v\u1ee5 ' + sName + ' kh\u00f4ng?')) {
            toggleServiceStatusAjax(serviceId, sName);
        }
    }
}
const confirmToggleServiceStatus = confirmToggleService;

function toggleServiceStatusAjax(serviceId, sName) {
    const ctx = window.ADMIN_CTX || '';
    const formData = new URLSearchParams();
    formData.append("action", "toggle-service-status");
    formData.append("serviceId", serviceId);
    formData.append("ajax", "true");

    fetch(ctx + "/admin/dashboard", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: formData
    })
    .then(res => res.text())
    .then(text => {
        const data = JSON.parse(text);
        if (data.success) {
            showToast("success", data.message);
            const statusTd = document.getElementById("service-status-td-" + serviceId);
            const actionTd = document.getElementById("service-action-td-" + serviceId);
            const safeName = (sName || '').replace(/'/g, "\\'");

            if (data.newStatus) {
                if (statusTd) statusTd.innerHTML = '<span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-circle-check me-1"></i>\u0110ang Ph\u1ee5c V\u1ee5</span>';
                if (actionTd) actionTd.innerHTML = '<button type="button" class="btn btn-sm btn-outline-warning rounded-pill px-3 fw-semibold" style="font-size:.78rem;" onclick="confirmToggleService(' + serviceId + ', true, \'' + safeName + '\')"><i class="fa-solid fa-eye-slash me-1"></i><span>\u1ea8n</span></button>';
            } else {
                if (statusTd) statusTd.innerHTML = '<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle px-2 py-1 rounded-pill"><i class="fa-solid fa-eye-slash me-1"></i>T\u1ea1m \u1ea8n</span>';
                if (actionTd) actionTd.innerHTML = '<button type="button" class="btn btn-sm btn-outline-success rounded-pill px-3 fw-semibold" style="font-size:.78rem;" onclick="confirmToggleService(' + serviceId + ', false, \'' + safeName + '\')"><i class="fa-solid fa-eye me-1"></i><span>Hi\u1ec3n Th\u1ecb</span></button>';
            }
        } else {
            showToast("error", data.message || "Kh\u00f4ng th\u1ec3 c\u1eadp nh\u1eadt tr\u1ea1ng th\u00e1i d\u1ecbch v\u1ee5.");
        }
    })
    .catch(err => {
        console.error("AJAX Error:", err);
        showToast("error", "L\u1ed7i k\u1ebft n\u1ed1i m\u00e1y ch\u1ee7!");
    });
}

// 6. CONFIRM & AJAX SAVE CLINIC SETTINGS
function confirmSaveSettings() {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'L\u01b0u C\u1ea5u H\u00ecnh H\u1ec7 Th\u1ed1ng',
            text: 'B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n c\u1eadp nh\u1eadt to\u00e0n b\u1ed9 th\u00f4ng tin v\u00e0 khung gi\u1edd kh\u00e1m ph\u00f2ng kh\u00e1m?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#f59e0b',
            cancelButtonColor: '#475569',
            confirmButtonText: '<i class="fa-solid fa-floppy-disk me-1"></i> L\u01b0u C\u1ea5u H\u00ecnh',
            cancelButtonText: '<i class="fa-solid fa-xmark me-1"></i> H\u1ee7y B\u1ecf',
            background: '#0b1628',
            color: '#ffffff',
            customClass: {
                popup: 'glass-card border border-secondary border-opacity-25 rounded-4 shadow-lg'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                saveSettingsAjax();
            }
        });
    } else {
        if (confirm('B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n l\u01b0u c\u00e1c thay \u0111\u1ed5i c\u1ea5u h\u00ecnh kh\u00f4ng?')) {
            saveSettingsAjax();
        }
    }
}

function saveSettingsAjax() {
    const ctx = window.ADMIN_CTX || '';
    const form = document.getElementById("settingsForm");
    const formData = new URLSearchParams(new FormData(form));

    fetch(ctx + "/admin/dashboard", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: formData
    })
    .then(res => res.text())
    .then(text => {
        const data = JSON.parse(text);
        if (data.success) {
            showToast("success", data.message);
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        } else {
            showToast("error", data.message || "Kh\u00f4ng th\u1ec3 l\u01b0u c\u1ea5u h\u00ecnh.");
        }
    })
    .catch(err => {
        console.error("AJAX Error:", err);
        showToast("error", "L\u1ed7i k\u1ebft n\u1ed1i m\u00e1y ch\u1ee7!");
    });
}

// 7. INTERACTIVE TIME SLOT CHIPS FOR ADMIN
const availableDefaultHours = ["08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"];

function renderSlotChipsFromInput() {
    const input = document.getElementById('clinicTimeSlotsInput');
    const container = document.getElementById('adminSlotChips');
    if (!input || !container) return;

    const currentVal = input.value || '';
    const selectedList = currentVal.split(',').map(s => s.trim());

    let html = '';
    availableDefaultHours.forEach(hour => {
        const isSelected = selectedList.includes(hour);
        const styleAttr = isSelected
            ? 'background: linear-gradient(135deg, #0ea5e9, #10b981) !important; color: #ffffff !important; border: 1px solid #38bdf8 !important; box-shadow: 0 4px 12px rgba(14, 165, 233, 0.35) !important;'
            : 'background: rgba(30, 41, 59, 0.85) !important; color: #94a3b8 !important; border: 1px solid rgba(148, 163, 184, 0.25) !important;';
        const icon = isSelected ? 'fa-solid fa-circle-check text-white' : 'fa-regular fa-circle text-muted';
        html += '<button type="button" class="btn btn-sm rounded-pill px-3 py-1 fw-bold d-flex align-items-center gap-1" style="' + styleAttr + '" onclick="toggleSlotChip(\'' + hour + '\')">' +
                '<i class="' + icon + ' me-1"></i> ' + hour +
                '</button>';
    });
    container.innerHTML = html;
}

function toggleSlotChip(hour) {
    const input = document.getElementById('clinicTimeSlotsInput');
    if (!input) return;
    let selectedList = input.value ? input.value.split(',').map(s => s.trim()).filter(Boolean) : [];
    if (selectedList.includes(hour)) {
        selectedList = selectedList.filter(h => h !== hour);
    } else {
        selectedList.push(hour);
        selectedList.sort();
    }
    input.value = selectedList.join(',');
    renderSlotChipsFromInput();
}

function selectAllSlots(all) {
    const input = document.getElementById('clinicTimeSlotsInput');
    if (!input) return;
    input.value = all ? availableDefaultHours.join(',') : '';
    renderSlotChipsFromInput();
}

document.addEventListener('DOMContentLoaded', function() {
    renderSlotChipsFromInput();
});
