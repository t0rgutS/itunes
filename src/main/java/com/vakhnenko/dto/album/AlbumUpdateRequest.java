package com.vakhnenko.dto.album;

import com.vakhnenko.constraint.BasicNamesConstraint;
import com.vakhnenko.constraint.DateConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AlbumUpdateRequest {
    @NotNull
    private Long albumId;

    @BasicNamesConstraint
    private String albumName;

    @DateConstraint
    private Date albumDate;

    @BasicNamesConstraint
    private String performerName;

    @BasicNamesConstraint
    private String genre;
}
