<div class="containerLeft" id="container-left">
    <div class="tft-icon-round-petit tft-p-absolute tft-top-5 tft-right-10 tft-bg-black3" onclick="hideSidebar()" id="icon-sidebar">
        <i class="fas fa-arrow-right"></i>
    </div>
    <div class="c-left-header">
        <div class="tft-logo-avatar tft-bdr-greensav-1">
            <img src="../assets/images/icons/abonnes.png" alt="">
        </div>
        <p class="app-name">1000<span>Saveurs</span></p>
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
            <a href="patron/rapports/ventes.html">
                <div class="c-left-option">
                    <div class="tft-icon-round">
                        <i class="fas fa-chart-bar"></i>
                    </div>
                    <p class="option-name">Rapports</p>
                </div>
            </a>
            <a href="/patron/employes.html">
                <div class="c-left-option">
                    <div class="tft-icon-round">
                        <i class="fas fa-users"></i>
                    </div>
                    <p class="option-name">Employés</p>
                </div>
            </a>
            <div class="c-left-option" onclick="showContainerRightNotification()">
                <div class="tft-icon-round">
                    <i class="fas fa-bell"></i>
                </div>
                <p class="option-name">Notifications</p>
            </div>
            <div class="c-left-option" onclick="showContainerRightParametre()">
                <div class="tft-icon-round">
                    <i class="fas fa-cog"></i>
                </div>
                <p class="option-name">Parametres</p>
            </div>
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