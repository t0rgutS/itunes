package com.vakhnenko.dto.performer;

import com.vakhnenko.dto.SearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PerformerSearchRequest extends SearchRequest {
    private String performerName;

    private String songName;

    private String albumName;

    private String genre;

    //private String playlist_name; <--- ?
}
