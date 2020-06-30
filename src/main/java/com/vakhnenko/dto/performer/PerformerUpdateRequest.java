package com.vakhnenko.dto.performer;

import com.vakhnenko.constraint.BasicNamesConstraint;
import com.vakhnenko.entity.Album;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PerformerUpdateRequest {
    @NotNull
    private Long performerId;

    @BasicNamesConstraint
    private String performerName;
}
