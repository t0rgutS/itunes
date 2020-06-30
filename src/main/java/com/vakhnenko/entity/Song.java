package com.vakhnenko.entity;


import com.vakhnenko.constraint.LengthConstraint;
import com.vakhnenko.constraint.BasicNamesConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Table(name = "songs")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Song extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Long songId;

    @NotNull
    @NotEmpty
    @BasicNamesConstraint
    @Column(name = "song_name")
    private String songName;

    @NotNull
    @LengthConstraint
    @Column(name = "song_length")
    private Double songLength;

    @NotNull
    @JoinColumn(name = "album_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Album album;

    @ManyToMany(mappedBy = "songs")
    private Set<Playlist> playlists = new HashSet<>();

}
