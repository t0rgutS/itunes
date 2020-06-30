package com.vakhnenko.service;

import com.vakhnenko.dto.user.UserCreateRequest;
import com.vakhnenko.dto.user.UserResponse;
import com.vakhnenko.dto.user.UserUpdateRequest;
import com.vakhnenko.entity.Role;
import com.vakhnenko.entity.User;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.repository.RoleRepository;
import com.vakhnenko.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.vakhnenko.utils.Exceptions.ROLE_NOT_FOUND;
import static com.vakhnenko.utils.Exceptions.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRep;
    private final RoleRepository roleRep;

    public UserResponse create(UserCreateRequest request) {
        if (userRep.existsByLogin(request.getLogin()))
            throw new BadRequestException("Логин " + request.getLogin() + " уже занят!");
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setActive(request.isActive());
        user.setUsername(request.getUsername());
        Role role = roleRep.findById((long) request.getRole()).orElseThrow(ROLE_NOT_FOUND);
        user.setRole(role);
        userRep.save(user);
        return new UserResponse(user);
    }

    public UserResponse update(UserUpdateRequest request) {
        User user = userRep.findById(request.getUserId()).orElseThrow(USER_NOT_FOUND);
        if (request.getLogin() != null) {
            if (userRep.existsByLogin(request.getLogin()))
                throw new BadRequestException("Логин " + request.getLogin() + " уже занят!");
            user.setLogin(request.getLogin());
        }
        if (request.getUsername() != null)
            user.setUsername(request.getUsername());
        if (request.getPassword() != null)
            user.setPassword(request.getPassword());
        if (user.isActive())
            user.setActive(request.isActive());
        userRep.save(user);
        return new UserResponse(user);
    }

    public UserResponse view(Long id) {
        User user = userRep.findById(id).orElseThrow(USER_NOT_FOUND);
        return new UserResponse(user);
    }

    public UserResponse findByLogin(String login) {
        User user = userRep.findByLogin(login).orElse(new User());
        return new UserResponse(user);
    }
}
