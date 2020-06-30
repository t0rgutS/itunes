package com.vakhnenko.controller.rest;

import com.vakhnenko.dto.song.SongCreateRequest;
import com.vakhnenko.dto.song.SongResponse;
import com.vakhnenko.dto.song.SongSearchRequest;
import com.vakhnenko.dto.song.SongUpdateRequest;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public SongResponse create(@Valid @RequestBody SongCreateRequest request) {
        return songService.create(request);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public SongResponse update(@Valid @RequestBody SongUpdateRequest request) {
        return songService.update(request);
    }

    @RequestMapping(value = "/addtoplaylist", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public SongResponse addToPlaylist(@Valid @RequestBody SongUpdateRequest request) {
        return songService.update(request);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SongResponse view(@PathVariable Long id) {
        return songService.view(id);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SongResponse> viewAllSongs(@RequestParam(required = false) String songName,
                                           @RequestParam(required = false) String albumName,
                                           @RequestParam(required = false) String playlistName,
                                           @RequestParam(required = false) String playlistAuthor,
                                           @RequestParam(required = false) String performerName,
                                           @RequestParam(required = false) String genre,
                                           @RequestParam(required = false) Long performerId,
                                           @RequestParam(required = false) Long albumId,
                                           @RequestParam(required = false) Long playlistId,
                                           @RequestParam Integer page) {
        try {
            SongSearchRequest request = new SongSearchRequest();
            if (albumId != null)
                request.setAlbumId(albumId);
            if (performerId != null)
                request.setPerformerId(performerId);
            if (playlistId != null)
                request.setPlaylistId(playlistId);
            if (songName != null)
                if (!songName.equals(""))
                    request.setSongName(URLDecoder.decode(songName, "UTF-8"));
            if (albumName != null)
                if (!albumName.equals(""))
                    request.setAlbumName(URLDecoder.decode(albumName, "UTF-8"));
            if (playlistName != null)
                if (!playlistName.equals(""))
                    request.setPlaylistName(URLDecoder.decode(playlistName, "UTF-8"));
            if (playlistAuthor != null)
                if (!playlistAuthor.equals(""))
                    request.setPlaylistAuthor(URLDecoder.decode(playlistAuthor, "UTF-8"));
            if (performerName != null)
                if (!performerName.equals(""))
                    request.setPerformerName(URLDecoder.decode(performerName, "UTF-8"));
            if (genre != null)
                if (!genre.equals(""))
                    request.setGenre(URLDecoder.decode(genre, "UTF-8"));
            request.setPage(page);
            return songService.search(request).map(SongResponse::new);
        } catch (UnsupportedEncodingException uee) {
            throw new BadRequestException("Не удалось получить данные для поиска!");
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        songService.delete(id);
    }
}
