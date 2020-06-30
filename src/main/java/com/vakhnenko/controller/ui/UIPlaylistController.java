package com.vakhnenko.controller.ui;

import com.vakhnenko.dto.playlist.PlaylistCreateRequest;
import com.vakhnenko.dto.playlist.PlaylistUpdateRequest;
import com.vakhnenko.entity.Playlist;
import com.vakhnenko.entity.Song;
import com.vakhnenko.entity.User;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.exception.NotFoundException;
import com.vakhnenko.utils.Helper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
import java.util.Arrays;

@Controller
public class UIPlaylistController {
    @Resource(name = "sessionUser")
    private User sessionUser;

    @RequestMapping({"/", "/playlists"})
    public String viewPlaylists(@RequestParam(required = false) String pageBack,
                                @RequestParam(required = false) String pageForward,
                                @RequestParam(required = false) String logout,
                                Model model) {
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
        model.addAttribute("username", sessionUser.getUsername());
        int page = Helper.setupPage(pageBack, pageForward);
        model.addAttribute("currentPage", (page + 1));
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/playlists")
                .queryParam("authorName", ((UserDetails) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername())
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
        HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
        ResponseEntity<PagedModel<Playlist>> playlists = template
                .exchange(uriBuilder.toUriString(), HttpMethod.GET, entity,
                        new ParameterizedTypeReference<PagedModel<Playlist>>() {
                        });
        model.addAttribute("playlists", playlists.getBody());
        return "playlists";
    }

    @RequestMapping("/playlists/{id}")
    public String viewSinglePlaylist(@PathVariable Long id,
                                     @RequestParam(required = false) String pageBack,
                                     @RequestParam(required = false) String pageForward,
                                     @RequestParam(required = false) String removeSong,
                                     @RequestParam(required = false) String logout,
                                     Model model) {
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
        model.addAttribute("username", sessionUser.getUsername());
        int page = Helper.setupPage(pageBack, pageForward);
        model.addAttribute("currentPage", (page + 1));
        HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
        if (removeSong != null) {
            if (!removeSong.equals("")) {
                template.exchange("http://localhost:8080/api/playlists/" + id + "/removesong/" + removeSong,
                        HttpMethod.DELETE, entity, Void.class);
            }
        }
        Playlist playlist = template.exchange("http://localhost:8080/api/playlists/" + id,
                HttpMethod.GET, entity, Playlist.class).getBody();
        model.addAttribute("playlist", playlist);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/songs")
                .queryParam("playlistId", playlist.getPlaylistId())
                .queryParam("page", page);
        if (model.asMap().get("performerName") != null)
            uriBuilder.queryParam("performerName", (String) model.asMap().get("performerName"));
        if (model.asMap().get("songName") != null)
            uriBuilder.queryParam("songName", (String) model.asMap().get("songName"));
        if (model.asMap().get("albumName") != null)
            uriBuilder.queryParam("albumName", (String) model.asMap().get("albumName"));
        if (model.asMap().get("genre") != null)
            uriBuilder.queryParam("genre", (String) model.asMap().get("genre"));
        if (model.asMap().get("songLength") != null)
            uriBuilder.queryParam("songLength", (String) model.asMap().get("songLength"));
        ResponseEntity<PagedModel<Song>> songs = template
                .exchange(uriBuilder.toUriString(), HttpMethod.GET, entity,
                        new ParameterizedTypeReference<PagedModel<Song>>() {
                        });
        model.addAttribute("songs", songs.getBody());
        return "singlePlaylist";
    }

    @RequestMapping("/search/playlists")
    public String searchPlaylists(@RequestParam(required = false) String playlistName,
                                  @RequestParam(required = false) String performerName,
                                  @RequestParam(required = false) String songName,
                                  @RequestParam(required = false) String albumName,
                                  @RequestParam(required = false) String startSearch,
                                  @RequestParam(required = false) String logout,
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
        model.addAttribute("username", sessionUser.getUsername());
        if (startSearch != null) {
            redirect.addFlashAttribute("playlistName", playlistName);
            redirect.addFlashAttribute("performerName", performerName);
            redirect.addFlashAttribute("songName", songName);
            redirect.addFlashAttribute("albumName", albumName);
            return "redirect:/";
        }
        model.addAttribute("table", "playlists");
        model.addAttribute("searchText", "Поиск плейлистов");
        return "search";
    }

    @RequestMapping("/add/playlists")
    public String addPlaylist(@RequestParam(required = false) String playlistName,
                              @RequestParam(required = false) String add,
                              @RequestParam(required = false) String logout,
                              Model model) {
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
        model.addAttribute("username", sessionUser.getUsername());
        model.addAttribute("table", "playlists");
        model.addAttribute("addText", "Добавить плейлист");
        if (add != null) {
            PlaylistCreateRequest request = new PlaylistCreateRequest();
            request.setPlaylistName(playlistName);
            request.setAuthor(((UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getUsername());
            HttpEntity entity = new HttpEntity(request, Helper.setupHeaders(sessionCookie));
            template.exchange("http://localhost:8080/api/playlists/add", HttpMethod.POST, entity, Playlist.class);
            return "redirect:/";
        }
        return "add";
    }

    @RequestMapping("/update/playlists/{id}")
    public String updatePlaylist(@PathVariable Long id,
                                 @RequestParam(required = false) String playlistName,
                                 @RequestParam(required = false) String update,
                                 @RequestParam(required = false) String logout,
                                 Model model) {
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
        model.addAttribute("username", sessionUser.getUsername());
        model.addAttribute("table", "playlists");
        model.addAttribute("updateText", "Обновить плейлист");
        model.addAttribute("id", id);
        if (playlistName == null) {
            HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
            Playlist playlist = template.exchange("http://localhost:8080/api/playlists/" + id,
                    HttpMethod.GET, entity, Playlist.class).getBody();
            model.addAttribute("playlistName", playlist.getPlaylistName());
        }
        if (update != null) {
            PlaylistUpdateRequest request = new PlaylistUpdateRequest();
            request.setPlaylistId(id);
            request.setPlaylistName(playlistName);
            HttpEntity entity = new HttpEntity(request, Helper.setupHeaders(sessionCookie));
            template.exchange("http://localhost:8080/api/playlists/update", HttpMethod.PATCH, entity, Playlist.class);
            return "redirect:/playlists/" + id;
        }
        return "update";
    }

    @RequestMapping("/delete/playlists/{id}")
    public String deletePlaylist(@PathVariable Long id,
                                 @RequestParam(required = false) String delete,
                                 @RequestParam(required = false) String logout,
                                 Model model) {
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
        model.addAttribute("username", sessionUser.getUsername());
        model.addAttribute("table", "playlists");
        model.addAttribute("deleteText", "Подтвердить удаление плейлиста");
        if (delete != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", sessionCookie);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity entity = new HttpEntity(headers);
            template.exchange("http://localhost:8080/api/playlists/delete/" + id,
                    HttpMethod.DELETE, entity, Void.class);
            return "redirect:/";
        } else {
            HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
            Playlist playlist = template.exchange("http://localhost:8080/api/playlists/"
                    + id, HttpMethod.GET, entity, Playlist.class).getBody();
            if (playlist == null)
                return "redirect:/";
            model.addAttribute("playlist", playlist);
        }
        return "delete";
    }


}
