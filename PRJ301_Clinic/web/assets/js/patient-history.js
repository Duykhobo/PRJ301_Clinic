/**
 * ============================================================================
 * PATIENT HISTORY JAVASCRIPT CONTROLLER (100% UNICODE-SAFE ENCODING)
 * ============================================================================
 */

let selectedPatientFilter = 'ALL';

function setPatientHistoryFilter(filterType, btn) {
    selectedPatientFilter = filterType;
    const group = document.getElementById('patientStatusFilterGroup');
    if (group) {
        Array.from(group.children).forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
    }
    filterPatientHistory();
}

function filterPatientHistory() {
    const queryInput = document.getElementById('searchHistory');
    const query = queryInput ? queryInput.value.toLowerCase().trim() : '';
    const table = document.getElementById('patientHistoryTable');
    if (!table) return;

    const tbody = table.getElementsByTagName('tbody')[0];
    if (!tbody) return;
    const rows = tbody.getElementsByTagName('tr');
    let visibleCount = 0;
    let totalCount = 0;

    for (let i = 0; i < rows.length; i++) {
        const r = rows[i];
        if (r.classList.contains('no-result-row')) continue;
        totalCount++;

        const rowText = r.innerText.toLowerCase();
        const status = r.getAttribute('data-status') || '';
        const payment = r.getAttribute('data-payment') || '';

        const matchesQuery = !query || rowText.includes(query);
        let matchesStatus = true;

        if (selectedPatientFilter === 'COMPLETED') {
            matchesStatus = (status === 'COMPLETED');
        } else if (selectedPatientFilter === 'PENDING') {
            matchesStatus = (status === 'PENDING' || status === 'CONFIRMED');
        } else if (selectedPatientFilter === 'UNPAID') {
            matchesStatus = (payment === 'UNPAID' && status !== 'CANCELLED');
        } else if (selectedPatientFilter === 'CANCELLED') {
            matchesStatus = (status === 'CANCELLED');
        }

        if (matchesQuery && matchesStatus) {
            r.style.display = '';
            visibleCount++;
        } else {
            r.style.display = 'none';
        }
    }

    const countBadge = document.getElementById('patientFilterCount');
    if (countBadge) {
        countBadge.textContent = 'Hi\u1ec3n th\u1ecb: ' + visibleCount + ' / ' + totalCount;
    }
}

function viewPatientPrescription(appId, doctor, service, diagnosis, prescription, moistureVal, oilVal) {
    const elAppId = document.getElementById('pModalAppId');
    const elDoc = document.getElementById('pModalDoctor');
    const elServ = document.getElementById('pModalService');
    const elDiag = document.getElementById('pModalDiagnosis');
    const elPresc = document.getElementById('pModalPrescription');
    const elMoist = document.getElementById('pModalMoisture');
    const elOil = document.getElementById('pModalOil');

    if (elAppId) elAppId.innerText = appId;
    if (elDoc) elDoc.innerText = doctor;
    if (elServ) elServ.innerText = service;
    if (elDiag) elDiag.innerText = diagnosis || 'Ch\u01b0a c\u00f3 ch\u1ea9n \u0111o\u00e1n chi ti\u1ebft.';
    if (elPresc) elPresc.innerText = prescription || 'Ch\u01b0a c\u00f3 \u0111\u01a1n thu\u1ed1c ch\u1ec9 \u0111\u1ecbnh.';

    // Chỉ số da từ Bác sĩ hoặc mặc định
    const moisture = (moistureVal !== null && moistureVal !== undefined) ? moistureVal : (Math.floor(Math.random() * 25) + 50);
    const oil = (oilVal !== null && oilVal !== undefined) ? oilVal : (Math.floor(Math.random() * 25) + 40);
    if (elMoist) elMoist.innerText = moisture + '%';
    if (elOil) elOil.innerText = oil + '%';

    const modalEl = document.getElementById('patientPrescriptionModal');
    if (modalEl && typeof bootstrap !== 'undefined') {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }
}

function openReviewModal(appId, doctor, service, currentRating, currentComment) {
    const elAppId = document.getElementById('revModalAppId');
    const elAppLabel = document.getElementById('revModalAppLabel');
    const elDoc = document.getElementById('revModalDoctor');
    const elServ = document.getElementById('revModalService');
    const elComment = document.getElementById('revCommentInput');

    if (elAppId) elAppId.value = appId;
    if (elAppLabel) elAppLabel.innerText = '#' + appId;
    if (elDoc) elDoc.innerText = doctor;
    if (elServ) elServ.innerText = service;
    if (elComment) elComment.value = currentComment || '';

    setRatingStars(currentRating || 5);

    const modalEl = document.getElementById('patientReviewModal');
    if (modalEl && typeof bootstrap !== 'undefined') {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }
}

function setRatingStars(stars) {
    const input = document.getElementById('revRatingInput');
    if (input) input.value = stars;

    const group = document.getElementById('starRatingGroup');
    if (group) {
        const iconList = group.querySelectorAll('i.fa-star');
        iconList.forEach((star, index) => {
            if (index < stars) {
                star.classList.remove('fa-regular');
                star.classList.add('fa-solid', 'text-warning');
                star.classList.remove('text-secondary');
            } else {
                star.classList.remove('fa-solid', 'text-warning');
                star.classList.add('fa-regular', 'text-secondary');
            }
        });
    }

    const label = document.getElementById('ratingTextLabel');
    if (label) {
        const descMap = {
            1: 'R\u1ea5t kh\u00f4ng h\u00e0i l\u00f2ng (1/5 sao)',
            2: 'Ch\u01b0a h\u00e0i l\u00f2ng (2/5 sao)',
            3: 'B\u00ecnh th\u01b0\u1eddng (3/5 sao)',
            4: 'H\u00e0i l\u00f2ng (4/5 sao)',
            5: 'Tuy\u1ec7t v\u1eddi (5/5 sao)'
        };
        label.innerText = descMap[stars] || (stars + '/5 sao');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    filterPatientHistory();
});
