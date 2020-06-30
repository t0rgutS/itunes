package com.vakhnenko.entity;

import com.vakhnenko.constraint.LoginConstraint;
import com.vakhnenko.constraint.NameConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "users")
@Entity
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @LoginConstraint
    @Column(name = "login", unique = true)
    private String login;

    @NotNull
    @NotEmpty
    @Length(min = 5, max = 255, message = "Длина пароля должна быть от 5 до 255 символов!")
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "active")
    private boolean active;

    @NotNull
    @NameConstraint
    @Column(name = "user_name")
    private String username;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Playlist> playlists = new HashSet<>();
}
