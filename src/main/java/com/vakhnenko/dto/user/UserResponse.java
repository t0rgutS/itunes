package com.vakhnenko.dto.user;

import com.vakhnenko.dto.EntityResponse;
import com.vakhnenko.entity.Role;
import com.vakhnenko.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse extends EntityResponse {
    private final Long userId;
    private final String login;
    private final String username;
    private final Role role;
    private final boolean active;
    //private final Set<Playlist> playlists;

    public UserResponse(User user) {
        super(user);
        this.userId = user.getUserId();
        this.login = user.getLogin();
        this.username = user.getUsername();
        this.active = user.isActive();
        this.role = user.getRole();
        //this.playlists = user.getPlaylists();
    }
}
