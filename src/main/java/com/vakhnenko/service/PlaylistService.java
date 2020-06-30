package com.vakhnenko.service;

import com.vakhnenko.dto.playlist.PlaylistCreateRequest;
import com.vakhnenko.dto.playlist.PlaylistResponse;
import com.vakhnenko.dto.playlist.PlaylistSearchRequest;
import com.vakhnenko.dto.playlist.PlaylistUpdateRequest;
import com.vakhnenko.entity.Playlist;
import com.vakhnenko.entity.Song;
import com.vakhnenko.entity.User;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.repository.PlaylistRepository;
import com.vakhnenko.repository.SongRepository;
import com.vakhnenko.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static com.vakhnenko.utils.Exceptions.*;

@Service
@RequiredArgsConstructor
public class PlaylistService {
    @Autowired
    private EntityManager manager;

    private final PlaylistRepository playlistRep;
    private final UserRepository userRep;
    private final SongRepository songRep;

    public PlaylistResponse create(PlaylistCreateRequest request) {
        if (playlistRep.existsByPlaylistNameAndAuthor_Login(request.getPlaylistName(), request.getAuthor()))
            throw new BadRequestException("Плейлист " + request.getPlaylistName() + " уже создан!");
        User author = userRep.findByLogin(request.getAuthor()).orElseThrow(USER_NOT_FOUND);
        Playlist playlist = new Playlist();
        playlist.setPlaylistName(request.getPlaylistName());
        playlist.setAuthor(author);
        playlistRep.save(playlist);
        return new PlaylistResponse(playlist);
    }

    public PlaylistResponse update(PlaylistUpdateRequest request) {
        Playlist playlist = playlistRep.findById(request.getPlaylistId()).orElseThrow(PLAYLIST_NOT_FOUND);
        if (request.getPlaylistName() != null) {
            if (!playlist.getPlaylistName().equals(request.getPlaylistName())) {
                if (playlistRep.existsByPlaylistNameAndAuthor(request.getPlaylistName(), playlist.getAuthor()))
                    throw new BadRequestException("Плейлист " + request.getPlaylistName() + " уже создан!");
                playlist.setPlaylistName(request.getPlaylistName());
                playlistRep.save(playlist);
            }
        }
        return new PlaylistResponse(playlist);
    }

    public PlaylistResponse view(Long id) {
        Playlist playlist = playlistRep.findById(id).orElseThrow(PLAYLIST_NOT_FOUND);
        return new PlaylistResponse(playlist);
    }

    public Page<Playlist> search(PlaylistSearchRequest request) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Playlist> query = builder.createQuery(Playlist.class);
        Root<Playlist> root = query.from(Playlist.class);
        Predicate basePredicate = builder.equal(root.get("author").get("login"), request.getAuthorName());
        if (request.getAlbumName() != null) {
            Join<Playlist, Song> join = root.join("songs");
            basePredicate = builder.and(basePredicate, builder.like(join.get("album").get("albumName"),
                    "%" + request.getAlbumName() + "%"));
        }
        if (request.getPerformerName() != null) {
            Join<Playlist, Song> join = root.join("songs");
            basePredicate = builder.and(basePredicate, builder.like(join.get("album")
                    .get("performer").get("performerName"), "%" + request.getPerformerName() + "%"));
        }
        if (request.getPlaylistName() != null) {
            basePredicate = builder.and(basePredicate, builder.like(root.get("playlistName"),
                    "%" + request.getPlaylistName() + "%"));
        }
        if (request.getSongName() != null) {
            Join<Playlist, Song> join = root.join("songs");
            basePredicate = builder.and(basePredicate, builder.like(join.get("songName"),
                    "%" + request.getSongName() + "%"));
        }
        query.where(basePredicate);
        TypedQuery<Playlist> typedQuery = manager.createQuery(query);
        typedQuery.setFirstResult(request.getPage() * request.getSize());
        typedQuery.setMaxResults(request.getPage() * request.getSize() + request.getSize());
        List<Playlist> playlists = typedQuery.getResultList();
        return new PageImpl<Playlist>(playlists, request.pageable(), playlists.size());
    }

    public List<PlaylistResponse> getAll(String authorName) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Playlist> query = builder.createQuery(Playlist.class);
        Root<Playlist> root = query.from(Playlist.class);
        query.where(builder.equal(root.get("author").get("login"), authorName));
        TypedQuery<Playlist> typedQuery = manager.createQuery(query);
        List<Playlist> found = typedQuery.getResultList();
        List<PlaylistResponse> result = new ArrayList<>();
        for (int i = 0; i < found.size(); i++)
            result.add(new PlaylistResponse(found.get(i)));
        return result;
    }

    public Page<Song> getSongs(Long id, int page, int size) {
        Playlist playlist = playlistRep.findById(id).orElseThrow(PLAYLIST_NOT_FOUND);
        List<Song> songs = new ArrayList<>(playlist.getSongs());
        return new PageImpl<Song>(songs, PageRequest.of(page, size), songs.size());
    }

    public void delete(Long id) {
        playlistRep.deleteById(id);
    }

    public void removeSong(Long id, Long songId) {
        Playlist playlist = playlistRep.findById(id).orElseThrow(PLAYLIST_NOT_FOUND);
        Song song = songRep.findById(songId).orElseThrow(SONG_NOT_FOUND);
        playlist.getSongs().remove(song);
        song.getPlaylists().remove(playlist);
        songRep.save(song);
        playlistRep.save(playlist);
    }
}
