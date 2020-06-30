package com.vakhnenko.dto.performer;

import com.vakhnenko.constraint.BasicNamesConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PerformerCreateRequest {
    @NotNull
    @BasicNamesConstraint
    private String performerName;
}
