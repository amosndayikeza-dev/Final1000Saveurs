//Recuperation des elements canvas et de leurs context 2d
const baton = document.getElementById("revenusDepenses").getContext('2d');
const ligne = document.getElementById("dettesPayes").getContext('2d');

//===================== diagramme en batons (revenus et depenses) ========================//
new Chart(baton,{
    type : 'bar',
    data : {
        labels : ['Jan','Fev','Mars','Avril','Mai','Juin','Juillet','Aout','Sept','Oct','Nov','Dec'],
        datasets : [{
            label : '💰 Revenus',
            data : [10000,14000,19000,23000,29000,31000,33000,35000,38000,40000,41000,44000],
            backgroundColor : 'rgb(0, 106, 98)',
            borderColor : 'rgb(0, 106, 98)',
            borderWidth : 1
        },{
            label : '💸 Depenses',
            data : [8000,17000,12000,10000,21000,27000,30000,32000,38000,40000,40000,42000],
            backgroundColor : 'rgb(253, 140, 92)',
            borderColor : 'rgb(253, 140, 92)',
            borderWidth : 1
        }]
    },
    options : {
        responsive : true,
        scales : {
            y : {
                beginAtZero : true,
                title : {
                    display : true,
                    text : 'Montant en Fbu'
                }
            }
        },
        plugins : {
            tooltip : {
                backgroundColor : 'rgb(253, 140, 92)',
                callbacks : {
                    label : function(RevDep){
                        return RevDep.dataset.label + " : " + RevDep.raw + " " + 'Fbu';
                    }
                }
            }
        }
    }
});
//============================= fin =================================//

//===================== diagramme avec lignes (remboursements et dettes) ========================//
new Chart(ligne,{
    type : 'line',
    data : {
        labels : ['Jan','Fev','Mars','Avril','Mai','Juin','Juillet','Aout','Sept','Oct','Nov','Dec'],
        datasets : [{
            label : '💰 Paiement',
            data : [10000,14000,19000,23000,29000,31000,33000,35000,38000,40000,41000,44000],
            backgroundColor : 'rgb(0, 106, 98)',
            borderColor : 'rgb(0, 106, 98)',
            borderWidth : 1
        },{
            label : '💸 Dettes',
            data : [8000,17000,12000,10000,21000,27000,30000,32000,38000,40000,40000,42000],
            backgroundColor : 'rgb(253, 140, 92)',
            borderColor : 'rgb(253, 140, 92)',
            borderWidth : 1
        }]
    },
    options : {
        responsive : true,
        scales : {
            y : {
                beginAtZero : true,
                title : {
                    display : true,
                    text : 'Montant en Fbu'
                }
            }
        },
        plugins : {
            tooltip : {
                backgroundColor : 'rgb(0, 106, 98)',
                callbacks : {
                    label : function(RemDet){
                        return RemDet.dataset.label + " : " + RemDet.raw + " " + 'Fbu';
                    }
                }
            }
        }
    }
});
