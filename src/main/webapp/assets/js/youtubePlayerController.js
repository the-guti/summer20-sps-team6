// Initialize player
var tag = document.createElement('script');
tag.src = "https://www.youtube.com/iframe_api";

var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

var player;
function onYouTubeIframeAPIReady() {
    player = new YT.Player('player', {
        height: '360',
        width: '640',
        playerVars: {
            'autoplay': 1, 
            'controls':0,           // Hides control bar
            'disablekb': 1,         // Disables kb controls
            'fs':0,                 // Hide fullscreen button
            'iv_load_policy': 3,    // Hide annotations
            'modestbranding': 1,    // Hide youtube branding
            'rel': 0,               // Do not show related videos
            'showinfo': 0,          // Hide current song information
        }, 
        events: {
            'onReady': onPlayerReady,               // First start
            'onStateChange': onPlayerStateChange    // Actual looping
        }
    });
}

function onPlayerReady(event) {  
    actionPlayerServlet("START_PLAYER");
}

// state: -1 = unstarted, 0 = ended, 1 = playing video, 2 = paused,3 = buffering, 5 = video cued
function onPlayerStateChange(event){
    const playerState = getPlayerState();
    console.log(playerState);
    if(playerState === 2){
        actionPlayerServlet("STOP_PLAYER");
    }else if(playerState === 1 ){
        actionPlayerServlet("START_PLAYER");
    }
}

function getPartyId(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    return urlParams.get('id');;
}

// Function that manages communication with sync server
function syncManager(partyId){
    // Obtain playlist container to put songs
    const playlistContainer = document.getElementById('playlist-container');
    const url = '/musicPlayer?party-id=' + partyId;
    
    // Repeat evewrything inside every second
    window.setInterval(async function () {
        // Call Servlet
        const response = await fetch(url);
        console.log(response);

        switch (response.status) {
            // All good
            case 200: 
                // Get Party playlist state (currentSongPlayInfo, currentPlaylist;)
                const partyPlaylistState = await response.json();

                // Update playlist
                getPlaylist(playlistContainer, partyPlaylistState.currentPlaylist);

                // Extract YoutubeSongPlayInfo( YoutubeSong song  / L songStartGmtTimeMs  / Bool stopped)
                const currentYTSongPlayInfo = partyPlaylistState.currentSongPlayInfo;

                songSync(currentYTSongPlayInfo);
                
                document.getElementById("errorMessage").innerHTML = "";

                break;
            case 400:
                // Error message - If no room-id is given or a non-numeric room-id is given, return a 400 response code
                document.getElementById("errorMessage").innerHTML = "Invalid party";
                break;
            case 204:
                // Error message  no songs
                document.getElementById("errorMessage").innerHTML = "No songs in queue";
                break;
            default:
                // Error
                document.getElementById("errorMessage").innerHTML = "Lost communication with server, maybe its the aliens";
                break;
        }
    }, 1000);
}

function songSync(currentSongPlayInfo){
    const videoId = currentSongPlayInfo.song.videoId;
    const startTime = currentSongPlayInfo.songStartGmtTimeMs;
    const currentTime = Date.now();

    // Local player stopped
    if(getPlayerState() === 2){
        // Server player still going
        if(!currentSongPlayInfo.stopped){
            playVideo();
        }
        return;
    }else if(getPlayerState() === 1 && currentSongPlayInfo.stopped){// Local palyer is palying but server player is stopped
        pauseVideo();
    }
        
    if(startTime === 0){
        // Check if same video
        if(player.getVideoData()['video_id'] != videoId){
             player.loadVideoById(videoId);
        }
        return;
    }else{
        correctTime = (currentTime - startTime)/1000;
        timeDif = Math.abs(getTimeElapsed() - correctTime);
        if(player.getVideoData()['video_id'] != videoId){
            player.loadVideoById(videoId, correctTime);
        }else if(timeDif > 1){// Right video playing, wrong time by more than a second
            seekTo(correctTime);
        }
    }
}

function getPlaylist(playlistContainer, playlist) { 
    playlistContainer.innerHTML = "";
    const currentPlaylist = playlist;
    for (let i = 0; i < currentPlaylist.length; i++) {
        const song = document.createElement("P");
        song.innerHTML = "<b>" + currentPlaylist[i].songName + "</b><br/>Duration: "
            + formatMsDurationAsMinutesAndSeconds(currentPlaylist[i].songDuration);
        song.classList.add("comment");
        playlistContainer.appendChild(song);
    }
    playlistContainer.scrollTop = 9999999
}

// Helper function
function formatMsDurationAsMinutesAndSeconds(ms) {
    let minutes = Math.floor(ms/60/1000).toString();
    let seconds = (Math.floor(ms/1000) % 60).toString();
    if (seconds.length === 1) {
        seconds = "0" + seconds;
    }
    return minutes + ":" + seconds;
}

async function isServerPlayerStopped() {
    const partyId = getPartyId();
    const url = '/musicPlayer?party-id=' + partyId;
    const response = await fetch(url);
    if (response.status !== 200) {
        return true;
    }
    const playerState = await response.json();
    return playerState.currentSongPlayInfo.stopped;
}

// POST - ADD Song
async function addSong(){
    const videoId = document.getElementById("song_id").value;
    const songName = document.getElementById("song_name").value;
    const songDurationMilliseconds = document.getElementById("song_duration").value * 1000;

    var partyId = getPartyId();
    const action = "ADD_SONG";

    const youTubeSong = new Object();
    youTubeSong.songName = songName;
    youTubeSong.videoId = videoId;
    youTubeSong.songDuration = songDurationMilliseconds;

    videoId.innerHTML = "";
    songName.innerHTML = "";
    songDurationMilliseconds.innerHTML = "";
    url = `/musicPlayer?party-id=${partyId}&youtube-song-json=${JSON.stringify(youTubeSong)}&action=${action}`
        await fetch(encodeURI(url),
        {
            headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            },
            method: "POST",
            body: "[]"  
        })
        .then(function(res){ console.log(res) })
        .catch(function(res){ console.log(res) });
    if (await isServerPlayerStopped()) {
        await actionPlayerServlet("START_PLAYER");
    }
}

// POST -  START_PLAYER , STOP_PLAYER , SKIP_SONG
async function actionPlayerServlet(action){
    var partyId = getPartyId();
    
    url = `/musicPlayer?party-id=${partyId}&action=${action}`
        await fetch(encodeURI(url),
        {
            headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            },
            method: "POST",
            body: "[]"  
        })
        .then(function(res){ console.log(res) })
        .catch(function(res){ console.log(res) })
}

function changeVolume(){
    var volume = document.getElementById("volume").value;
    player.setVolume(volume);
}

// Returns the elapsed time in seconds since the video started playing.
function getTimeElapsed(){
    return player.getCurrentTime();
}

// Returns the duration in seconds of the currently playing video. 
function getDuration(){
    return player.getDuration();
}

// state: -1 = unstarted, 0 = ended, 1 = playing video, 2 = paused,3 = buffering, 5 = video cued
function getPlayerState(){
    return player.getPlayerState();
}

function pauseVideo(){
    player.pauseVideo();    
}

function playVideo(){
    player.playVideo();
}

// Uses seconds
function seekTo(startTimeSec){
    player.seekTo(startTimeSec,true);
}

// Stop the song first in the servlet, then check servelet status and stopin the player
function stopVideo(){
    player.stopVideo();
}
