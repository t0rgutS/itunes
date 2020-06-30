package com.vakhnenko.dto.user;

import com.vakhnenko.constraint.LoginConstraint;
import com.vakhnenko.constraint.NameConstraint;
import com.vakhnenko.entity.Playlist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequest {
    @NotNull
    private Long userId;

    @LoginConstraint
    private String login;

    @NameConstraint
    private String username;

    @Length(min = 5, max = 255, message = "Длина пароля должна быть от 5 до 255 символов!")
    private String password;

    private boolean active;
}
