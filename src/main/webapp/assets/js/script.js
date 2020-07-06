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

function getComments(id){
    const commentsContainer = document.getElementById('comments-container');
    fetch('/comment?id='+id)
            .then(response => response.json())
            .then(comments => {
                commentsContainer.innerHTML = "";
                commentsContainer.innerHTML = "";
                for (let i = 0; i < comments.length; i++){
                    const comment = document.createElement("P");
                    comment.innerHTML = "Name: " + comments[i].name + "<br/>Comment: " + comments[i].text;
                    comment.classList.add("comment");  
                    commentsContainer.appendChild(comment);
                }
            });
}

function loadParty(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const partyId = urlParams.get('id');
    fetch('/party?id='+partyId)
        .then(response => response.json())
        .then(data => {
            document.getElementById("partyName").innerHTML = data.partyName;
            document.getElementById("party-id").value = data.id;
            getComments(data.id);
        });
}