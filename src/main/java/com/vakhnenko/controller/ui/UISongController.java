package com.vakhnenko.controller.ui;

import com.vakhnenko.dto.song.SongCreateRequest;
import com.vakhnenko.dto.song.SongUpdateRequest;
import com.vakhnenko.entity.*;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.exception.NotFoundException;
import com.vakhnenko.utils.Helper;
import com.vakhnenko.utils.PreviousPage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class UISongController {
    @Resource(name = "sessionPreviousPage")
    private PreviousPage previous;

    @Resource(name = "sessionUser")
    private User sessionUser;

    @RequestMapping("/songs")
    public String viewSongs(@RequestParam(required = false) String pageBack,
                            @RequestParam(required = false) String pageForward,
                            @RequestParam(required = false) String logout,
                            Model model) {
        try {
            if (logout != null) {
                sessionUser.setLogin(null);
                return "redirect:/logout";
            }
            RestTemplate template = new RestTemplate();
            template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            String sessionCookie = "JSESSIONID=" + RequestContextHolder.currentRequestAttributes().getSessionId()
                    + "; Path=/; HttpOnly";
            if (sessionUser.getLogin() == null) {
                sessionUser = Helper.getSessionUserData(template, sessionCookie);
            }
            if (sessionUser.getRole().getRole().toUpperCase().equals("ADMIN"))
                model.addAttribute("access", true);
            model.addAttribute("username", sessionUser.getUsername());
            int page = Helper.setupPage(pageBack, pageForward);
            model.addAttribute("currentPage", (page + 1));
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/songs")
                    .queryParam("page", page);
            String redirectAttr;
            if (model.asMap().get("playlistName") != null) {
                redirectAttr = (String) model.asMap().get("playlistName");
                if (!redirectAttr.equals(""))
                    uriBuilder.queryParam("playlistName", redirectAttr);
            }
            if (model.asMap().get("performerName") != null) {
                redirectAttr = (String) model.asMap().get("performerName");
                if (!redirectAttr.equals(""))
                    uriBuilder.queryParam("performerName", redirectAttr);
            }
            if (model.asMap().get("songName") != null) {
                redirectAttr = (String) model.asMap().get("songName");
                if (!redirectAttr.equals(""))
                    uriBuilder.queryParam("songName", redirectAttr);
            }
            if (model.asMap().get("albumName") != null) {
                redirectAttr = (String) model.asMap().get("albumName");
                if (!redirectAttr.equals(""))
                    uriBuilder.queryParam("albumName", redirectAttr);
            }
            if (model.asMap().get("genre") != null) {
                redirectAttr = (String) model.asMap().get("genre");
                if (!redirectAttr.equals(""))
                    uriBuilder.queryParam("genre", redirectAttr);
            }
            if (model.asMap().get("songLength") != null) {
                redirectAttr = (String) model.asMap().get("songLength");
                if (!redirectAttr.equals(""))
                    uriBuilder.queryParam("songLength", redirectAttr);
            }
            HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
            ResponseEntity<PagedModel<Song>> songs = template
                    .exchange(uriBuilder.toUriString(), HttpMethod.GET, entity,
                            new ParameterizedTypeReference<PagedModel<Song>>() {
                            });
            model.addAttribute("songs", songs.getBody());
        } catch (Exception e) {
            Throwable root = ExceptionUtils.getRootCause(e);
            System.out.println(root.getMessage());
            if (!root.getMessage().contains("\"message\":")) {
                model.addAttribute("error", root.getMessage());
            } else {
                String[] errorParts = root.getMessage().split("[{\\[\\]},]");
                int i = 0;
                while (!errorParts[i].contains("\"message\":"))
                    i++;
                model.addAttribute("error", errorParts[i].substring(11, errorParts[i].length() - 1));
            }
        }
        return "songs";
    }

    @RequestMapping("/search/songs")
    public String searchSongs(@RequestParam(required = false) String playlistName,
                              @RequestParam(required = false) String performerName,
                              @RequestParam(required = false) String songName,
                              @RequestParam(required = false) String albumName,
                              @RequestParam(required = false) String songLength,
                              @RequestParam(required = false) String genre,
                              @RequestParam(required = false) String startSearch,
                              @RequestParam(required = false) String logout,
                              final HttpServletRequest httpRequest,
                              Model model, RedirectAttributes redirect) {
        if (logout != null) {
            sessionUser.setLogin(null);
            return "redirect:/logout";
        }
        RestTemplate template = new RestTemplate();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        String sessionCookie = "JSESSIONID=" + RequestContextHolder.currentRequestAttributes().getSessionId()
                + "; Path=/; HttpOnly";
        if (sessionUser.getLogin() == null) {
            sessionUser = Helper.getSessionUserData(template, sessionCookie);
        }
        if (previous.getPreviousPage() == null)
            if (httpRequest.getHeader("Referer") != null)
                if (!httpRequest.getHeader("Referer").contains("/search/songs"))
                    previous.setPreviousPage(httpRequest.getHeader("Referer"));
        model.addAttribute("username", sessionUser.getUsername());
        if (performerName != null)
            model.addAttribute("performerName", performerName);
        if (albumName != null)
            model.addAttribute("albumName", albumName);
        if (startSearch != null) {
            redirect.addFlashAttribute("playlistName", playlistName);
            redirect.addFlashAttribute("performerName", performerName);
            redirect.addFlashAttribute("songName", songName);
            redirect.addFlashAttribute("albumName", albumName);
            redirect.addFlashAttribute("genre", genre);
            redirect.addFlashAttribute("songLength", songLength);
            redirect.addFlashAttribute("showAlbums", false);
            if (previous.getPreviousPage() != null) {
                if (!previous.getPreviousPage().equals("")) {
                    String redirectAddr = "redirect:" + previous.getPreviousPage();
                    previous.setPreviousPage(null);
                    return redirectAddr;
                }
            }
            return "redirect:/songs";
        }
        model.addAttribute("table", "songs");
        model.addAttribute("searchText", "Поиск песен");
        return "search";
    }

    @RequestMapping("/add/songs")
    public String addSong(@RequestParam(required = false) String songName,
                          @RequestParam(required = false) String songLength,
                          @RequestParam(required = false) String albumName,
                          @RequestParam(required = false) String performerName,
                          @RequestParam(required = false) String add,
                          @RequestParam(required = false) String logout,
                          final HttpServletRequest httpRequest,
                          Model model) {
        try {
            if (logout != null) {
                sessionUser.setLogin(null);
                return "redirect:/logout";
            }
            RestTemplate template = new RestTemplate();
            String sessionCookie = "JSESSIONID=" + RequestContextHolder.currentRequestAttributes().getSessionId()
                    + "; Path=/; HttpOnly";
            if (sessionUser.getLogin() == null) {
                sessionUser = Helper.getSessionUserData(template, sessionCookie);
            }
            if (previous.getPreviousPage() == null)
                if (httpRequest.getHeader("Referer") != null)
                    if (!httpRequest.getHeader("Referer").contains("/add/songs"))
                        previous.setPreviousPage(httpRequest.getHeader("Referer"));
            model.addAttribute("username", sessionUser.getUsername());
            model.addAttribute("table", "songs");
            model.addAttribute("addText", "Добавить песню");
            if (add != null) {
                SongCreateRequest request = new SongCreateRequest();
                request.setSongName(songName);
                request.setSongLength(Double.parseDouble(songLength));
                request.setAlbumName(albumName);
                request.setPerformerName(performerName);
                HttpEntity entity = new HttpEntity(request, Helper.setupHeaders(sessionCookie));
                template.exchange("http://localhost:8080/api/songs/add", HttpMethod.POST, entity, Song.class);
                if (previous.getPreviousPage() != null) {
                    if (!previous.getPreviousPage().equals("")) {
                        String redirectAddr = "redirect:" + previous.getPreviousPage();
                        previous.setPreviousPage(null);
                        return redirectAddr;
                    }
                }
                return "redirect:/songs";
            }
            if (performerName != null)
                model.addAttribute("performerName", performerName);
            if (albumName != null)
                model.addAttribute("albumName", albumName);
        } catch (Exception e) {
            model.addAttribute("albumName", albumName);
            model.addAttribute("songLength", songLength);
            model.addAttribute("performerName", performerName);
            model.addAttribute("songName", songName);
            Throwable root = ExceptionUtils.getRootCause(e);
            System.out.println(root.getMessage());
            if (!root.getMessage().contains("\"message\":")) {
                model.addAttribute("error", root.getMessage());
            } else {
                String[] errorParts = root.getMessage().split("[{\\[\\]},]");
                int i = 0;
                while (!errorParts[i].contains("\"message\":"))
                    i++;
                model.addAttribute("error", errorParts[i].substring(11, errorParts[i].length() - 1));
            }
        }
        return "add";
    }

    @RequestMapping("/update/songs/{id}")
    public String updateSong(@PathVariable Long id,
                             @RequestParam(required = false) String songName,
                             @RequestParam(required = false) String songLength,
                             @RequestParam(required = false) String albumName,
                             @RequestParam(required = false) String performerName,
                             @RequestParam(required = false) Long playlist,
                             @RequestParam(required = false) String update,
                             @RequestParam(required = false) String addToPlaylist,
                             @RequestParam(required = false) String logout,
                             final HttpServletRequest httpRequest,
                             Model model) {
        try {
            if (logout != null) {
                sessionUser.setLogin(null);
                return "redirect:/logout";
            }
            if (previous.getPreviousPage() == null)
                if (httpRequest.getHeader("Referer") != null)
                    if (!httpRequest.getHeader("Referer").contains("/update/songs"))
                        previous.setPreviousPage(httpRequest.getHeader("Referer"));
            RestTemplate template = new RestTemplate();
            template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            String sessionCookie = "JSESSIONID=" + RequestContextHolder.currentRequestAttributes().getSessionId()
                    + "; Path=/; HttpOnly";
            if (sessionUser.getLogin() == null) {
                sessionUser = Helper.getSessionUserData(template, sessionCookie);
            }
            model.addAttribute("username", sessionUser.getUsername());
            model.addAttribute("table", "songs");
            model.addAttribute("updateText", "Обновить песню");
            model.addAttribute("id", id);
            if (httpRequest.getRequestURI().contains("albumName")
                    && httpRequest.getRequestURI().contains("performerName"))
                System.out.println("true");
            if (songName == null) {
                HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
                Song song = template.exchange("http://localhost:8080/api/songs/" + id,
                        HttpMethod.GET, entity, Song.class).getBody();
                model.addAttribute("songName", song.getSongName());
                model.addAttribute("songLength", song.getSongLength());
                model.addAttribute("albuName", song.getAlbum().getAlbumName());
                model.addAttribute("performerName", song.getAlbum().getPerformer().getPerformerName());
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/" +
                        "playlists/allforuser")
                        .queryParam("authorName", ((UserDetails) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal()).getUsername());
                ResponseEntity<ArrayList<Playlist>> playlists = template
                        .exchange(uriBuilder.toUriString(), HttpMethod.GET, entity,
                                new ParameterizedTypeReference<ArrayList<Playlist>>() {
                                });
                if (sessionUser.getRole().getRole().toUpperCase().equals("ADMIN"))
                    model.addAttribute("access", true);
                model.addAttribute("playlists", playlists.getBody());
            }
            if (update != null) {
                SongUpdateRequest request = new SongUpdateRequest();
                request.setSongName(songName);
                request.setSongLength(Double.parseDouble(songLength));
                request.setAlbumName(albumName);
                request.setPerformerName(performerName);
                request.setPlaylistId(playlist);
                request.setSongId(id);
                HttpEntity entity = new HttpEntity(request, Helper.setupHeaders(sessionCookie));
                template.exchange("http://localhost:8080/api/songs/update", HttpMethod.PATCH, entity, Song.class);
            } else if (addToPlaylist != null) {
                if (playlist != null) {
                    SongUpdateRequest request = new SongUpdateRequest();
                    request.setPlaylistId(playlist);
                    request.setSongId(id);
                    HttpEntity entity = new HttpEntity(request, Helper.setupHeaders(sessionCookie));
                    template.exchange("http://localhost:8080/api/songs/addtoplaylist", HttpMethod.PATCH, entity, Song.class);
                }
            }
            if (addToPlaylist != null || update != null) {
                if (previous.getPreviousPage() != null) {
                    if (!previous.getPreviousPage().equals("")) {
                        String redirectAddr = "redirect:" + previous.getPreviousPage();
                        previous.setPreviousPage(null);
                        return redirectAddr;
                    }
                }
                return "redirect:/songs";
            }
        } catch (Exception e) {
            model.addAttribute("albumName", albumName);
            model.addAttribute("songLength", songLength);
            model.addAttribute("performerName", performerName);
            model.addAttribute("songName", songName);
            Throwable root = ExceptionUtils.getRootCause(e);
            System.out.println(root.getMessage());
            if (!root.getMessage().contains("\"message\":")) {
                model.addAttribute("error", root.getMessage());
            } else {
                String[] errorParts = root.getMessage().split("[{\\[\\]},]");
                int i = 0;
                while (!errorParts[i].contains("\"message\":"))
                    i++;
                model.addAttribute("error", errorParts[i].substring(11, errorParts[i].length() - 1));
            }
        }
        return "update";
    }

    @RequestMapping("/delete/songs/{id}")
    public String deleteSong(@PathVariable Long id,
                             @RequestParam(required = false) String delete,
                             @RequestParam(required = false) String logout,
                             final HttpServletRequest httpRequest,
                             Model model) {
        try {
            if (logout != null) {
                sessionUser.setLogin(null);
                return "redirect:/logout";
            }
            RestTemplate template = new RestTemplate();
            template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            String sessionCookie = "JSESSIONID=" + RequestContextHolder.currentRequestAttributes().getSessionId()
                    + "; Path=/; HttpOnly";
            if (sessionUser.getLogin() == null) {
                sessionUser = Helper.getSessionUserData(template, sessionCookie);
            }
            if (previous.getPreviousPage() == null)
                if (httpRequest.getHeader("Referer") != null)
                    if (!httpRequest.getHeader("Referer").contains("/delete/songs"))
                        previous.setPreviousPage(httpRequest.getHeader("Referer"));
            model.addAttribute("username", sessionUser.getUsername());
            model.addAttribute("table", "songs");
            model.addAttribute("deleteText", "Подтвердить удаление песни");
            if (delete != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Cookie", sessionCookie);
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity entity = new HttpEntity(headers);
                template.exchange("http://localhost:8080/api/songs/delete/" + id, HttpMethod.DELETE, entity, Void.class);
                if (previous.getPreviousPage() != null) {
                    if (!previous.getPreviousPage().equals("")) {
                        String redirectAddr = "redirect:" + previous.getPreviousPage();
                        previous.setPreviousPage(null);
                        return redirectAddr;
                    }
                }
                return "redirect:/songs";
            } else {
                HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
                Song song = template.exchange("http://localhost:8080/api/songs/" + id, HttpMethod.GET, entity,
                        Song.class).getBody();
                if (song == null) {
                    if (previous.getPreviousPage() != null) {
                        if (!previous.getPreviousPage().equals("")) {
                            String redirectAddr = "redirect:" + previous.getPreviousPage();
                            previous.setPreviousPage(null);
                            return redirectAddr;
                        }
                    }
                    return "redirect:/songs";
                }
                model.addAttribute("song", song);
            }
        } catch (Exception e) {
            Throwable root = ExceptionUtils.getRootCause(e);
            System.out.println(root.getMessage());
            if (!root.getMessage().contains("\"message\":")) {
                model.addAttribute("error", root.getMessage());
            } else {
                String[] errorParts = root.getMessage().split("[{\\[\\]},]");
                int i = 0;
                while (!errorParts[i].contains("\"message\":"))
                    i++;
                model.addAttribute("error", errorParts[i].substring(11, errorParts[i].length() - 1));
            }
        }
        return "delete";
    }


}
