function createParty(){
    const partyName = document.getElementById("partyName").value;
    if(partyName.length == 0) return;

    fetch('/create?name='+partyName)
        .then(response => response.json())
        .then(data => {
            alert("New party created: " + data.partyName+ "\nParty id: " + data.id)
            window.location.href = '/party.html?id=' + data.id;
        });
}


function loadParty(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const partyId = urlParams.get('id');
    fetch('/party?id='+partyId)
        .then(response => response.json())
        .then(data => {
            console.log(data)
            document.getElementById("partyName").innerHTML = data.partyName;
            document.getElementById("party-id").value = data.partyName;
            getComments(data.id);
        });
}