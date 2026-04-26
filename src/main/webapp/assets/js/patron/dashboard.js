// Fonctions de modal (à conserver)
        function closeModal() {
            document.getElementById('deconnection-modal').style.display = 'none';
        }
        function deconnectionModal() {
            document.getElementById('deconnection-modal').style.display = 'flex';
        }

        // Chargement des données du tableau de bord
        async function loadDashboardData() {
            try {
                const response = await fetch('/api/patron/dashboard');
                if (!response.ok) throw new Error('Erreur chargement dashboard');
                const data = await response.json();
                document.getElementById('deptCount').innerText = data.departmentsCount || 0;
                document.getElementById('empCount').innerText = data.employeesCount || 0;
                document.getElementById('debtTotal').innerHTML = (data.pendingDebts || 0).toLocaleString() + ' fbu';
                document.getElementById('reportsCount').innerText = data.reportsCount || 0;

                // Mettre à jour les activités récentes si votre API les fournit
                if (data.recentEmployee) {
                    document.getElementById('lastEmployee').innerText = data.recentEmployee.name;
                    document.getElementById('lastEmployeeDate').innerText = data.recentEmployee.date;
                }
                if (data.recentReport) {
                    document.getElementById('lastReport').innerText = data.recentReport.name;
                    document.getElementById('lastReportDate').innerText = data.recentReport.date;
                }
                if (data.recentDepartment) {
                    document.getElementById('lastDepartment').innerText = data.recentDepartment.name;
                    document.getElementById('lastDepartmentDate').innerText = data.recentDepartment.date;
                }
            } catch (error) {
                console.error('Erreur chargement dashboard :', error);
            }
        }

        // Charger les informations de l'utilisateur connecté
        async function loadUserInfo() {
            try {
                const response = await fetch('/api/auth/me');
                if (response.ok) {
                    const user = await response.json();
                    document.getElementById('userName').innerText = user.userName || 'Utilisateur';
                    document.getElementById('userRole').innerText = user.role === 'patron' ? 'Patron' : 'Utilisateur';
                }
            } catch (error) {
                console.error('Erreur chargement utilisateur :', error);
            }
        }

        // Déconnexion
        document.getElementById('logoutYes').addEventListener('click', async (e) => {
            e.preventDefault();
            await fetch('/api/auth/logout', { method: 'POST' });
            window.location.href = '/login.html';
        });

        // Initialisation
        document.addEventListener('DOMContentLoaded', () => {
            loadDashboardData();
            loadUserInfo();
        });