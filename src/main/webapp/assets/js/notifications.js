// ============================================================
// NOTIFICATIONS PERSONNALISÉES (FICHIER COMMUN)
// ============================================================

function showNotification(message, type = 'info', title = '', duration = 3000) {
    const defaultTitles = {
        success: '✅ Succès',
        error: '❌ Erreur',
        warning: '⚠️ Attention',
        info: 'ℹ️ Information'
    };
    const finalTitle = title || defaultTitles[type] || 'Information';
    const toast = document.createElement('div');
    toast.className = `tft-toast tft-toast-${type}`;
    let icon = '';
    switch (type) {
        case 'success': icon = 'fas fa-check-circle'; break;
        case 'error': icon = 'fas fa-exclamation-circle'; break;
        case 'warning': icon = 'fas fa-exclamation-triangle'; break;
        default: icon = 'fas fa-info-circle';
    }
    toast.innerHTML = `
        <i class="${icon}"></i>
        <div class="tft-toast-content">
            <div class="tft-toast-title">${finalTitle}</div>
            <div class="tft-toast-message">${escapeHtml(message)}</div>
        </div>
        <i class="fas fa-times tft-toast-close"></i>
    `;
    document.body.appendChild(toast);
    const closeBtn = toast.querySelector('.tft-toast-close');
    closeBtn.addEventListener('click', () => closeNotification(toast));
    const timeout = setTimeout(() => closeNotification(toast), duration);
    toast.dataset.timeout = timeout;
}

function closeNotification(toast) {
    if (toast.classList.contains('tft-toast-hide')) return;
    if (toast.dataset.timeout) clearTimeout(parseInt(toast.dataset.timeout));
    toast.classList.add('tft-toast-hide');
    setTimeout(() => { if (toast.parentNode) toast.parentNode.removeChild(toast); }, 300);
}

function showSuccess(message, title = '') { showNotification(message, 'success', title, 3000); }
function showError(message, title = '') { showNotification(message, 'error', title, 4000); }
function showWarning(message, title = '') { showNotification(message, 'warning', title, 3500); }
function showInfo(message, title = '') { showNotification(message, 'info', title, 2500); }

function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>]/g, m => ({ '&':'&amp;', '<':'&lt;', '>':'&gt;' }[m]));
}