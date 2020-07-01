package com.vakhnenko.controller.ui;

import com.vakhnenko.dto.album.AlbumCreateRequest;
import com.vakhnenko.dto.album.AlbumUpdateRequest;
import com.vakhnenko.entity.Album;
import com.vakhnenko.entity.Song;
import com.vakhnenko.entity.User;
import com.vakhnenko.utils.Helper;
import com.vakhnenko.utils.PreviousPage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

@Controller
public class UIAlbumController {
    @Resource(name = "sessionPreviousPage")
    private PreviousPage previous;

    @Resource(name = "sessionUser")
    private User sessionUser;

    @RequestMapping("/albums")
    public String viewAlbums(@RequestParam(required = false) String pageBack,
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
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/albums")
                    .queryParam("page", page);
            String redirectAttr;
            if (model.asMap().get("albumName") != null) {
                redirectAttr = (String) model.asMap().get("albumName");
                if (!redirectAttr.equals(""))
                    uriBuilder.queryParam("albumName", redirectAttr);
            }
            if (model.asMap().get("albumDate") != null) {
                redirectAttr = (String) model.asMap().get("albumDate");
                if (!redirectAttr.equals(""))
                    uriBuilder.queryParam("albumDate", redirectAttr);
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
            ResponseEntity<PagedModel<Album>> albums = template
                    .exchange(uriBuilder.toUriString(), HttpMethod.GET, entity,
                            new ParameterizedTypeReference<PagedModel<Album>>() {
                            });
            model.addAttribute("albums", albums.getBody());
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
        return "albums";
    }

    @RequestMapping("/albums/{id}")
    public String viewSingleAlbum(@PathVariable Long id,
                                  @RequestParam(required = false) String pageBack,
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
            HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
            Album album = template.exchange("http://localhost:8080/api/albums/" + id,
                    HttpMethod.GET, entity, Album.class).getBody();
            model.addAttribute("album", album);
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/songs")
                    .queryParam("albumId", album.getAlbumId())
                    .queryParam("page", page);
            if (model.asMap().get("playlistName") != null)
                uriBuilder.queryParam("playlistName", (String) model.asMap().get("playlistName"));
            if (model.asMap().get("songName") != null)
                uriBuilder.queryParam("songName", (String) model.asMap().get("songName"));
            if (model.asMap().get("genre") != null)
                uriBuilder.queryParam("genre", (String) model.asMap().get("genre"));
            if (model.asMap().get("songLength") != null)
                uriBuilder.queryParam("songLength", (String) model.asMap().get("songLength"));
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
        return "singleAlbum";
    }

    @RequestMapping("/search/albums")
    public String searchAlbums(@RequestParam(required = false) String performerName,
                               @RequestParam(required = false) String songName,
                               @RequestParam(required = false) String albumName,
                               @RequestParam(required = false) String albumDate,
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
        model.addAttribute("username", sessionUser.getUsername());
        if (previous.getPreviousPage() == null)
            if (httpRequest.getHeader("Referer") != null)
                if (!httpRequest.getHeader("Referer").contains("/search/albums"))
                    previous.setPreviousPage(httpRequest.getHeader("Referer"));
        if (performerName != null)
            model.addAttribute("performerName", performerName);
        if (startSearch != null) {
            redirect.addFlashAttribute("performerName", performerName);
            redirect.addFlashAttribute("songName", songName);
            redirect.addFlashAttribute("albumName", albumName);
            redirect.addFlashAttribute("albumDate", albumDate);
            redirect.addFlashAttribute("genre", genre);
            redirect.addFlashAttribute("showAlbums", true);
            if (previous.getPreviousPage() != null) {
                if (!previous.getPreviousPage().equals("")) {
                    String redirectAddr = "redirect:" + previous.getPreviousPage();
                    previous.setPreviousPage(null);
                    return redirectAddr;
                }
            }
            return "redirect:/albums";
        }
        model.addAttribute("table", "albums");
        model.addAttribute("searchText", "Поиск альбомов");
        return "search";
    }

    @RequestMapping("/add/albums")
    public String addAlbum(@RequestParam(required = false) String albumDate,
                           @RequestParam(required = false) String genre,
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
            model.addAttribute("username", sessionUser.getUsername());
            if (previous.getPreviousPage() == null)
                if (httpRequest.getHeader("Referer") != null)
                    if (!httpRequest.getHeader("Referer").contains("/add/albums"))
                        previous.setPreviousPage(httpRequest.getHeader("Referer"));
            model.addAttribute("table", "albums");
            model.addAttribute("addText", "Добавить альбом");
            if (add != null) {
                try {
                    AlbumCreateRequest request = new AlbumCreateRequest();
                    request.setAlbumName(albumName);
                    request.setAlbumDate(new SimpleDateFormat("yyyy-MM-dd").parse(albumDate));
                    request.setGenre(genre);
                    request.setPerformerName(performerName);
                    HttpEntity entity = new HttpEntity(request, Helper.setupHeaders(sessionCookie));
                    template.exchange("http://localhost:8080/api/albums/add", HttpMethod.POST, entity, Album.class);
                    if (previous.getPreviousPage() != null) {
                        if (!previous.getPreviousPage().equals("")) {
                            String redirectAddr = "redirect:" + previous.getPreviousPage();
                            previous.setPreviousPage(null);
                            return redirectAddr;
                        }
                    }
                    return "redirect:/albums";
                } catch (ParseException e) {
                    model.addAttribute("albumName", albumName);
                    model.addAttribute("albumDate", albumDate);
                    model.addAttribute("performerName", performerName);
                    model.addAttribute("genre", genre);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            model.addAttribute("albumName", albumName);
            model.addAttribute("albumDate", albumDate);
            model.addAttribute("performerName", performerName);
            model.addAttribute("genre", genre);
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

    @RequestMapping("/update/albums/{id}")
    public String updateAlbum(@PathVariable Long id,
                              @RequestParam(required = false) String albumDate,
                              @RequestParam(required = false) String genre,
                              @RequestParam(required = false) String albumName,
                              @RequestParam(required = false) String performerName,
                              @RequestParam(required = false) String update,
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
            model.addAttribute("username", sessionUser.getUsername());
            if (previous.getPreviousPage() == null)
                if (httpRequest.getHeader("Referer") != null)
                    if (!httpRequest.getHeader("Referer").contains("/update/albums"))
                        previous.setPreviousPage(httpRequest.getHeader("Referer"));
            model.addAttribute("table", "albums");
            model.addAttribute("updateText", "Обновить данные альбома");
            model.addAttribute("id", id);
            if (albumName == null) {
                HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
                Album album = template.exchange("http://localhost:8080/api/albums/" + id,
                        HttpMethod.GET, entity, Album.class).getBody();
                model.addAttribute("albumName", album.getAlbumName());
                model.addAttribute("albumDate", album.getAlbumDate());
                model.addAttribute("performerName", album.getPerformer().getPerformerName());
                model.addAttribute("genre", album.getGenre().getGenre());
            }
            if (update != null) {
                try {
                    AlbumUpdateRequest request = new AlbumUpdateRequest();
                    request.setAlbumName(albumName);
                    request.setAlbumDate(new SimpleDateFormat("yyyy-MM-dd").parse(albumDate));
                    request.setPerformerName(performerName);
                    request.setGenre(genre);
                    request.setAlbumId(id);
                    HttpEntity entity = new HttpEntity(request, Helper.setupHeaders(sessionCookie));
                    template.exchange("http://localhost:8080/api/albums/update", HttpMethod.PATCH,
                            entity, Album.class);
                    if (previous.getPreviousPage() != null) {
                        if (!previous.getPreviousPage().equals("")) {
                            String redirectAddr = "redirect:" + previous.getPreviousPage();
                            previous.setPreviousPage(null);
                            return redirectAddr;
                        }
                    }
                    return "redirect:/albums/" + id;
                } catch (ParseException e) {
                    model.addAttribute("albumName", albumName);
                    model.addAttribute("albumDate", albumDate);
                    model.addAttribute("performerName", performerName);
                    model.addAttribute("genre", genre);
                    model.addAttribute("error", "Не удалось получить дату релиза!");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            model.addAttribute("albumName", albumName);
            model.addAttribute("albumDate", albumDate);
            model.addAttribute("performerName", performerName);
            model.addAttribute("genre", genre);
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

    @RequestMapping("/delete/albums/{id}")
    public String deleteAlbum(@PathVariable Long id,
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
            model.addAttribute("username", sessionUser.getUsername());
            if (previous.getPreviousPage() == null)
                if (httpRequest.getHeader("Referer") != null)
                    if (!httpRequest.getHeader("Referer").contains("/delete/albums"))
                        previous.setPreviousPage(httpRequest.getHeader("Referer"));
            model.addAttribute("table", "albums");
            model.addAttribute("deleteText", "Подтвердить удаление альбома");
            if (delete != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Cookie", sessionCookie);
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity entity = new HttpEntity(headers);
                template.exchange("http://localhost:8080/api/albums/delete/" + id, HttpMethod.DELETE, entity, Void.class);
                if (previous.getPreviousPage() != null) {
                    if (!previous.getPreviousPage().equals("")) {
                        String redirectAddr = "redirect:" + previous.getPreviousPage();
                        previous.setPreviousPage(null);
                        return redirectAddr;
                    }
                }
                return "redirect:/albums";
            } else {
                HttpEntity entity = new HttpEntity(Helper.setupHeaders(sessionCookie));
                Album album = template.exchange("http://localhost:8080/api/albums/" + id, HttpMethod.GET, entity,
                        Album.class).getBody();
                if (album == null) {
                    if (previous.getPreviousPage() != null) {
                        if (!previous.getPreviousPage().equals("")) {
                            String redirectAddr = "redirect:" + previous.getPreviousPage();
                            previous.setPreviousPage(null);
                            return redirectAddr;
                        }
                    }
                    return "redirect:/albums";
                }
                model.addAttribute("album", album);
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
