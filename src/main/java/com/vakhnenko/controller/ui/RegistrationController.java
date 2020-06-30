package com.vakhnenko.controller.ui;

import com.vakhnenko.dto.user.UserCreateRequest;
import com.vakhnenko.dto.user.UserResponse;
import com.vakhnenko.entity.User;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.exception.NotFoundException;
import com.vakhnenko.utils.BasicRoles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class RegistrationController {
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String newUser(@RequestParam(required = false) String login,
                          @RequestParam(required = false) String password,
                          @RequestParam(required = false) String confirmPassword,
                          @RequestParam(required = false) String username,
                          Model model) {
        try {
            if (login == null || password == null || confirmPassword == null || username == null)
                return "registration";
            if (login.equals("") || password.equals("") || confirmPassword.equals("") || username.equals(""))
                return "registration";
            RestTemplate restTemplate = new RestTemplate();
            UserResponse user = new UserResponse(restTemplate.getForObject("http://localhost:8080/api/users/find/"
                    + login, User.class));
            if (user.getUserId() != null) {
                model.addAttribute("error", "Пользователь " + login + " уже зарегистрирован!");
                model.addAttribute("login", login);
                model.addAttribute("username", username);
                return "registration";
            }
            UserCreateRequest request = new UserCreateRequest();
            request.setLogin(login);
            request.setPassword(new BCryptPasswordEncoder().encode(password));
            request.setUsername(username);
            request.setRole(BasicRoles.USER.getValue());
            request.setActive(true);
            restTemplate.postForObject("http://localhost:8080/api/users/create", request, User.class);
        } catch (BadRequestException bre) {
            model.addAttribute("error", bre.getMessage());
            model.addAttribute("login", login);
            model.addAttribute("username", username);
            return "registration";
        } catch (NotFoundException nfe) {
            model.addAttribute("error", nfe.getMessage());
            model.addAttribute("login", login);
            model.addAttribute("username", username);
            return "registration";
        }
        return "redirect:/";
    }
}
