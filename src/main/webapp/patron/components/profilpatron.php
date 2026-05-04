<div class="containerRight tft-p-relative" id="container-right">
    <div class="tft-close-icon tft-p-absolute tft-top-15 tft-right-15 tft-hover-red" onclick="closeModalInfos()">
        <i class="fas fa-times tft-clr-white3"></i>
    </div>
    <div class="admin-profil">
        <div class="tft-avatar-profil-moyen2 tft-bdr-greensav-2">
            <img src="../assets/images/user/arashmil.jpg">
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