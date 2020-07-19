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
    event.target.loadVideoById(videoIds[currentVideoId], 210);
    /*         
    event.target.seekTo(startTime, true);
    event.target.playVideo();
    */
}

function onPlayerStateChange(event){
    console.log(getPlayerState());
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
/*
    YoutubeSong song;
    long songStartGmtTimeMs;
    boolean stopped;
*/

async function syncManager(){
    // Call the Servlet

    // Get info, youtube song play info

    // Do action accordingly, call local player functions
    
    // Sleep for 1000
    await sleep(1000); // avoid showing content until content exists

    // Call itself again to loop

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

function seekTo(startTime){
    player.seekTo(startTime,true);
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
