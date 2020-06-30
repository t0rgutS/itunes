package com.vakhnenko.controller.rest;

import com.vakhnenko.dto.user.UserCreateRequest;
import com.vakhnenko.dto.user.UserResponse;
import com.vakhnenko.dto.user.UserUpdateRequest;
import com.vakhnenko.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @RequestMapping(method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse update(@Valid @RequestBody UserUpdateRequest request) {
        return userService.update(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse view(@PathVariable Long id) {
        return userService.view(id);
    }

    @RequestMapping(value = "/find/{login}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse findUser(@PathVariable String login) {
        return userService.findByLogin(login);
    }
}
