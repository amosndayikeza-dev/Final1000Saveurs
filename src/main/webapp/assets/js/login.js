// login.js - Gestion de l'authentification

document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const errorDiv = document.getElementById('error-message');

    loginForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const email = emailInput.value.trim();
        const password = passwordInput.value;

        if (!email || !password) {
            // errorDiv.textContent = Veuillez remplir tous les champs.';
            errorDiv.innerHTML = `
            <div class="tft-icon-carre-moyen tft-bg-red">
                <i class="fas fa-exclamation-triangle tft-clr-remain-white"></i>
            </div>
            <p class="tft-title1">Veuillez remplir tous les champs</p>`;
            return;
        }

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (response.ok && data.success) {
                // Redirection selon le rôle
                if (data.role === 'patron') {
                    window.location.href = '/patron/dashboardpatron.php';
                } else if (data.role === 'manager') {
                    window.location.href = '/manager/dashboard.html';
                } else if (data.role === 'admin') {
                    window.location.href = '/admin/dashboard.html';
                } else {
                    // Rôle inconnu → accueil
                    window.location.href = '/';
                }
            } else {
                // errorDiv.textContent = data.error || 'Email ou mot de passe incorrect.';
                errorDiv.innerHTML = data.error || `
                <div class="tft-icon-carre-moyen tft-bg-red">
                    <i class="fas fa-warning tft-clr-remain-white"></i>
                </div>
                <p class="tft-title1">Email ou mot de passe incorrect</p>`;
            }
        } catch (error) {
            console.error('Erreur de connexion :', error);
            // errorDiv.textContent ='Erreur de connexion au serveur. Veuillez réessayer.';
            errorDiv.innerHTML = `
            <div class="tft-icon-carre-moyen tft-bg-red">
                <i class="fas fa-warning tft-clr-remain-white"></i>
            </div>
            <p class="tft-title1">Erreur de connexion au serveur. Veuillez réessayer</p>`;
        }
    });
});