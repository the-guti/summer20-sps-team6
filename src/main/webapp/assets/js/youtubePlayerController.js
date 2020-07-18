var tag = document.createElement('script');
tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
/*
        songQueue.add("HgzGwKwLmgM");
        songQueue.add("a01QQZyl-_I");
        songQueue.add("azdwsXLmrHE");
        songQueue.add("f4Mc-NYPHaQ");
        songQueue.add("KXw8CRapg7k");
*/
var player;
function onYouTubeIframeAPIReady() {
    player = new YT.Player('player', {
        height: '390',
        width: '640',
        videoId: 'dQw4w9WgXcQ',
        playerVars: {
            'autoplay': 1, 
            'controls':0,
            'disablekb': 1,
        },
        events: {
            'onReady': onPlayerReady,
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
function onPlayerReady(event) {           
    event.target.playVideo();
    event.target.seekTo(startTime, true);

    player.loadVideoById("HgzGwKwLmgM", startTime);
}
