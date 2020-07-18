package com.google.sps.media;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class YoutubeSong {
    private String songName;
    private String videoId;
    private long songDuration;
}
