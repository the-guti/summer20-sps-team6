package com.google.sps.data;

import com.google.sps.media.YoutubeSong;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class YoutubeSongPlayInfo {
    private YoutubeSong song;
    private long songStartGmtTimeMs;
    private boolean stopped;
}
