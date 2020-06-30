package com.vakhnenko.dto.song;

import com.vakhnenko.dto.SearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SongSearchRequest extends SearchRequest {
    private String songName;

    private String albumName;

    private String songLength;

    private String performerName;

    private String playlistName;

    private String playlistAuthor;

    private String genre;

    private Long performerId;

    private Long albumId;

    private Long playlistId;
}
