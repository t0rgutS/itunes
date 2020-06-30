package com.vakhnenko.controller.rest;

import com.vakhnenko.dto.performer.PerformerCreateRequest;
import com.vakhnenko.dto.performer.PerformerResponse;
import com.vakhnenko.dto.performer.PerformerSearchRequest;
import com.vakhnenko.dto.performer.PerformerUpdateRequest;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.service.PerformerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("/api/performers")
@RequiredArgsConstructor
public class PerformerController {
    private final PerformerService performerService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public PerformerResponse create(@Valid @RequestBody PerformerCreateRequest request) {
        return performerService.create(request);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public PerformerResponse update(@Valid @RequestBody PerformerUpdateRequest request) {
        return performerService.update(request);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PerformerResponse view(@PathVariable Long id) {
        return performerService.view(id);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PerformerResponse> viewAllPerformers(@RequestParam(required = false) String albumName,
                                                     @RequestParam(required = false) String genre,
                                                     @RequestParam(required = false) String performerName,
                                                     @RequestParam(required = false) String songName,
                                                     @RequestParam Integer page) {
        try {
            PerformerSearchRequest request = new PerformerSearchRequest();
            if (albumName != null)
                if (!albumName.equals(""))
                    request.setAlbumName(URLDecoder.decode(albumName, "UTF-8"));
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
            return performerService.search(request).map(PerformerResponse::new);
        } catch (UnsupportedEncodingException uee) {
            throw new BadRequestException("Не удалось получить данные для поиска!");
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        performerService.delete(id);
    }

}
