package com.vakhnenko.dto.album;

import com.vakhnenko.dto.EntityResponse;
import com.vakhnenko.dto.genre.GenreResponse;
import com.vakhnenko.dto.performer.PerformerResponse;
import com.vakhnenko.entity.Album;
import com.vakhnenko.entity.Genre;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class AlbumResponse extends EntityResponse {
    private final Long albumId;
    private final String albumName;
    private final Date albumDate;
    private final GenreResponse genre;
    private final PerformerResponse performer;

    public AlbumResponse(Album album) {
        super(album);
        this.albumId = album.getAlbumId();
        this.albumName = album.getAlbumName();
        this.albumDate = album.getAlbumDate();
        this.genre = new GenreResponse(album.getGenre());
        this.performer = new PerformerResponse(album.getPerformer());
    }

    public String getFormattedDate() {
        if (albumDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(albumDate);
        } else return null;
    }
}
