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

var startTime = 0;
function getStartTime() {
    fetch('/musicControler')
    .then(response => response.text())
    .then((time) => {
        startTime = parseInt(time);
    })
    ;
}
currentVideoId = 0;     // index

var videoIds = [
    'dQw4w9WgXcQ',      // Song id: Never gonna give you up
    'a01QQZyl-_I',      // Song id: Queen - Under Pressure
    'zO6D_BAuYCI',      // Song id  Queen - Crazy Little Thing Called Love 
    'HgzGwKwLmgM',      // Song id: Queen - Don't Stop Me Now
    'azdwsXLmrHE',      // Song id: Queen - Radio Ga Ga
    'f4Mc-NYPHaQ',      // Song id: Queen - I Want To Break Free
    'KXw8CRapg7k'       // Song id: Queen - We Are The Champions
];

function onPlayerReady(event) {  
    event.target.loadVideoById(videoIds[currentVideoId], 200);
}

function onPlayerStateChange(event){
    console.log(getPlayerState());
    // Check if playing video ended
    /*if(getPlayerState() === 0){ 
        currentVideoId++;
        // Check if there are more videos in the queue
        if(currentVideoId < videoIds.length){
            player.loadVideoById(videoIds[currentVideoId]);
        }
    }*/
}

function getPartyId(){
    // Get party id 5910974510923776
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    return urlParams.get('id');;
}

function startSyncManager(){
    const partyId = getPartyId();
    setInterval( function() { syncManager(partyId); }, 1000 );
}

async function syncManager(partyId){
    // Call the Servlet
    const response = await fetch("/musicPlayer?party-id=" + partyId);
    
    switch (response.status) {
        case 200:
            // Get info, youtube song play info ( Youtube song (S songName /S videoId /L songDuration) / L songStartGmtTimeMs  / Bool stopped)
            const currentYoutubeSongPlayInfo = await response.json();
            
            if(currentYoutubeSongPlayInfo.stopped){
                // Play?
            }else{

            }
            
            // Do action accordingly, call local player functions
            //console.log(currentYoutubeSongPlayInfo);    
            break;
        case 400:
            // Error message - If no room-id is given or a non-numeric room-id is given, return a 400 response code
            break;
        case 204:
            // Error message  no songs
            break;
        default:
            // Error
            break;
    }
}

function updateYoutubePlayer(currentSongPlayInfo){
    const videoId = currentSongPlayInfo.song.videoId;
    console.log("update check videoId", currentSongPlayInfo.song.videoId);
    // Check if same video
    if(player.getVideoData()['video_id'] != videoId){
        // Check time
        /*if(false){
            seekTo(startTime);
        }*/
        player.loadVideoById(videoId);
        console.log("load video");
    }
}

async function addSong(){
    const videoId = document.getElementById("song_id").value;
    const songName = document.getElementById("song_name").value;
    const songDuration = document.getElementById("song_duration").value;

    var partyId = getPartyId();
    const action = "ADD_SONG";

    const youTubeSong = new Object();
    youTubeSong.songName = songName;
    youTubeSong.videoId = videoId;
    youTubeSong.songDuration = songDuration;

    videoId.innerHTML = "";
    songName.innerHTML = "";
    songDuration.innerHTML = "";

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
        .catch(function(res){ console.log(res) })
}

// START_PLAYER , STOP_PLAYER , SKIP_SONG
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

var state = 'stop';

function buttonPlayPress() {
    if(state==='stop'){
      state='play';
      var button = d3.select("#button_play").classed('btn-success', true); 
      button.select("i").attr('class', "fa fa-pause");  
    }
    else if(state==='play' || state==='resume'){
      state = 'pause';
      d3.select("#button_play i").attr('class', "fa fa-play"); 
    }
    else if(state==='pause'){
      state = 'resume';
      d3.select("#button_play i").attr('class', "fa fa-pause");        
    }
    console.log("button play pressed, play was "+state);
}

// startSyncManager();
