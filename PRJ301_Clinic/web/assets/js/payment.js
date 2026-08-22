/**
 * ============================================================================
 * PAYMENT & SEPAY VIETQR CONTROLLER (100% UNICODE-SAFE ENCODING)
 * ============================================================================
 */

let pollInterval = null;

function initPaymentPolling() {
    pollInterval = setInterval(checkPaymentStatus, 3000);
}

function checkPaymentStatus() {
    const ctx = window.PAYMENT_CTX || '';
    const appId = window.PAYMENT_APPOINTMENT_ID || '';
    if (!ctx || !appId) return;

    fetch(ctx + '/booking?action=check-payment-status&id=' + appId)
        .then(res => res.json())
        .then(data => {
            if (data.paymentStatus === 'PAID') {
                if (pollInterval) clearInterval(pollInterval);
                const badge = document.getElementById('paymentStatusBadge');
                const text = document.getElementById('paymentStatusText');
                const spinner = document.getElementById('paymentStatusSpinner');

                if (badge && text) {
                    badge.className = 'badge px-3 py-2 rounded-pill slot-btn-selected mb-2 d-inline-flex align-items-center justify-content-center gap-2 w-100';
                    if (spinner) spinner.className = 'fa-solid fa-circle-check text-emerald';
                    text.innerText = '\u0110\u00c3 THANH TO\u00c1N SEPAY TH\u00c0NH C\u00d4NG!';
                }

                if (typeof showToast === 'function') {
                    showToast('X\u00e1c nh\u1eadn thanh to\u00e1n SePay VietQR th\u00e0nh c\u00f4ng!', true);
                }

                setTimeout(() => {
                    window.location.href = ctx + '/MainController?action=history';
                }, 2000);
            }
        })
        .catch(err => console.log('Checking status...'));
}

function confirmPayCash(appointmentId) {
    const ctx = window.PAYMENT_CTX || '';
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'X\u00e1c Nh\u1eadn Thanh To\u00e1n Ti\u1ec1n M\u1eb7t?',
            text: 'B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n chuy\u1ec3n sang h\u00ecnh th\u1ee9c thanh to\u00e1n ti\u1ec1n m\u1eb7t tr\u1ef1c ti\u1ebfp t\u1ea1i qu\u1ea7y l\u1ec5 t\u00e2n khi \u0111\u1ebfn kh\u00e1m?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#f59e0b',
            cancelButtonColor: '#64748b',
            confirmButtonText: '<i class="fa-solid fa-money-bill-wave me-1"></i> \u0110\u1ed3ng \u00dd',
            cancelButtonText: 'Quay L\u1ea1i',
            background: '#0f172a',
            color: '#f8fafc',
            customClass: {
                popup: 'border border-warning border-opacity-40 shadow-lg'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = ctx + '/booking?action=pay-cash&id=' + appointmentId;
            }
        });
    } else {
        if (confirm('B\u1ea1n c\u00f3 ch\u1eafc ch\u1eafn mu\u1ed1n chuy\u1ec3n sang thanh to\u00e1n ti\u1ec1n m\u1eb7t t\u1ea1i qu\u1ea7y kh\u00f4ng?')) {
            window.location.href = ctx + '/booking?action=pay-cash&id=' + appointmentId;
        }
    }
}

function simulateSepayWebhook(appointmentId) {
    const ctx = window.PAYMENT_CTX || '';
    if (typeof showToast === 'function') {
        showToast('\u0110ang g\u1eedi t\u00edn hi\u1ec7u m\u00f4 ph\u1ecfng thanh to\u00e1n SePay...', false);
    }
    fetch(ctx + '/sepay-webhook?appointmentId=' + appointmentId)
        .then(res => res.json())
        .then(data => {
            if (data.status === 200) {
                checkPaymentStatus();
            } else {
                if (typeof showToast === 'function') {
                    showToast('L\u1ed7i m\u00f4 ph\u1ecfng thanh to\u00e1n: ' + data.message, false);
                }
            }
        })
        .catch(err => {
            if (typeof showToast === 'function') {
                showToast('Kh\u1edfi ch\u1ea1y m\u00f4 ph\u1ecfng th\u1ea5t b\u1ea1i', false);
            }
        });
}

document.addEventListener('DOMContentLoaded', function () {
    initPaymentPolling();
});
