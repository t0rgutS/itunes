package com.vakhnenko.entity;

import com.vakhnenko.constraint.NameConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Table(name = "genres")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Genre extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Long genreId;

    @NotNull
    @NameConstraint
    @Column(name = "genre")
    private String genre;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "genre")
    private Set<Album> albums;
}
