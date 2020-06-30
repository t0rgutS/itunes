package com.vakhnenko.dto.playlist;

import com.vakhnenko.constraint.BasicNamesConstraint;
import com.vakhnenko.entity.Song;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PlaylistUpdateRequest {
    @NotNull
    private Long playlistId;

    @BasicNamesConstraint
    private String playlistName;
}
