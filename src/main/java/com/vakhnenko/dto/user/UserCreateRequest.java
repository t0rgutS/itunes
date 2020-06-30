package com.vakhnenko.dto.user;

import com.vakhnenko.constraint.LoginConstraint;
import com.vakhnenko.constraint.NameConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UserCreateRequest {
    @NotNull
    @LoginConstraint
    private String login;

    @NotNull
    @NameConstraint
    private String username;

    @NotNull
    @Length(min = 5, max = 255, message = "Длина пароля должна быть от 5 до 255 символов!")
    private String password;

    @NotNull
    private boolean active;

    @NotNull
    private short role;
}
