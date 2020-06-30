package com.vakhnenko.controller.ui;

import com.vakhnenko.dto.performer.PerformerCreateRequest;
import com.vakhnenko.dto.performer.PerformerUpdateRequest;
import com.vakhnenko.entity.Album;
import com.vakhnenko.entity.Performer;
import com.vakhnenko.entity.Song;
import com.vakhnenko.entity.User;
import com.vakhnenko.utils.Helper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class UIPerformerController {
    @Resource(name = "sessionUser")
    private User sessionUser;

    @RequestMapping("/performers")
    public String viewPerformers(@RequestParam(required = false) String pageBack,
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
        if (sessionUser.getRole().getRole().toUpperCase().equals("ADMIN"))
            model.addAttribute("access", true);
        model.addAttribute("username", sessionUser.getUsername());
        int page = Helper.setupPage(pageBack, pageForward);
        model.addAttribute("currentPage", (page + 1));
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/performers")
                .queryParam("page", page);
        String redirectAttr;
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
        HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
        ResponseEntity<PagedModel<Performer>> performers = template
                .exchange(uriBuilder.toUriString(), HttpMethod.GET, entity,
                        new ParameterizedTypeReference<PagedModel<Performer>>() {
                        });
        model.addAttribute("performers", performers.getBody());
        return "performers";
    }

    @RequestMapping("/performers/{id}")
    public String viewSinglePerformer(@PathVariable Long id,
                                      @RequestParam(required = false) String showAlbums,
                                      @RequestParam(required = false) String showSongs,
                                      @RequestParam(required = false) String pageBack,
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
        if (sessionUser.getRole().getRole().toUpperCase().equals("ADMIN"))
            model.addAttribute("access", true);
        model.addAttribute("username", sessionUser.getUsername());
        int page = Helper.setupPage(pageBack, pageForward);
        model.addAttribute("currentPage", (page + 1));
        boolean albumIndicator;
        if (model.asMap().get("showAlbums") != null)
            albumIndicator = (Boolean) model.asMap().get("showAlbums");
        else if (showAlbums == null && showSongs == null)
            albumIndicator = true;
        else {
            if (showAlbums == null)
                albumIndicator = false;
            else
                albumIndicator = true;
        }
        model.addAttribute("albumIndicator", albumIndicator);
        HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
        Performer performer = template.exchange("http://localhost:8080/api/performers/" + id,
                HttpMethod.GET, entity, Performer.class).getBody();
        model.addAttribute("performer", performer);
        if (albumIndicator) {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/albums")
                    .queryParam("performerId", performer.getPerformerId())
                    .queryParam("page", page);
            if (model.asMap().get("songName") != null)
                uriBuilder.queryParam("songName", (String) model.asMap().get("songName"));
            if (model.asMap().get("albumName") != null)
                uriBuilder.queryParam("albumName", (String) model.asMap().get("albumName"));
            if (model.asMap().get("genre") != null)
                uriBuilder.queryParam("genre", (String) model.asMap().get("genre"));
            if (model.asMap().get("albumDate") != null)
                uriBuilder.queryParam("albumDate", (String) model.asMap().get("albumDate"));
            ResponseEntity<PagedModel<Album>> albums = template
                    .exchange(uriBuilder.toUriString(), HttpMethod.GET, entity,
                            new ParameterizedTypeReference<PagedModel<Album>>() {
                            });
            model.addAttribute("albums", albums.getBody());
        } else {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/songs")
                    .queryParam("performerId", performer.getPerformerId())
                    .queryParam("page", page);
            if (model.asMap().get("playlistName") != null)
                uriBuilder.queryParam("playlistName", (String) model.asMap().get("playlistName"));
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
        }
        return "singlePerformer";
    }

    @RequestMapping("/search/performers")
    public String searchPerformers(@RequestParam(required = false) String performerName,
                                   @RequestParam(required = false) String songName,
                                   @RequestParam(required = false) String albumName,
                                   @RequestParam(required = false) String genre,
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
            redirect.addFlashAttribute("performerName", performerName);
            redirect.addFlashAttribute("songName", songName);
            redirect.addFlashAttribute("albumName", albumName);
            redirect.addFlashAttribute("genre", genre);
            return "redirect:/performers";
        }
        model.addAttribute("table", "performers");
        model.addAttribute("searchText", "Поиск исполнителей");
        return "search";
    }

    @RequestMapping("/add/performers")
    @PreAuthorize("hasRole('ADMIN')")
    public String addPerformer(@RequestParam(required = false) String performerName,
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
        model.addAttribute("table", "performers");
        model.addAttribute("addText", "Добавить исполнителя");
        if (add != null) {
            PerformerCreateRequest request = new PerformerCreateRequest();
            request.setPerformerName(performerName);
            HttpEntity entity = new HttpEntity(request, Helper.setupHeaders(sessionCookie));
            template.exchange("http://localhost:8080/api/performers/add", HttpMethod.POST, entity, Performer.class);
            return "redirect:/performers";
        }
        return "add";
    }

    @RequestMapping(value = "/update/performers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updatePerformer(@PathVariable Long id,
                                  @RequestParam(required = false) String performerName,
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
        model.addAttribute("table", "performers");
        model.addAttribute("updateText", "Обновить данные исполнителя");
        model.addAttribute("id", id);
        if (performerName == null) {
            HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
            Performer performer = template.exchange("http://localhost:8080/api/performers/" + id,
                    HttpMethod.GET, entity, Performer.class).getBody();
            model.addAttribute("performerName", performer.getPerformerName());
        }
        if (update != null) {
            PerformerUpdateRequest request = new PerformerUpdateRequest();
            request.setPerformerName(performerName);
            request.setPerformerId(id);
            HttpEntity entity = new HttpEntity(request, Helper.setupHeaders(sessionCookie));
            template.exchange("http://localhost:8080/api/performers/update", HttpMethod.PATCH, entity,
                    Performer.class);
            return "redirect:/performers/" + id;
        }
        return "update";
    }

    @RequestMapping("/delete/performers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePerformer(@PathVariable Long id,
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
        model.addAttribute("table", "performers");
        model.addAttribute("deleteText", "Подтвердить удаление исполнителя");
        if (delete != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", sessionCookie);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity entity = new HttpEntity(headers);
            template.delete("http://localhost:8080/api/performers/delete/" + id, entity);
            return "redirect:/performers";
        } else {
            HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
            Performer performer = template.exchange("http://localhost:8080/api/performers/" + id, HttpMethod.GET, entity,
                    Performer.class).getBody();
            if (performer == null)
                return "redirect:/performers";
            model.addAttribute("performer", performer);
        }
        return "delete";
    }
}
