package com.google.sps.data;

import com.google.sps.media.YoutubeSong;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Contains all information needed to know how to play a song
 * This class is intended to be serialized into JSON by Gson and used to play songs in the script.js file
 */
@AllArgsConstructor
@Getter
@Setter
public class YoutubeSongPlayInfo {
    private YoutubeSong song;
    private long songStartGmtTimeMs;
    private boolean stopped;
}
