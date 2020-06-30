package com.vakhnenko.service;

import com.vakhnenko.dto.song.SongCreateRequest;
import com.vakhnenko.dto.song.SongResponse;
import com.vakhnenko.dto.song.SongSearchRequest;
import com.vakhnenko.dto.song.SongUpdateRequest;
import com.vakhnenko.entity.Album;
import com.vakhnenko.entity.Performer;
import com.vakhnenko.entity.Playlist;
import com.vakhnenko.entity.Song;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.repository.AlbumRepository;
import com.vakhnenko.repository.PerformerRepository;
import com.vakhnenko.repository.PlaylistRepository;
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
public class SongService {
    @Autowired
    private EntityManager manager;

    private final SongRepository songRep;
    private final AlbumRepository albumRep;
    private final PerformerRepository performerRep;
    private final PlaylistRepository playlistRep;

    public SongResponse create(SongCreateRequest request) {
        if (songRep.existsBySongNameAndAlbum_AlbumName(request.getSongName(), request.getAlbumName()))
            throw new BadRequestException("Песня " + request.getSongName() + " уже добавлена в альбом "
                    + request.getAlbumName());
        Performer performer = performerRep.findByPerformerName(request.getPerformerName()).orElseThrow(PERFORMER_NOT_FOUND);
        Album album = albumRep.findByAlbumNameAndPerformer(request.getAlbumName(), performer).orElseThrow(ALBUM_NOT_FOUND);
        Song song = new Song();
        song.setSongName(request.getSongName());
        song.setSongLength(request.getSongLength());
        song.setAlbum(album);
        songRep.save(song);
        return new SongResponse(song);
    }

    public SongResponse update(SongUpdateRequest request) {
        Song song = songRep.findById(request.getSongId()).orElseThrow(SONG_NOT_FOUND);
        if (request.getSongName() != null) {
            if (!song.getSongName().equals(request.getSongName())) {
                if (songRep.existsBySongNameAndAlbum(request.getSongName(), song.getAlbum()))
                    throw new BadRequestException("Песня " + request.getSongName() + " уже добавлена в альбом "
                            + song.getAlbum().getAlbumName());
                song.setSongName(request.getSongName());
            }
        }
        if (request.getSongLength() != null)
            if (!song.getSongLength().equals(request.getSongLength()))
                song.setSongLength(request.getSongLength());
        if (request.getAlbumName() != null && request.getPerformerName() != null) {
            if (!song.getAlbum().getAlbumName().equals(request.getAlbumName())) {
                Album album = albumRep.findByAlbumNameAndPerformer_PerformerName(request.getAlbumName(),
                        request.getPerformerName())
                        .orElseThrow(ALBUM_NOT_FOUND);
                if (songRep.existsBySongNameAndAlbum(song.getSongName(), album))
                    throw new BadRequestException("Песня " + song.getSongName() + " уже добавлена в альбом "
                            + request.getAlbumName());
                song.setAlbum(album);
            }
        } else if ((request.getAlbumName() != null && request.getPerformerName() == null)
                || (request.getAlbumName() == null && request.getPerformerName() != null))
            throw new BadRequestException("Чтобы занести песню в альбом, нужно указать название альбома и исполнителя!");
        if (request.getPlaylistId() != null) {
            Playlist playlist = playlistRep.findById(request.getPlaylistId()).orElseThrow(PLAYLIST_NOT_FOUND);
            if (!song.getPlaylists().contains(playlist)) {
                song.getPlaylists().add(playlist);
                playlist.getSongs().add(song);
                playlistRep.save(playlist);
            }
        }
        songRep.save(song);
        return new SongResponse(song);
    }

    public Page<Song> search(SongSearchRequest request) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Song> query = builder.createQuery(Song.class);
        Root<Song> root = query.from(Song.class);
        Predicate basePredicate = null;
        if (request.getSongName() != null) {
            basePredicate = builder.like(root.get("songName"), "%" + request.getSongName() + "%");
        }
        if (request.getPlaylistId() != null) {
            Join<Song, Playlist> join = root.join("playlists");
            if (basePredicate != null) {
                basePredicate = builder.and(basePredicate, builder.equal(join.get("playlistId"),
                        request.getPlaylistId()));
            } else {
                basePredicate = builder.equal(join.get("playlistId"),
                        request.getPlaylistId());
            }
        } else if (request.getPlaylistName() != null && request.getPlaylistAuthor() != null) {
            Join<Song, Playlist> join = root.join("playlists");
            if (basePredicate != null) {
                basePredicate = builder.and(basePredicate, builder.like(join.get("playlistName"),
                        "%" + request.getPlaylistName() + "%"));
            } else {
                basePredicate = builder.like(join.get("playlistName"),
                        "%" + request.getPlaylistName() + "%");
            }
            basePredicate = builder.and(basePredicate, builder.equal(join.get("author").get("login"),
                    request.getPlaylistAuthor()));
        }
        if (request.getAlbumId() != null) {
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.equal(root.get("album").get("albumId"),
                        request.getAlbumId()));
            else
                basePredicate = builder.equal(root.get("album").get("albumId"), request.getAlbumId());
        } else if (request.getAlbumName() != null) {
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.like(root.get("album").get("albumName"),
                        "%" + request.getAlbumName() + "%"));
            else
                basePredicate = builder.like(root.get("album").get("albumName"),
                        "%" + request.getAlbumName() + "%");
        }
        if (request.getPerformerId() != null) {
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.equal(root.get("album")
                        .get("performer").get("performerId"), request.getPerformerId()));
            else
                basePredicate = builder.equal(root.get("album").get("performer").get("performerId"),
                        request.getPerformerId());
        } else if (request.getPerformerName() != null) {
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.like(root.get("album")
                        .get("performer").get("performerName"), "%" + request.getPerformerName() + "%"));
            else
                basePredicate = builder.like(root.get("album").get("performer").get("performerName"),
                        "%" + request.getPerformerName() + "%");
        }
        if (request.getGenre() != null) {
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.like(root.get("album").get("genre").get("genre"),
                        "%" + request.getGenre() + "%"));
            else
                basePredicate = builder.like(root.get("album").get("genre").get("genre"),
                        "%" + request.getGenre() + "%");
        }
        if (basePredicate != null)
            query.where(basePredicate);
        TypedQuery<Song> typedQuery = manager.createQuery(query);
        typedQuery.setFirstResult(request.getPage() * request.getSize());
        typedQuery.setMaxResults(request.getPage() * request.getSize() + request.getSize());
        List<Song> songs = typedQuery.getResultList();
        return new PageImpl<Song>(songs, request.pageable(), songs.size());
    }

    public SongResponse view(Long id) {
        Song song = songRep.findById(id).orElseThrow(PLAYLIST_NOT_FOUND);
        return new SongResponse(song);
    }

    public void delete(Long id) {
        songRep.deleteById(id);
    }
}
