package com.vakhnenko.entity;

import com.vakhnenko.constraint.BasicNamesConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Table(name = "performers")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Performer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performer_id")
    private Long performerId;

    @NotNull
    @NotEmpty
    @BasicNamesConstraint
    @Column(name = "performer_name", unique = true)
    private String performerName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "performer")
    private Set<Album> albums = new HashSet<>();

}
