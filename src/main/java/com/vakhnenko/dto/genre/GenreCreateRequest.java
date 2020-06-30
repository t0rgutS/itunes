package com.vakhnenko.dto.genre;

import com.vakhnenko.constraint.NameConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class GenreCreateRequest {
    @NotNull
    @NameConstraint
    private String genre;
}
