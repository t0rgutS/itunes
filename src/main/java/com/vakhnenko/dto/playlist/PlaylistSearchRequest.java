package com.vakhnenko.dto.playlist;

import com.vakhnenko.dto.SearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaylistSearchRequest extends SearchRequest {
    private String authorName;

    private String playlistName;

    private String performerName;

    private String songName;

    private String albumName;
}
