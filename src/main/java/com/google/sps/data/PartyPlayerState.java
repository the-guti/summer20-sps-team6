package com.google.sps.data;

import java.util.List;

import com.google.sps.media.YoutubeSong;
import lombok.AllArgsConstructor;

/**
 * Contains all information a client would want to know about a given Party's playlist state
 * Intended to be serialized into JSON by Gson, and used in the script.js file
 * The #1 use of this is for the PartyMusicPlayerServlet to be able to communicate a Player's state to a HTML requester
 */
@AllArgsConstructor
public class PartyPlayerState {
    private final YoutubeSongPlayInfo currentSongPlayInfo;
    private final List<YoutubeSong> currentPlaylist;
}
