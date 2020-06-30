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
public class AlbumCreateRequest {
    @NotNull
    @BasicNamesConstraint
    private String albumName;

    @NotNull
    @DateConstraint
    private Date albumDate;

    @NotNull
    @BasicNamesConstraint
    private String genre;

    @NotNull
    @BasicNamesConstraint
    private String performerName;
}
