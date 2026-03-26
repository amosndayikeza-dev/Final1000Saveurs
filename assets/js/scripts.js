
    const deconnectionPopup=document.getElementById("deconnection-modal");
    const popupModal=document.querySelector(".tft-popup-modal");
    const containerLeft=document.getElementById("container-left");


    // afficher le popup de deconnexion
    function deconnectionModal(){
        deconnectionPopup.classList.add("tft-show");
    }

    // ferme tout modal ouvert
    function closeModal(){
        popupModal.classList.remove("tft-show");
    }

    // affiche le sidebar
    function showSidebar(){
        containerLeft.classList.add("tft-show");
    }