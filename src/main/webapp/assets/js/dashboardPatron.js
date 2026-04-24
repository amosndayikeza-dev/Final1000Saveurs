
        // Script pour récupérer les données du dashboard
        document.addEventListener('DOMContentLoaded', function() {
            // Récupérer les indicateurs depuis l'API
            fetch('/api/patron/dashboard')
                .then(response => {
                    if (!response.ok) throw new Error('Erreur chargement dashboard');
                    return response.json();
                })
                .then(data => {
                    document.getElementById('deptCount').innerText = data.departmentsCount || 0;
                    document.getElementById('empCount').innerText = data.employeesCount || 0;
                    document.getElementById('debtTotal').innerHTML = (data.pendingDebts || 0).toLocaleString() + ' fbu';
                    // Si vous avez d'autres indicateurs (rapports, etc.)
                    document.getElementById('reportsCount').innerText = data.reportsCount || 0;
                })
                .catch(err => console.error(err));

            // Récupérer les infos de l'utilisateur connecté (pour l'affichage)
            fetch('/api/auth/me')
                .then(response => response.json())
                .then(user => {
                    document.getElementById('userName').innerText = user.userName || 'Utilisateur';
                    document.getElementById('userRole').innerText = user.role === 'patron' ? 'Patron' : 'Utilisateur';
                    document.getElementById('profilName').innerText = user.userName || '';
                    document.getElementById('profilRole').innerText = user.role === 'patron' ? 'Patron' : '';
                })
                .catch(err => console.error(err));

            // Optionnel : récupérer les dernières actions (employé, rapport, département récents)
            // Vous devez créer un endpoint /api/patron/recent-activities ou appeler plusieurs endpoints
        });