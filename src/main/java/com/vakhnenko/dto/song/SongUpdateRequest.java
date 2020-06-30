package com.vakhnenko.dto.song;

import com.vakhnenko.constraint.BasicNamesConstraint;
import com.vakhnenko.constraint.LengthConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SongUpdateRequest {
    @NotNull
    private Long songId;

    @BasicNamesConstraint
    private String songName;

    @LengthConstraint
    private Double songLength;

    @BasicNamesConstraint
    private String albumName;

    @BasicNamesConstraint
    private String performerName;

    private Long playlistId;
}
