
    const deconnectionPopup=document.getElementById("deconnection-modal");
    const popupModals=document.querySelectorAll(".tft-popup-modal");
    const containerLeft=document.getElementById("container-left");
    const nomDepartement=document.getElementById("nom-departement");
    const descriptionDepartement=document.getElementById("description-departement");
    const dateCreationDepartement=document.getElementById("date-creation-departement");
    const photoEmployeDepartement=document.getElementById("photo-employe-departement");
    const nomEmployeDepartement=document.getElementById("nom-employe-departement");
    const posteEmployeDepartement=document.getElementById("poste-employe-departement");
    const formAjoutDepartement=document.getElementById("ajout-departement");
    const containerDepartements=document.getElementById("container-departements");
    const addDepartement=document.getElementById("add-departement");


    // afficher le popup de deconnexion
    function deconnectionModal(){
        deconnectionPopup.classList.add("tft-show");
    }

    // ferme tout modal ouvert
    function closeModal(){
        popupModals.forEach(popupModal =>{
            popupModal.classList.remove("tft-show");
        })
    }

    // affiche le sidebar
    function showSidebar(){
        containerLeft.classList.add("tft-show");
    }

    // affiche le popup d'ajout de departement
    function ajouterDepartement(){
        addDepartement.classList.add("tft-show");
    }

    formAjoutDepartement.addEventListener("submit" ,(a)=>{
        a.preventDefault();
        console.log("le nom du departement est : " + nomDepartement.value);
        console.log("la description du departement est : " + descriptionDepartement.value);
        console.log("la date de creation du departement est : " + dateCreationDepartement.value);
        console.log("le nom de l'employe du departement est : " + nomEmployeDepartement.value);
        console.log("le poste de l'employe du departement est : " + posteEmployeDepartement.value);
        let f=photoEmployeDepartement.files[0];
        console.log(f);
        if(f){
            const reader=new FileReader();
            reader.onload=function(){
                const urlImage=reader.result;
                const departementContainer=document.createElement("div");
                departementContainer.setAttribute("class","departement");
                departementContainer.innerHTML=`
                <p class="tft-sm-title2 tft-bg-black2" id="creation-date">${dateCreationDepartement.value}</p>
                    <div class="departement-details">
                        <h3 class="tft-title2 tft-clr-orangesav tft-a-self-center">${nomDepartement.value}</h3>
                        <p class="tft-sm-title1 tft-text-justify">${descriptionDepartement.value}</p>
                    </div>
                    <div class="departement-workers">
                        <div class="simple-workers">
                            <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                                <img src="../../assets/images/user/1.png.jpg" alt="">
                            </div>
                            <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                                <img src="../../assets/images/user/user7.jpg" alt="">
                            </div>
                            <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                                <img src="../../assets/images/user/mlane.jpg" alt="">
                            </div>
                        </div>
                        <div class="departement-manager">
                            <div class="tft-avatar-profil-moyen tft-bdr-white2-2 tft-cursorpointer">
                                <img src="${urlImage}" alt="">
                            </div>
                        </div>
                        <div class="simple-workers">
                            <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                                <img src="../../assets/images/user/arashmil.jpg" alt="">
                            </div>
                            <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                                <img src="../../assets/images/user/sauro.jpg" alt="">
                            </div>
                            <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                                <img src="../../assets/images/user/jm_denis.jpg" alt="">
                            </div>
                        </div>
                    </div>
                    <div class="departement-btns">
                        <a class="tft-btn" href="#">
                            Editer
                        </a>
                        <button class="tft-btn">Supprimer</button>
                    </div> `;
                document.containerDepartements.appendChild("departementContainer");
                const employesInForm=document.createElement("div");
                employesInForm.setAttribute("class","single-employe");
                employesInForm.innerHTML=`
                <div class="tft-avatar-profil-petit">
                    <img src="${urlImage}" alt="">
                </div>
                <div class="employe-nom-poste">
                    <h4 class="tft-title4">${nomEmployeDepartement.value}</h4>
                    <p class="tft-sm-title1">${posteEmployeDepartement.value}</p>
                </div>`;
            };
            reader.readAsDataURL(f);
        }
        // faut que
        const departementContainer=document.createElement("div");
        departementContainer.setAttribute("class","departement");
        departementContainer.innerHTML=`
        <p class="tft-sm-title2 tft-bg-black2" id="creation-date">${dateCreationDepartement.value}</p>
            <div class="departement-details">
                <h3 class="tft-title2 tft-clr-orangesav tft-a-self-center">${nomDepartement.value}</h3>
                <p class="tft-sm-title1 tft-text-justify">${descriptionDepartement.value}</p>
            </div>
            <div class="departement-workers">
                <div class="simple-workers">
                    <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                        <img src="../../assets/images/user/1.png.jpg" alt="">
                    </div>
                    <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                        <img src="../../assets/images/user/user7.jpg" alt="">
                    </div>
                    <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                        <img src="../../assets/images/user/mlane.jpg" alt="">
                    </div>
                </div>
                <div class="departement-manager">
                    <div class="tft-avatar-profil-moyen tft-bdr-white2-2 tft-cursorpointer">
                        <img src="${urlImage}" alt="">
                    </div>
                </div>
                <div class="simple-workers">
                    <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                        <img src="../../assets/images/user/arashmil.jpg" alt="">
                    </div>
                    <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                        <img src="../../assets/images/user/sauro.jpg" alt="">
                    </div>
                    <div class="tft-avatar-profil-petit tft-bdr-white2-2 tft-cursorpointer">
                        <img src="../../assets/images/user/jm_denis.jpg" alt="">
                    </div>
                </div>
            </div>
            <div class="departement-btns">
                <a class="tft-btn" href="#">
                    Editer
                </a>
                <button class="tft-btn">Supprimer</button>
            </div> `;
        document.containerDepartements.appendChild("departementContainer");
        const employesInForm=document.createElement("div");
        employesInForm.setAttribute("class","single-employe");
        employesInForm.innerHTML=`
        <div class="tft-avatar-profil-petit">
            <img src="${urlImage}" alt="">
        </div>
        <div class="employe-nom-poste">
            <h4 class="tft-title4">${nomEmployeDepartement.value}</h4>
            <p class="tft-sm-title1">${posteEmployeDepartement.value}</p>
        </div>`;

    })
    photoEmployeDepartement.addEventListener("change" ,()=>{
        if(this.files[0]){
            const reader=new FileReader();
            reader.onload=function(){
                const url=reader.result;
                document.getElementById("avatarProfil").src=url;
            }
            reader.readAsDataURL(this.files[0]);
        }
    })