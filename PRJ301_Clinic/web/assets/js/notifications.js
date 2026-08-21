/**
 * notifications.js - Quản lý Trung Tâm Thông Báo Thời Gian Thực (Notification Center)
 * Hỗ trợ Smart Polling khi tab active, đánh dấu đã đọc và cập nhật UI mượt mà.
 */
class NotificationCenter {
    constructor() {
        this.contextPath = window.APP_CONTEXT_PATH || (window.location.pathname.startsWith('/PRJ301_Clinic') ? '/PRJ301_Clinic' : '');
        this.unreadBadge = document.getElementById('notification-badge');
        this.notificationList = document.getElementById('notification-list');
        this.markAllBtn = document.getElementById('mark-all-read-btn');
        this.pollingInterval = 15000; // 15s
        this.timer = null;

        this.init();
    }

    init() {
        if (!this.unreadBadge && !this.notificationList) {
            return;
        }

        // Lấy thông báo lần đầu ngay khi mở trang
        this.fetchNotifications();

        // Polling thông minh: chỉ chạy khi tab đang mở (Visibility State)
        this.startPolling();

        document.addEventListener('visibilitychange', () => {
            if (document.visibilityState === 'visible') {
                this.fetchNotifications();
                this.startPolling();
            } else {
                this.stopPolling();
            }
        });

        if (this.markAllBtn) {
            this.markAllBtn.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                this.markAllAsRead();
            });
        }
    }

    startPolling() {
        this.stopPolling();
        this.timer = setInterval(() => {
            if (document.visibilityState === 'visible') {
                this.fetchNotifications();
            }
        }, this.pollingInterval);
    }

    stopPolling() {
        if (this.timer) {
            clearInterval(this.timer);
            this.timer = null;
        }
    }

    fetchNotifications() {
        fetch(this.contextPath + '/api/notifications?action=get-notifications', {
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        })
        .then(res => res.json())
        .then(data => {
            if (data && data.success) {
                this.updateBadge(data.unreadCount);
                this.renderList(data.notifications);
            }
        })
        .catch(err => console.debug('Notifications polling:', err));
    }

    updateBadge(count) {
        if (!this.unreadBadge) return;
        if (count > 0) {
            this.unreadBadge.innerText = count > 99 ? '99+' : count;
            this.unreadBadge.style.display = 'inline-flex';
            this.unreadBadge.classList.remove('d-none');
        } else {
            this.unreadBadge.innerText = '0';
            this.unreadBadge.style.display = 'none';
            this.unreadBadge.classList.add('d-none');
        }
    }

    getIconForType(type) {
        switch ((type || '').toUpperCase()) {
            case 'PAYMENT':
                return '<div class="noti-icon noti-icon-payment"><i class="fa-solid fa-credit-card"></i></div>';
            case 'APPOINTMENT':
                return '<div class="noti-icon noti-icon-appointment"><i class="fa-solid fa-calendar-check"></i></div>';
            case 'SCHEDULE':
                return '<div class="noti-icon noti-icon-schedule"><i class="fa-solid fa-clock"></i></div>';
            case 'MEDICAL':
                return '<div class="noti-icon noti-icon-medical"><i class="fa-solid fa-notes-medical"></i></div>';
            default:
                return '<div class="noti-icon noti-icon-system"><i class="fa-solid fa-bell"></i></div>';
        }
    }

    renderList(notifications) {
        if (!this.notificationList) return;

        if (!notifications || notifications.length === 0) {
            this.notificationList.innerHTML = '<div class="p-4 text-center text-white-50 small"><i class="fa-regular fa-bell-slash fs-4 d-block mb-2 text-cyan opacity-50"></i>Bạn chưa có thông báo nào</div>';
            return;
        }

        let html = '';
        notifications.forEach(n => {
            const isUnread = !n.isRead;
            const itemBg = isUnread ? 'noti-item-unread' : 'noti-item-read';
            const iconHtml = this.getIconForType(n.type);
            const targetLink = n.link ? (n.link.startsWith('http') ? n.link : this.contextPath + '/' + n.link.replace(/^\//, '')) : 'javascript:void(0);';

            html += `
                <a href="${targetLink}" class="noti-item ${itemBg}" onclick="notificationCenter.markSingleRead(${n.id})">
                    ${iconHtml}
                    <div class="noti-content">
                        <div class="d-flex justify-content-between align-items-center mb-1">
                            <h6 class="noti-title mb-0 ${isUnread ? 'text-white fw-bold' : 'text-white-50'}">${n.title}</h6>
                            ${isUnread ? '<span class="noti-dot"></span>' : ''}
                        </div>
                        <p class="noti-msg mb-1 text-white-50">${n.message}</p>
                        <span class="noti-time text-cyan"><i class="fa-regular fa-clock me-1"></i>${n.timeAgo || 'Vừa xong'}</span>
                    </div>
                </a>
            `;
        });

        this.notificationList.innerHTML = html;
    }

    markSingleRead(id) {
        if (!id) return;
        const formData = new URLSearchParams();
        formData.append('action', 'mark-read');
        formData.append('id', id);

        fetch(this.contextPath + '/api/notifications', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: formData
        }).catch(err => console.debug(err));
    }

    markAllAsRead() {
        const formData = new URLSearchParams();
        formData.append('action', 'mark-all-read');

        fetch(this.contextPath + '/api/notifications', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: formData
        })
        .then(res => res.json())
        .then(data => {
            if (data && data.success) {
                this.updateBadge(0);
                this.fetchNotifications();
            }
        })
        .catch(err => console.error(err));
    }
}

// Khởi tạo toàn cục
let notificationCenter = null;
document.addEventListener('DOMContentLoaded', () => {
    notificationCenter = new NotificationCenter();
});