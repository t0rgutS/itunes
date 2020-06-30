package com.vakhnenko.dto.song;


import com.vakhnenko.constraint.LengthConstraint;
import com.vakhnenko.constraint.BasicNamesConstraint;
import com.vakhnenko.entity.Album;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SongCreateRequest {
    @NotNull
    @BasicNamesConstraint
    private String songName;

    @NotNull
    @LengthConstraint
    private Double songLength;

    @NotNull
    private String albumName;

    @NotNull
    private String performerName;
}
