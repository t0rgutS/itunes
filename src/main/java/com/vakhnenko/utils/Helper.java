package com.vakhnenko.utils;

import com.vakhnenko.entity.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class Helper {
    public static HttpHeaders setupHeaders(String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionCookie);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public static int setupPage(String pageBack, String pageForward) {
        int page;
        if (pageBack == null && pageForward == null) {
            return 0;
        } else {
            if (pageBack != null) {
                if (!pageBack.equals("")) {
                    page = Integer.parseInt(pageBack);
                    if (page > 1)
                        page--;
                } else return 0;
            } else {
                if (!pageForward.equals("")) {
                    page = Integer.parseInt(pageForward);
                    page++;
                } else return 0;
            }
        }
        return --page;
    }

    public static User getSessionUserData(RestTemplate template, String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionCookie);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new HttpEntity(headers);
        return template.exchange("http://localhost:8080/api/users/find/" + ((UserDetails)
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername(),
                HttpMethod.GET, entity, User.class).getBody();
    }
}
