package com.vakhnenko.dto.playlist;

import com.vakhnenko.constraint.BasicNamesConstraint;
import com.vakhnenko.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PlaylistCreateRequest {
    @NotNull
    @BasicNamesConstraint
    private String playlistName;

    @NotNull
    @BasicNamesConstraint
    private String author;
}
