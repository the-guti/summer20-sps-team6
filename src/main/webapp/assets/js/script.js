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

function joinParty(){
    const partyId = document.getElementById("partyId").value;
    if(partyId.length == 0) return;
    window.location.href = '/party.html?id=' + partyId;
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

/**
 * Shows all HTML elements of the given Class
 */
function showClass(className) {
    const classElements = document.getElementsByClassName(className);
    for(let i = 0; i < classElements["length"]; i++){
        classElements[i].style.display = "block";
    }
}

/**
 * function that returns after the set number of milliseconds
 */
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * Defines getCurrentUser.cachedUser by fetching the current user from UserInformationServlet.
 * If the user is not logged in, the fetch will have a 400 response code and the cachedUser will be defined as null
 * Otherwise the cachedUser will be defined as the current User object
 */
async function defineCurrentUserCache() {
    const response = await fetch('/userInfo');
    if(response.ok){
        getCurrentUser.cachedUser = await response.json();
    } else {
        getCurrentUser.cachedUser = null;
    }
}

/**
 * Returns the Current Logged in user object
 * If no user is logged in, return null
 */
async function getCurrentUser() {
    if(getCurrentUser.cachedUser === undefined){
        await defineCurrentUserCache();
    }
    return getCurrentUser.cachedUser;
}

async function isUserLoggedIn() {
    const currentUser = await getCurrentUser();
    return currentUser != null;
}

/**
 * If the current user is logged in, unhide the show-logged-in class
 * If the current user is not logged in, unhide the show-logged-out class
 */
// TODO find better way to wait for content to exist
async function showLoginBasedContent() {
    await sleep(100); // avoid showing content until content exists
    if(await isUserLoggedIn()) {
        showClass("show-logged-in");
    } else {
        showClass("show-logged-out");
    }
}
