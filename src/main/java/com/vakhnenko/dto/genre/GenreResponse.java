package com.vakhnenko.dto.genre;

import com.vakhnenko.dto.EntityResponse;
import com.vakhnenko.entity.Genre;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreResponse extends EntityResponse {
    private final Long genreId;
    private final String genre;

    public GenreResponse(Genre genre) {
        super(genre);
        this.genreId = genre.getGenreId();
        this.genre = genre.getGenre();
    }
}
