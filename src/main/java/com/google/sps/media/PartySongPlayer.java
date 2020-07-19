package com.google.sps.media;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimeZone;

import com.google.sps.data.YoutubeSongPlayInfo;

/**
 * Thread safe Object which can hold a party's list of songs, and move through them as time goes on
 * Player is stopped by default
 */
public class PartySongPlayer {
    private long currentSongStartGmtTimeMs;
    private long stoppedPlaybackTimeMs;
    private boolean isStopped = true;
    private Queue<YoutubeSong> playlist;

    public PartySongPlayer() {
        playlist = new LinkedList<>();
        stoppedPlaybackTimeMs = 0;
    }
    
    private long getCurrentGmtTimeMs() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
    }

    /**
     * Updates the player by skipping songs that have finished playing
     * If the player is stopped
     */
    private synchronized void updatePlayer() {
        if (isStopped) {
            // player is stopped no updates have occurred
            return;
        }
        while (!playlist.isEmpty()) {
            YoutubeSong currentSong = playlist.peek();
            if (getCurrentGmtTimeMs() - currentSongStartGmtTimeMs < currentSong.getSongDuration()) {
                // Currently in the middle of playing this song, no update needed
                return;
            } else {
                // Current Song finished playing, start playing the next song
                currentSongStartGmtTimeMs += currentSong.getSongDuration();
                playlist.remove();
            }
        }
        // Playlist is empty, it should be stopped
        isStopped = true;
        stoppedPlaybackTimeMs = 0;
    }

    /**
     * Starts the player at the time in the song it was last stopped
     * If there is no song to play this is a no-op
     */
    public synchronized void startPlayer() {
        if (!isStopped || getCurrentSong() == null) {
            return;
        }
        seek(stoppedPlaybackTimeMs);
        isStopped = false;
    }

    /**
     * Updates the player to start playing the current song,
     * Saves the current time in the once playing song
     * Stops the player
     */
    public synchronized void stopPlayer() {
        updatePlayer();
        if (isStopped) {
            return;
        }
        stoppedPlaybackTimeMs = getCurrentGmtTimeMs() - currentSongStartGmtTimeMs;
        isStopped = true;
    }

    /**
     * Updates the player to then find the current song
     * If there is no current song this returns null
     */
    public synchronized YoutubeSong getCurrentSong() {
        updatePlayer();
        return playlist.peek();
    }

    /**
     * Returns information about the current song that's playing
     * If there is no current song this returns null
     */
    public synchronized YoutubeSongPlayInfo getCurrentSongInformation() {
        YoutubeSong currentSong = getCurrentSong();
        if(currentSong == null) {
            return null;
        } else {
            return new YoutubeSongPlayInfo(currentSong, currentSongStartGmtTimeMs, isStopped);
        }
    }

    /**
     * Returns a copy of the current playlist of songs
     * The copy is sent so that changes to the playlist don't also effect this object
     */
    public synchronized final List<YoutubeSong> getCurrentPlaylist() {
        return new ArrayList<>(playlist);
    }

    /**
     * Plays the next song in the playlist
     * If there is no song currently playing this is a no-op
     * If there is no next song or the player is stopped, this only skips the current song
     */
    public synchronized void playNextSong() {
        updatePlayer();
        if (!playlist.isEmpty()){
            if (isStopped) {
                stoppedPlaybackTimeMs = 0;
            } else {
                currentSongStartGmtTimeMs = getCurrentGmtTimeMs();
            }
            playlist.remove();
        }
    }

    /**
     * adds a song to the end of the playlist
     */
    public synchronized void addSong(YoutubeSong song) {
        playlist.add(song);
    }

    /**
     * Seek to a time in the current song.
     * If there is no current song this method is a no-op
     * If the seek time is negative, this method is a no-op
     * If the seek time is greater than the song duration this method will play the next song
     * @param timeMsInSong the time to seek to
     */
    public synchronized void seek(long timeMsInSong) {
        updatePlayer();
        if (timeMsInSong < 0) {
            return;
        }
        YoutubeSong currentSong = getCurrentSong();
        if (currentSong == null) {
            return;
        } else if (timeMsInSong > currentSong.getSongDuration()) {
            // Seeking to the next song
            playNextSong();
        } else {
            currentSongStartGmtTimeMs = getCurrentGmtTimeMs() - timeMsInSong;
        }
    }

}
