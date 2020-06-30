package com.vakhnenko.service;

import com.vakhnenko.dto.genre.GenreCreateRequest;
import com.vakhnenko.dto.genre.GenreResponse;
import com.vakhnenko.dto.genre.GenreUpdateRequest;
import com.vakhnenko.entity.Genre;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import static com.vakhnenko.utils.Exceptions.GENRE_NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRep;

    public GenreResponse create(GenreCreateRequest request) {
        if (genreRep.existsByGenre(request.getGenre()))
            throw new BadRequestException("Жанр " + request.getGenre() + " уже добавлен!");
        Genre genre = new Genre();
        genre.setGenre(request.getGenre());
        genreRep.save(genre);
        return new GenreResponse(genre);
    }

    public GenreResponse update(GenreUpdateRequest request) {
        Genre genre = genreRep.findById(request.getGenreId()).orElseThrow(GENRE_NOT_FOUND);
        if (request.getGenre() != null) {
            if (genreRep.existsByGenre(request.getGenre()))
                throw new BadRequestException("Жанр " + request.getGenre() + " уже добавлен!");
            genre.setGenre(request.getGenre());
            genreRep.save(genre);
        }
        return new GenreResponse(genre);
    }
}
