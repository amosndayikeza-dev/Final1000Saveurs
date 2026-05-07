
<!-- head debut-->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>1000saveurs</title>
    <link rel="icon" href="../assets/images/icons/abonnes.png">
    <link rel="icon" href="../assets/images/icons/abonnes.png">
    <link rel="stylesheet" href="../assets/css/styles.css">
    <link rel="stylesheet" href="../assets/css/tft-configStyles.css">
    <link rel="stylesheet" href="../assets/css/responsiveStyles.css">
    <link rel="stylesheet" href="../assets/fonts/feather/feather.css">
    <link rel="stylesheet" href="../assets/font-awesome/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<!-- head fin-->
<body>
    <div class="containerAll">
        <!-- partie gauche debut-->
        <div class="containerLeft" id="container-left">
            <div class="tft-icon-round-petit tft-p-absolute tft-top-5 tft-right-10 tft-bg-black3" onclick="hideSidebar()" id="icon-sidebar">
                <i class="fas fa-arrow-right"></i>
            </div>
            <div class="c-left-header">
                <a href="./dashboardpatron.php">
                    <div class="tft-logo-avatar tft-bdr-greensav-1">
                        <img src="../assets/images/icons/abonnes.png" alt="">
                    </div>
                </a>
                <div class="app-title">
                    <h1 class="app-name">1000<span>Saveurs</span></h1>
                </div>
            </div>
            <div class="menu">
                <div class="c-left-menu">
                    <a href="./dashboardpatron.php">
                        <div class="c-left-option">
                            <div class="tft-icon-round" onclick="showSidebar()">
                                <i class="fas fa-home"></i>
                            </div>
                            <p class="option-name">Dashboard</p>
                        </div>
                    </a>
                    <a href="./departementspatron.php">
                        <div class="c-left-option">
                            <div class="tft-icon-round">
                                <i class="fas fa-sitemap"></i>
                            </div>
                            <p class="option-name">Départements</p>
                        </div>
                    </a>
                    <div class="repport">
                        <div class="c-left-option tft-br-5-5-0-0 tft-p-relative">
                            <div class="tft-icon-round">
                                <i class="fas fa-chart-bar"></i>
                            </div>
                            <p class="option-name">Rapports</p>
                            <div class="tft-chevron-icon tft-p-absolute tft-top-15 tft-right-5" onclick="showRepportOptions()">
                                <i class="fas fa-chevron-down tft-clr-gris1"></i>
                            </div>
                        </div>
                        <div class="repport-options" id="repport-options">
                            <a href="./salespatron.html" class="repport-title">
                                <div class="tft-icon-round-petit tft-bdr-greensav-1">
                                    <i class="fas fa-chart-bar tft-clr-greensav"></i>
                                </div>
                                <p class="tft-title4">Ventes</p>
                            </a>
                            <a href="./repportdebtspatron.html" class="repport-title">
                                <div class="tft-icon-round-petit tft-bdr-greensav-1">
                                    <i class="fas fa-chart-bar tft-clr-greensav"></i>
                                </div>
                                <p class="tft-title4">Dettes</p>
                            </a>
                            <a href="./repportsalarypatron.html" class="repport-title">
                                <div class="tft-icon-round-petit tft-bdr-greensav-1">
                                    <i class="fas fa-chart-bar tft-clr-greensav"></i>
                                </div>
                                <p class="tft-title4">Salaires</p>
                            </a>
                        </div>
                    </div>
                    <a href="./employespatron.html">
                        <div class="c-left-option">
                            <div class="tft-icon-round">
                                <i class="fas fa-users"></i>
                            </div>
                            <p class="option-name">Employés</p>
                        </div>
                    </a>
                    <a href="./stockpatron.html">
                        <div class="c-left-option">
                            <div class="tft-icon-round">
                                <i class="fas fa-users"></i>
                            </div>
                            <p class="option-name">Stock</p>
                        </div>
                    </a>
                </div>
                <div class="user-option">
                    <div class="tft-avatar-profil-petit tft-bdr-orangesav-2 tft-cursor-pointer" onclick="showContainerRight()">
                        <img src="../assets/images/icons/femme.jpg" alt="">
                    </div>
                    <div class="user-infos">
                        <div class="user-name">
                            <h2 id="userName" class="tft-title4 tft-fw-600"> Chargement</h2>
                            <p id="userRole" class="tft-sm-title1">Patronne</p>
                        </div>
                        <div class="tft-icon-carre-moyen tft-bg-black3 tft-transition" onclick="deconnectionModal()">
                            <i class="fe fe-log-out tft-clr-red"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- partie gauche fin-->
        <!-- partie centre -->
        <div class="containerCenter">
            <div class="c-center-header">
                <h2 class="tft-title1 tft-fw-600 tft-center tft-gap-10px">
                    <div class="tft-icon-carre tft-bdr-gris-1" onclick="showSidebar()">
                        <i class="fas fa-bars"></i>
                    </div>
                    Départements
                </h2>
                <div class="header-aside">
                    <div class="tft-search-withIcon">
                        <input type="search" placeholder="Rechercher" class="tft-clr-white3">
                        <div class="tft-search-withIcon-icon tft-bg-greensav">
                            <i class="fas fa-search tft-clr-remain-white tft-fw-600"></i>
                        </div>
                    </div>
                    <!-- <div class="tft-users tft-bdr-gris-1" id="tft-users">
                        <div class="tft-users-pics"  id="tft-users-pics">
                            <div class="tft-user-pic tft-bdr-greensav-1">
                                <img src="../assets/images/icons/femme.png">
                            </div>
                            <div class="tft-user-pic tft-bdr-greensav-1">
                                <img src="../assets/images/icons/femme.png">
                            </div>
                            <div class="tft-user-pic tft-bdr-greensav-1">
                                <img src="../assets/images/icons/femme.png">
                            </div>
                            <div class="tft-user-pic tft-bdr-greensav-1">
                                <img src="../assets/images/icons/femme.png">
                            </div>
                            <div class="tft-user-pic tft-bdr-greensav-1">
                                <p class="tft-clr-white3">+15</p>
                            </div>
                        </div>
                        <h4 class="tft-sm-title1 tft-bg-black1" id="user-desc">Utilisateurs</h4>
                    </div> -->
                    <div class="tft-icon-carre-moyen tft-bdr-greensav-1 tft-bg-black3" onclick="showContainerRightNotification()">
                        <i class="fas fa-bell tft-clr-greensav"></i>
                    </div>
                    <div class="tft-icon-carre-moyen tft-bdr-greensav-1 tft-bg-black3"  onclick="showContainerRightParametre()">
                        <i class="fas fa-cog tft-clr-greensav"></i>
                    </div>
                    <div class="tft-icon-carre-moyen tft-bg-orangesav2" id="btn-light-mode" onclick="changeMode()">
                        <i class="fas fa-moon tft-clr-white"></i>
                    </div>
                    <div class="tft-icon-carre-moyen tft-bg-orangesav2 tft-hidden" id="btn-dark-mode" onclick="changeMode()">
                        <i class="fas fa-moon tft-clr-white"></i>
                    </div>
                </div>
            </div>
            <div class="container_departements" id="container-departements">
                <!-- <div class="departement">
                    <p class="tft-sm-title2 tft-bg-black2" id="creation-date">Crée le 24-3-2026</p>
                    <div class="departement-details">
                        <h3 class="tft-title2 tft-clr-orangesav tft-a-self-center">Boucherie</h3>
                        <p class="tft-sm-title1 tft-text-justify tft-w-100 tft-break-word tft-fs-15px tft-line-h-1-4">Lorem ipsum dolor sit amet consectetur adipisicing elit. Perspiciatis quod ab dolor repudiandae nemo quia et alias animi</p>
                    </div>
                    <div class="departement-infos">
                        <div class="manager-infos">
                            <div class="departement-info-icon">
                                <div class="tft-icon-round-moyen tft-bg-black2 tft-bdr-greensav-1 tft-cursor-pointer">
                                    <i class="fas fa-user-tie tft-clr-greensav"></i>
                                </div>
                            </div>
                            <div class="manager-name">
                                <h4 class="tft-title4">Lania Ishimwe</h4>
                                <p class="tft-sm-title1">Manager</p>
                            </div>
                        </div>
                        <div class="manager-infos">
                            <div class="departement-info-icon">
                                <div class="tft-icon-round-moyen tft-bg-black2 tft-bdr-greensav-1 tft-cursor-pointer">
                                    <i class="fas fa-map-marker-alt tft-clr-greensav"></i>
                                </div>
                            </div>
                            <div class="manager-name">
                                <h4 class="tft-title4">Rohero 2</h4>
                                <p class="tft-sm-title1">Adresse</p>
                            </div>
                        </div>
                    </div>
                    <div class="departement-btns">
                        <a class="tft-btn" href="#">
                            Editer
                        </a>
                        <button class="tft-btn">Supprimer</button>
                    </div>
                </div> -->
                <!--<div class="departement">
                </div>
                <div class="departement">
                </div>-->
                <!-- <div class="departement" id="btn-ajouter-departement">
                    <div class="tft-icon-round-grand tft-bg-greensav" onclick="ajouterDepartement()">
                        <i class="fas fa-plus tft-clr-remain-white"></i>
                    </div>
                    <p class="tft-title2">Ajouter un département</p>
                </div> -->
            </div>
        </div>
        <!-- partie droite ajoute la classe active pour montrer debut-->
        <div class="containerRight tft-p-relative" id="container-right">
            <div class="tft-close-icon tft-p-absolute tft-top-15 tft-right-15 tft-hover-red" onclick="closeModalInfos()">
                <i class="fas fa-times tft-clr-white3"></i>
            </div>
            <div class="admin-profil">
                <div class="tft-avatar-profil-moyen2 tft-bdr-greensav-2">
                    <img src="../assets/images/icons/femme.jpg">
                </div>
                <h4 class="tft-title4 tft-fw-600">Triphine Iribagiza</h4>
                <p class="tft-sm-title1">Patronne</p>
            </div>
            <div class="admin-infos-container" id="profil-admin">
                <div class="tft-w-100 tft-centre-tout tft-gap-20px tft-mt-10 infos-options">
                    <div class="tft-icon-carre tft-bg-gris tft-bg-greensav" onclick="showProfil()" id="btn-show-profil">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="tft-icon-carre tft-bg-gris" onclick="showNotification()" id="btn-show-notification">
                        <i class="fas fa-bell"></i>
                    </div>
                    <div class="tft-icon-carre tft-bg-gris" onclick="showParametre()" id="btn-show-parametre">
                        <i class="fas fa-cog"></i>
                    </div>
                </div>
                <div class="container-infos-user tft-show tft-mt-10" id="profil-modal">
                    <div class="infos-container">
                        <div class="infos-header">
                            <div class="tft-icon-round-petit tft-bg-black2 tft-bdr-greensav-1">
                                <i class="fas fa-user tft-clr-greensav"></i>
                            </div>
                            <h3 class="tft-title3">Les informations du Profil</h3>
                        </div>
                        <div class="infos-details">
                            <div class="info-titre">
                                <div class="info-icon tft-bg-orangesav2">
                                    <i class="fe fe-mail tft-clr-white"></i>
                                </div>
                                <p class="tft-clr-white3 tft-title4">Email</p>
                            </div>
                            <p class="tft-text-start tft-opacity-7">lania@gmail.com</p>
                        </div>
                        <div class="infos-details">
                            <div class="info-titre">
                                <div class="info-icon tft-bg-orangesav2">
                                    <i class="fe fe-phone tft-clr-white"></i>
                                </div>
                                <p class="tft-clr-white3 tft-title4">Telephone</p>
                            </div>
                            <p class="tft-text-start tft-opacity-7">+257 68 90 81 32</p>
                        </div>
                        <div class="infos-details">
                            <div class="info-titre">
                                <div class="info-icon tft-bg-orangesav2">
                                    <i class="fe fe-users tft-clr-white"></i>
                                </div>
                                <p class="tft-clr-white3 tft-title4">Genre</p>
                            </div>
                            <p class="tft-text-start tft-opacity-7">masculin</p>
                        </div>
                        <div class="infos-details">
                            <div class="info-titre">
                                <div class="info-icon tft-bg-orangesav2">
                                    <i class="fe fe-calendar tft-clr-white"></i>
                                </div>
                                <p class="tft-clr-white3 tft-title4">Naissance</p>
                            </div>
                            <p class="tft-text-start tft-opacity-7">16-11-2025</p>
                        </div>
                        <div class="infos-details">
                            <div class="info-titre">
                                <div class="info-icon tft-bg-orangesav2">
                                    <i class="fe fe-map-pin tft-clr-white"></i>
                                </div>
                                <p class="tft-clr-white3 tft-title4">Adresse</p>
                            </div>
                            <p class="tft-text-start tft-opacity-7">Burundi</p>
                        </div>
                    </div>
                    <div class="infos-buttons tft-mt-10">
                        <div class="infos-button tft-bg-greensav centre-tout tft-gap-5px">
                            <i class="fe fe-edit tft-clr-remain-white"></i>
                            <a href="#" class="tft-mt-2 tft-clr-remain-white">Editer</a>
                        </div>
                    </div>
                </div>
                <div class="container-infos-user tft-mt-10" id="notification-modal">
                    <div class="infos-container">
                        <div class="infos-header">
                            <div class="tft-icon-round-petit tft-bg-black2 tft-bdr-greensav-1">
                                <i class="fas fa-bell tft-clr-greensav"></i>
                            </div>
                            <h3 class="tft-title3">Notifications</h3>
                        </div>
                        <div class="single-notification">
                            <div class="title-notification">
                                <h4 class="tft-title4">Rapport</h4>
                                <p class="tft-sm-title2">2026-11-04</p>
                            </div>
                            <p class="tft-sm-title1">Lania Ishimwe</p>
                        </div>
                        <div class="single-notification">
                            <div class="title-notification">
                                <h4 class="tft-title4">Rapport</h4>
                                <p class="tft-sm-title2">2026-11-04</p>
                            </div>
                            <p class="tft-sm-title1">Lania Ishimwe</p>
                        </div>
                        <div class="single-notification">
                            <div class="title-notification">
                                <h4 class="tft-title4">Rapport</h4>
                                <p class="tft-sm-title2">2026-11-04</p>
                            </div>
                            <p class="tft-sm-title1">Lania Ishimwe</p>
                        </div>
                        <div class="single-notification">
                            <div class="title-notification">
                                <h4 class="tft-title4">Rapport</h4>
                                <p class="tft-sm-title2">2026-11-04</p>
                            </div>
                            <p class="tft-sm-title1">Lania Ishimwe</p>
                        </div>
                    </div>
                </div>
                <div class="container-infos-user tft-mt-10" id="parametre-modal">
                    <div class="infos-container">
                        <div class="infos-header">
                            <div class="tft-icon-round-petit tft-bg-black2 tft-bdr-greensav-1">
                                <i class="fas fa-cog tft-clr-greensav"></i>
                            </div>
                            <h3 class="tft-title3">Parametres</h3>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- partie droite ajoute la classe active pour montrer fin-->
        <!-- popup de deconnexion -->
        <div class="tft-popup-modal tft-a-center" id="deconnection-modal">
            <div class="tft-popup-container-small tft-bg-remain-black3 tft-p-relative">
                <div class="deconnection-actions">
                    <h3 class="tft-title1 tft-clr-remain-white">Voulez-vous deconnecter ?</h3>
                    <div class="deconnection-options">
                        <a class="tft-btn tft-bg-red tft-clr-remain-white" href="#">Oui</a>
                        <btn class="tft-btn tft-bg-greensav tft-clr-remain-white" onclick="closeModal()">Non</btn>
                    </div>
                </div>
                <div class="tft-close-icon tft-p-absolute tft-top-5 tft-right-10 tft-hover-red" onclick="closeModal()">
                    <i class="fas fa-times tft-clr-remain-white3"></i>
                </div>
            </div>
        </div>
        <!-- popup de deconnexion -->
        <!-- popup d'ajouter le departement -->
        <div class="tft-popup-modal" id="add-departement">
            <div class="tft-popup-container-small tft-bg-remain-black3 tft-p-relative">
                <h1 class="tft-title1 tft-mt-20 tft-clr-remain-white" id="modalTitle">Ajouter un nouveau département</h1>
                <div class="tft-form-container">
                    <form id="departementForm" class="tft-gap-10px">
                        <input type="hidden" id="deptId" name="id">
                        <div class="tft-form-group tft-gap-10px">
                            <label for="nom-departement" class="tft-form-label tft-clr-remain-white tft-flex tft-gap-8px tft-a-center">
                                <div class="tft-icon-round-moyen tft-bdr-orangesav-1"><i class="fas fa-user tft-clr-orangesav"></i></div>
                                Nom du département <span>*</span>
                            </label>
                            <input type="text" class="tft-form-control" id="nom-departement" name="name" placeholder="Tapez le nom ici..." required>
                        </div>
                        <div class="tft-form-group tft-gap-10px">
                            <label for="description-departement" class="tft-form-label tft-clr-remain-white tft-flex tft-gap-8px tft-a-center">
                                <div class="tft-icon-round-moyen tft-bdr-orangesav-1"><i class="fas fa-edit tft-clr-orangesav"></i></div>
                                Description <span>*</span>
                            </label>
                            <textarea class="tft-form-control" id="description-departement" name="description" placeholder="Tapez la description ici..." required></textarea>
                        </div>
                        <div class="tft-form-group tft-gap-10px">
                            <label for="address-departement" class="tft-form-label tft-clr-remain-white tft-flex tft-gap-8px tft-a-center">
                                <div class="tft-icon-round-moyen tft-bdr-orangesav-1"><i class="fas fa-map-marker-alt tft-clr-orangesav"></i></div>
                                Adresse
                            </label>
                            <input type="text" class="tft-form-control" id="address-departement" name="address" placeholder="Adresse">
                        </div>
                        <div class="tft-form-group tft-gap-10px">
                            <label for="manager-id" class="tft-form-label tft-clr-remain-white tft-flex tft-gap-8px tft-a-center">
                                <div class="tft-icon-round-moyen tft-bdr-orangesav-1"><i class="fas fa-user-tie tft-clr-orangesav"></i></div>
                                Gérant
                            </label>
                            <select id="manager-id" name="manager_id" class="tft-form-control">
                                <option value="">-- Aucun --</option>
                            </select>
                        </div>
                        <div class="form-actions tft-flex tft-gap-20px tft-mt-20">
                            <button type="submit" class="tft-btn tft-bdr-orangesav-1 tft-clr-orangesav tft-hover-orangesav">Enregistrer</button>
                            <button type="button" class="tft-btn tft-bdr-gris-1 tft-clr-white" onclick="closeModalForm()">Annuler</button>
                        </div>
                    </form>
                </div>
                <div class="tft-close-icon tft-p-absolute tft-top-5 tft-right-10 tft-hover-red" onclick="closeModalForm()">
                    <i class="fas fa-times tft-clr-remain-white3"></i>
                </div>
            </div>
        </div>
    </div>
    <script src="../assets/js/scripts.js"></script>
    <script>
        // ============================================================
        // FERMETURE DES MODALES
        // ============================================================
        function closeModal() {
            document.getElementById('deconnection-modal').style.display = 'none';
        }

        function deconnectionModal() {
            document.getElementById('deconnection-modal').style.display = 'flex';
        }

        function closeModalForm() {
            document.getElementById('add-departement').style.display = 'none';
            document.getElementById('departementForm').reset();
            document.getElementById('deptId').value = '';
        }

        function openAddModal() {
            document.getElementById('modalTitle').innerText = 'Ajouter un département';
            document.getElementById('departementForm').reset();
            document.getElementById('deptId').value = '';
            document.getElementById('add-departement').style.display = 'flex';
        }

        function openEditModal(id, name, description, address, managerId) {
            document.getElementById('modalTitle').innerText = 'Modifier le département';
            document.getElementById('deptId').value = id;
            document.getElementById('nom-departement').value = name;
            document.getElementById('description-departement').value = description || '';
            document.getElementById('address-departement').value = address || '';
            const select = document.getElementById('manager-id');
            if (select) select.value = managerId || '';
            document.getElementById('add-departement').style.display = 'flex';
        }

        // ============================================================
        // CHARGER LES DÉPARTEMENTS DEPUIS L'API
        // ============================================================
        async function loadDepartements() {
            try {
                // ← CORRECTION : Ajouter le slash à la fin
                const response = await fetch('/api/patron/departements/');

                if (!response.ok) {
                    throw new Error(`Erreur HTTP: ${response.status}`);
                }

                const departments = await response.json();
                const container = document.getElementById('container-departements');

                if (!container) {
                    console.error('Conteneur non trouvé');
                    return;
                }

                container.innerHTML = '';

                if (departments.length === 0) {
                    container.innerHTML = '<div class="error"><p>Aucun département trouvé</p></div>';
                }

                departments.forEach(dept => {
                    const card = document.createElement('div');
                    card.className = 'departement';

                    // Formatage de la date
                    let createdAt = 'Date inconnue';
                    if (dept.createdAt) {
                        const match = dept.createdAt.match(/(\d{4})-(\d{2})-(\d{2})/);
                        if (match) createdAt = `${match[3]}/${match[2]}/${match[1]}`;
                    }

                    card.innerHTML = `
                        <p class="tft-sm-title2 tft-bg-black2" id="creation-date">Créé le ${createdAt}</p>
                        <div class="departement-details">
                            <h3 class="tft-title2 tft-clr-orangesav tft-a-self-center">${escapeHtml(dept.name)}</h3>
                            <p class="tft-sm-title1 tft-text-justify tft-w-100 tft-break-word tft-fs-15px tft-line-h-1-4">${escapeHtml(dept.description || 'Aucune description')}</p>
                        </div>
                        <div class="departement-infos">
                            <div class="manager-infos">
                                <div class="departement-info-icon">
                                    <div class="tft-icon-round-moyen tft-bg-black2 tft-bdr-greensav-1">
                                        <i class="fas fa-user-tie tft-clr-greensav"></i>
                                    </div>
                                </div>
                                <div class="manager-name">
                                    <h4 class="tft-title4">${escapeHtml(dept.managerName || 'Non assigné')}</h4>
                                    <p class="tft-sm-title1">Manager</p>
                                </div>
                            </div>
                            <div class="manager-infos">
                                <div class="departement-info-icon">
                                    <div class="tft-icon-round-moyen tft-bg-black2 tft-bdr-greensav-1">
                                        <i class="fas fa-map-marker-alt tft-clr-greensav"></i>
                                    </div>
                                </div>
                                <div class="manager-name">
                                    <h4 class="tft-title4">${escapeHtml(dept.address || 'Adresse non renseignée')}</h4>
                                    <p class="tft-sm-title1">Adresse</p>
                                </div>
                            </div>
                        </div>
                        <div class="departement-btns">
                            <button class="tft-btn edit-btn" data-id="${dept.id}" data-name="${escapeHtml(dept.name)}" data-desc="${escapeHtml(dept.description || '')}" data-addr="${escapeHtml(dept.address || '')}" data-mgr="${dept.managerId || ''}">Editer</button>
                            <button class="tft-btn delete-btn" data-id="${dept.id}">Supprimer</button>
                        </div>
                    `;
                    container.appendChild(card);
                });

                // Ajouter la carte "Ajouter"
                const addCard = document.createElement('div');
                addCard.className = 'departement';
                addCard.id = 'btn-ajouter-departement';
                addCard.innerHTML = `
                    <div class="tft-icon-round-grand tft-bg-greensav" onclick="openAddModal()">
                        <i class="fas fa-plus tft-clr-remain-white"></i>
                    </div>
                    <p class="tft-title2">Ajouter un département</p>
                `;
                container.appendChild(addCard);

                // Attacher les événements
                document.querySelectorAll('.edit-btn').forEach(btn => {
                    btn.addEventListener('click', () => {
                        openEditModal(btn.dataset.id, btn.dataset.name, btn.dataset.desc, btn.dataset.addr, btn.dataset.mgr);
                    });
                });

                document.querySelectorAll('.delete-btn').forEach(btn => {
                    btn.addEventListener('click', async () => {
                        if (confirm('Supprimer définitivement ce département ?')) {
                            try {
                                const res = await fetch(`/api/patron/departements/${btn.dataset.id}`, { method: 'DELETE' });
                                if (res.ok) await loadDepartements();
                                else alert('Erreur lors de la suppression');
                            } catch (err) {
                                console.error(err);
                                alert('Erreur réseau');
                            }
                        }
                    });
                });

            } catch (error) {
                console.error('Erreur:', error);
                const container = document.getElementById('container-departements');
                if (container) {
                    container.innerHTML = `
                        <div class="error">
                            <div class="tft-icon-carre-moyen tft-bg-red">
                                <i class="fas fa-exclamation-triangle tft-clr-remain-white"></i>
                            </div>
                            <p class="tft-title1">Impossible de charger les départements</p>
                            <p class="tft-sm-title1">Vérifiez que l'API est accessible</p>
                        </div>`;
                }
            }
        }

        // ============================================================
        // SÉCURITÉ : ÉVITER LES INJECTIONS XSS
        // ============================================================
        function escapeHtml(str) {
            if (!str) return '';
            return str.replace(/[&<>]/g, m => {
                if (m === '&') return '&amp;';
                if (m === '<') return '&lt;';
                if (m === '>') return '&gt;';
                return m;
            });
        }

        // ============================================================
        // CHARGER LES EMPLOYÉS POUR LE SELECT DES GÉRANTS
        // ============================================================
        async function loadManagersSelect() {
            try {
                const response = await fetch('/api/patron/employees/');
                if (!response.ok) throw new Error('Erreur chargement employés');
                const employees = await response.json();
                const select = document.getElementById('manager-id');
                if (!select) return;

                select.innerHTML = '<option value="">-- Sélectionner un gérant --</option>';
                employees.forEach(emp => {
                    const fullName = `${emp.firstName || ''} ${emp.lastName || ''}`.trim();
                    const option = document.createElement('option');
                    option.value = emp.id;
                    option.textContent = fullName || `Employé #${emp.id}`;
                    select.appendChild(option);
                });
            } catch (error) {
                console.error('Erreur chargement des employés:', error);
            }
        }

        // ============================================================
        // GESTION DU FORMULAIRE (AJOUT/MODIFICATION)
        // ============================================================
        document.getElementById('departementForm').addEventListener('submit', async (e) => {
            e.preventDefault();

            const id = document.getElementById('deptId').value;
            const payload = {
                name: document.getElementById('nom-departement').value.trim(),
                description: document.getElementById('description-departement').value.trim(),
                address: document.getElementById('address-departement').value.trim(),
                managerId: document.getElementById('manager-id').value ? parseInt(document.getElementById('manager-id').value) : null
            };

            const url = id ? `/api/patron/departements/${id}` : '/api/patron/departements/';
            const method = id ? 'PUT' : 'POST';

            try {
                const response = await fetch(url, {
                    method: method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    const err = await response.json();
                    alert(err.error || 'Erreur lors de l\'enregistrement');
                } else {
                    closeModalForm();
                    await loadDepartements();
                    alert(id ? 'Département modifié' : 'Département ajouté');
                }
            } catch (error) {
                console.error(error);
                alert('Erreur réseau');
            }
        });

        // ============================================================
        // AFFICHER LE NOM DE L'UTILISATEUR
        // ============================================================
        async function loadUserName() {
            try {
                const response = await fetch('/api/auth/me');
                if (response.ok) {
                    const user = await response.json();
                    const userNameElement = document.getElementById('userName');
                    if (userNameElement) {
                        userNameElement.textContent = user.userName || user.firstName || 'Utilisateur';
                    }
                    const userRoleElement = document.getElementById('userRole');
                    if (userRoleElement) {
                        userRoleElement.textContent = user.role || 'Patron';
                    }
                }
            } catch (error) {
                console.error('Erreur chargement utilisateur:', error);
            }
        }

        // ============================================================
        // INITIALISATION AU CHARGEMENT DE LA PAGE
        // ============================================================
        document.addEventListener('DOMContentLoaded', () => {
            loadDepartements();
            loadManagersSelect();
            loadUserName();
        });

    </script>
</body>
</html>