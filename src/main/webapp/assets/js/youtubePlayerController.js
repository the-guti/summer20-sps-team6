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
currentVideoId = 0;

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
    event.target.loadVideoById(videoIds[currentVideoId], 210);
    /*         
    event.target.seekTo(startTime, true);
    event.target.playVideo();
    */
}

function onPlayerStateChange(event){
    // Check if playing video ended
    if(getPlayerState() === 0){ 
        console.log("ENDED");
        currentVideoId++;
        // Check if there are more videos in the queue
        if(currentVideoId < videoIds.length){
            player.loadVideoById(videoIds[currentVideoId]);
        }
    }
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

function stopVideo(){
    player.stopVideo();
}
