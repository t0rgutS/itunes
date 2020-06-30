package com.vakhnenko.controller.rest;

import com.vakhnenko.dto.playlist.PlaylistCreateRequest;
import com.vakhnenko.dto.playlist.PlaylistResponse;
import com.vakhnenko.dto.playlist.PlaylistSearchRequest;
import com.vakhnenko.dto.playlist.PlaylistUpdateRequest;
import com.vakhnenko.dto.song.SongResponse;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PlaylistResponse create(@Valid @RequestBody PlaylistCreateRequest request) {
        return playlistService.create(request);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public PlaylistResponse update(@Valid @RequestBody PlaylistUpdateRequest request) {
        return playlistService.update(request);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PlaylistResponse view(@PathVariable Long id) {
        return playlistService.view(id);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PlaylistResponse> viewAllPlaylists(@RequestParam String authorName,
                                                   @RequestParam(required = false) String playlistName,
                                                   @RequestParam(required = false) String performerName,
                                                   @RequestParam(required = false) String songName,
                                                   @RequestParam(required = false) String albumName,
                                                   @RequestParam Integer page) {
        try {
            PlaylistSearchRequest request = new PlaylistSearchRequest();
            request.setAuthorName(URLDecoder.decode(authorName, "UTF-8"));
            if (playlistName != null)
                if (!playlistName.equals(""))
                    request.setPlaylistName(URLDecoder.decode(playlistName, "UTF-8"));
            if (performerName != null)
                if (!performerName.equals(""))
                    request.setPerformerName(URLDecoder.decode(performerName, "UTF-8"));
            if (songName != null)
                if (!songName.equals(""))
                    request.setSongName(URLDecoder.decode(songName, "UTF-8"));
            if (albumName != null)
                if (!albumName.equals(""))
                    request.setAlbumName(URLDecoder.decode(albumName, "UTF-8"));
            request.setPage(page);
            return playlistService.search(request).map(PlaylistResponse::new);
        } catch (UnsupportedEncodingException uee) {
            throw new BadRequestException("Не удалось получить данные для поиска!");
        }
    }

    @RequestMapping(value = "/allforuser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlaylistResponse> allUsersPlaylists(@RequestParam String authorName) {
        try {
            return playlistService.getAll(URLDecoder.decode(authorName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new BadRequestException("Не удалось получить данные для поиска!");
        }
    }

    @RequestMapping(value = "{id}/songs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SongResponse> viewPlaylistSongs(@PathVariable Long id,
                                                @RequestParam Integer page,
                                                @RequestParam(required = false) Integer size) {
        if (size != null)
            return playlistService.getSongs(id, page, size).map(SongResponse::new);
        else
            return playlistService.getSongs(id, page, 10).map(SongResponse::new);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable Long id) {
        playlistService.delete(id);
    }

    @RequestMapping(value = "/{id}/removesong/{songId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeSong(@PathVariable Long id, @PathVariable Long songId) {
        playlistService.removeSong(id, songId);
    }
}
