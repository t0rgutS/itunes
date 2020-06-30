package com.vakhnenko.service;

import com.vakhnenko.dto.album.AlbumCreateRequest;
import com.vakhnenko.dto.album.AlbumResponse;
import com.vakhnenko.dto.album.AlbumSearchRequest;
import com.vakhnenko.dto.album.AlbumUpdateRequest;
import com.vakhnenko.entity.Album;
import com.vakhnenko.entity.Genre;
import com.vakhnenko.entity.Performer;
import com.vakhnenko.entity.Song;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.repository.AlbumRepository;
import com.vakhnenko.repository.GenreRepository;
import com.vakhnenko.repository.PerformerRepository;
import com.vakhnenko.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

import static com.vakhnenko.utils.Exceptions.*;

@RestController
@RequiredArgsConstructor
public class AlbumService {
    @Autowired
    private EntityManager manager;

    private final AlbumRepository albumRep;
    private final SongRepository songRep;
    private final GenreRepository genreRep;
    private final PerformerRepository performerRep;

    public AlbumResponse create(AlbumCreateRequest request) {
        if (albumRep.existsByAlbumNameAndPerformer_PerformerName(request.getAlbumName(), request.getPerformerName()))
            throw new BadRequestException("Альбом " + request.getAlbumName() + " исполнителя "
                    + request.getPerformerName() + "уже добавлен!");
        Performer performer = performerRep.findByPerformerName(request.getPerformerName()).orElseThrow(PERFORMER_NOT_FOUND);
        Album album = new Album();
        album.setAlbumName(request.getAlbumName());
        album.setAlbumDate(request.getAlbumDate());
        Genre genre;
        if (!genreRep.existsByGenre(request.getGenre())) {
            genre = new Genre();
            genre.setGenre(request.getGenre());
            genreRep.save(genre);
        } else
            genre = genreRep.findByGenre(request.getGenre()).orElseThrow(GENRE_NOT_FOUND);
        album.setGenre(genre);
        album.setPerformer(performer);
        albumRep.save(album);
        return new AlbumResponse(album);
    }

    public AlbumResponse update(AlbumUpdateRequest request) {
        Album album = albumRep.findById(request.getAlbumId()).orElseThrow(ALBUM_NOT_FOUND);
        if (request.getAlbumName() != null) {
            if (!album.getAlbumName().equals(request.getAlbumName())) {
                if (albumRep.existsByAlbumNameAndPerformer(request.getAlbumName(), album.getPerformer()))
                    throw new BadRequestException("Альбом " + request.getAlbumName() + " исполнителя "
                            + album.getPerformer().getPerformerName() + " уже добавлен!");
                album.setAlbumName(request.getAlbumName());
            }
        }
        if (request.getAlbumDate() != null)
            if (!album.getAlbumDate().equals(request.getAlbumDate()))
                album.setAlbumDate(request.getAlbumDate());
        if (request.getGenre() != null) {
            if (!album.getGenre().getGenre().equals(request.getGenre())) {
                Genre genre = genreRep.findByGenre(request.getGenre()).orElseThrow(GENRE_NOT_FOUND);
                album.setGenre(genre);
            }
        }
        if (request.getPerformerName() != null) {
            if (!album.getPerformer().getPerformerName().equals(request.getPerformerName())) {
                if (albumRep.existsByAlbumNameAndPerformer_PerformerName(album.getAlbumName(), request.getPerformerName()))
                    throw new BadRequestException("Альбом " + album.getAlbumName() + " исполнителя "
                            + request.getPerformerName() + "уже добавлен!");
                Performer performer = performerRep.findByPerformerName(request.getPerformerName())
                        .orElseThrow(PERFORMER_NOT_FOUND);
                album.setPerformer(performer);
            }
        }
        albumRep.save(album);
        return new AlbumResponse(album);
    }

    public AlbumResponse view(Long id) {
        Album album = albumRep.findById(id).orElseThrow(ALBUM_NOT_FOUND);
        return new AlbumResponse(album);
    }

    public Page<Album> search(AlbumSearchRequest request) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Album> query = builder.createQuery(Album.class);
        Root<Album> root = query.from(Album.class);
        Predicate basePredicate = null;
        if (request.getAlbumName() != null) {
            basePredicate = builder.like(root.get("albumName"), "%" + request.getAlbumName() + "%");
        }
        if (request.getAlbumDate() != null) {
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.equal(root.get("albumDate"),
                        request.getAlbumDate()));
            else
                basePredicate = builder.equal(root.get("albumDate"), request.getAlbumDate());
        }
        if (request.getPerformerId() != null) {
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.equal(root.get("performer")
                        .get("performerId"), request.getPerformerId()));
            else
                basePredicate = builder.equal(root.get("performer").get("performerId"), request.getPerformerId());
        } else if (request.getPerformerName() != null) {
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.like(root.get("performer")
                        .get("performerName"), "%" + request.getPerformerName() + "%"));
            else
                basePredicate = builder.like(root.get("performer").get("performerName"),
                        "%" + request.getPerformerName() + "%");
        }
        if (request.getGenre() != null) {
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.like(root.get("genre").get("genre"),
                        "%" + request.getGenre() + "%"));
            else
                basePredicate = builder.like(root.get("genre").get("genre"), "%" + request.getGenre() + "%");
        }
        if (request.getSongName() != null) {
            Join<Album, Song> join = root.join("songs");
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.like(join.get("songName"),
                        "%" + request.getSongName() + "%"));
            else
                basePredicate = builder.like(join.get("songName"), "%" + request.getSongName() + "%");
        }
        if (basePredicate != null)
            query.where(basePredicate);
        TypedQuery<Album> typedQuery = manager.createQuery(query);
        typedQuery.setFirstResult(request.getPage() * request.getSize());
        typedQuery.setMaxResults(request.getPage() * request.getSize() + request.getSize());
        List<Album> albums = typedQuery.getResultList();
        return new PageImpl<Album>(albums, request.pageable(), albums.size());
    }

    public void delete(Long id) {
        albumRep.deleteById(id);
    }

    public void removeSong(Long id, Long songId) {
        Album album = albumRep.findById(id).orElseThrow(ALBUM_NOT_FOUND);
        Song song = songRep.findById(songId).orElseThrow(SONG_NOT_FOUND);
        album.getSongs().remove(song);
        albumRep.save(album);
    }
}
