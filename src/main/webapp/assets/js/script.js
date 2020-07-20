function createParty() {
    const partyName = document.getElementById("partyName").value;
    if (partyName.length == 0) return;

    fetch('/create?name=' + partyName)
        .then(response => response.json())
        .then(data => {
            alert("New party created: " + data.partyName + "\nParty id: " + data.id)
            window.location.href = '/party.html?id=' + data.id;
        });
}

function getComments(id) {
    const commentsContainer = document.getElementById('comments-container');
    window.setInterval(function () {
        url = '/comment?id=' + id
        fetch(url)
            .then(response => response.json())
            .then(comments => {
                commentsContainer.innerHTML = "";
                commentsContainer.innerHTML = "";
                for (let i = 0; i < comments.length; i++) {
                    const comment = document.createElement("P");
                    comment.innerHTML = "<b>" + comments[i].name + "</b><br/>" + comments[i].text;
                    comment.classList.add("comment");
                    commentsContainer.appendChild(comment);
                }
            });
    }, 2000);
}

function getPlaylist(id) { // can easily be extended to update the song using the youtubePlayerController.js methods see below comment
    // change the name to be update player if doing so
    const playlistContainer = document.getElementById('playlist-container');
    window.setInterval(async function () {
        url = '/musicPlayer?party-id=' + id;
        const response = await fetch(url);
        const partyPlaylistState = await response.json();
        // call some "updateYoutubePlayer(partyPlaylistState.currentSongPlayInfo)"
        // partyPlaylistState.currentSongPlayInfo is a YoutubeSongPlayInfo object, see com.google.sps.data.YoutubeSongPlayInfo java package
        playlistContainer.innerHTML = "";
        const currentPlaylist = partyPlaylistState.currentPlaylist;
        for (let i = 0; i < currentPlaylist.length; i++) {
            const song = document.createElement("P");
            song.innerHTML = "<b>" + currentPlaylist[i].songName + "</b><br/>Duration: "
                + formatMsDurationAsMinutesAndSeconds(currentPlaylist[i].songDuration);
            song.classList.add("comment");
            playlistContainer.appendChild(song);
        }
        playlistContainer.scrollTop = 9999999
    }, 2000);
}

function formatMsDurationAsMinutesAndSeconds(ms) {
    let minutes = Math.floor(ms/60/1000).toString();
    let seconds = (Math.floor(ms/1000) % 60).toString();
    if (seconds.length === 1) {
        seconds = "0" + seconds;
    }
    return minutes + ":" + seconds;
}

function goToLatestComments(){
    const commentsContainer = document.getElementById('comments-container');
    commentsContainer.scrollTop = 9999999
}

function joinParty() {
    const partyId = document.getElementById("partyId").value;
    if (partyId.length == 0) return;
    window.location.href = '/party.html?id=' + partyId;
}

function loadParty() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const partyId = urlParams.get('id');
    fetch('/party?id=' + partyId)
        .then(response => response.json())
        .then(data => {
            document.getElementById("partyName").innerHTML = data.partyName;
            getComments(data.id);
            getPlaylist(data.id);
        });
}

/**
 * Shows all HTML elements of the given Class
 */
function showClass(className) {
    const classElements = document.getElementsByClassName(className);
    for (let i = 0; i < classElements["length"]; i++) {
        classElements[i].style.display = "block";
    }
}

function sendComment() {
    const username = document.getElementById("user_name").value
    const comment = document.getElementById("user_comment").value
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const partyId = urlParams.get('id');
    if (username.trim() !== "" && username.trim() !== "") {
        url = `/comment?party-id=${partyId}&username=${username}&comment=${comment}`
        fetch(encodeURI(url),
            {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                method: "POST",
                body: "[]"
            })
            .then(function () {
                document.getElementById("user_comment").value = "";
            })
    } else {
        console.log("nothing to send")
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
    if (response.ok) {
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
    if (getCurrentUser.cachedUser === undefined) {
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
    if (await isUserLoggedIn()) {
        showClass("show-logged-in");
    } else {
        showClass("show-logged-out");
    }
}

/**
 * Requests the current time in a song that's playing in the given roomId
 * Note JavaScript UTC time is the same as GMT time
 */
async function getMsTimeInCurrentSong(partyId) {
    const response = await fetch("/musicPlayer?party-id=" + partyId);
    const currentYoutubeSongPlayInfo = await response.json();
    return Date.now() - currentYoutubeSongPlayInfo.currentSongPlayInfo.songStartGmtTimeMs;
}
