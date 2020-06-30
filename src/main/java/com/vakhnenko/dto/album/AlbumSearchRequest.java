package com.vakhnenko.dto.album;

import com.vakhnenko.dto.SearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AlbumSearchRequest extends SearchRequest {
    private String albumName;

    private Date albumDate;

    private String genre;

    private String performerName;

    private String songName;

    private Long performerId;
}
