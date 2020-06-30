package com.vakhnenko.controller.rest;

import com.vakhnenko.dto.album.AlbumCreateRequest;
import com.vakhnenko.dto.album.AlbumResponse;
import com.vakhnenko.dto.album.AlbumSearchRequest;
import com.vakhnenko.dto.album.AlbumUpdateRequest;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public AlbumResponse create(@Valid @RequestBody AlbumCreateRequest request) {
        return albumService.create(request);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public AlbumResponse update(@Valid @RequestBody AlbumUpdateRequest request) {
        return albumService.update(request);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public AlbumResponse view(@PathVariable Long id) {
        return albumService.view(id);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<AlbumResponse> viewAllAlbums(@RequestParam(required = false) String albumName,
                                             @RequestParam(required = false) String albumDate,
                                             @RequestParam(required = false) String genre,
                                             @RequestParam(required = false) String performerName,
                                             @RequestParam(required = false) String songName,
                                             @RequestParam(required = false) Long performerId,
                                             @RequestParam Integer page) {
        try {
            AlbumSearchRequest request = new AlbumSearchRequest();
            if (performerId != null)
                request.setPerformerId(performerId);
            if (albumName != null)
                if (!albumName.equals(""))
                    request.setAlbumName(URLDecoder.decode(albumName, "UTF-8"));
            if (albumDate != null)
                if (!albumDate.equals(""))
                    request.setAlbumDate(new SimpleDateFormat("yyyy-MM-dd")
                            .parse(URLDecoder.decode(albumDate, "UTF-8")));
            if (performerName != null)
                if (!performerName.equals(""))
                    request.setPerformerName(URLDecoder.decode(performerName, "UTF-8"));
            if (songName != null)
                if (!songName.equals(""))
                    request.setSongName(URLDecoder.decode(songName, "UTF-8"));
            if (genre != null)
                if (!genre.equals(""))
                    request.setGenre(URLDecoder.decode(genre, "UTF-8"));
            request.setPage(page);
            return albumService.search(request).map(AlbumResponse::new);
        } catch (UnsupportedEncodingException uee) {
            throw new BadRequestException("Не удалось получить данные для поиска!");
        } catch (ParseException pe) {
            throw new BadRequestException("Неверный формат даты выпуска альбома!");
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        albumService.delete(id);
    }

    @RequestMapping(value = "/{id}/removesong/{songId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void removeSong(@PathVariable Long id, @PathVariable Long songId) {
        albumService.removeSong(id, songId);
    }
}
