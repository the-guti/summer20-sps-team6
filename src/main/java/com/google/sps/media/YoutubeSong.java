package com.google.sps.media;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public class YoutubeSong {
    private final String songName;
    private final String videoId;
    private final long songDuration;
}
