// Intégrez directement le script ici (ou gardez l'externe, mais assurez-vous des chemins)
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
                errorDiv.textContent = 'Veuillez remplir tous les champs.';
                return;
            }
            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });
                const data = await response.json();
                if (response.ok && data.success) {
                    const role = data.role;
                    if (role === 'patron') window.location.href = '/patron/dashboard.html';
                    else if (role === 'manager') window.location.href = '/manager/dashboard.html';
                    else if (role === 'admin') window.location.href = '/admin/dashboard.html';
                    else window.location.href = '/';
                } else {
                    errorDiv.textContent = data.error || 'Identifiants incorrects';
                }
            } catch (err) {
                errorDiv.textContent = 'Erreur de connexion au serveur.';
                console.error(err);
            }
        });
    });