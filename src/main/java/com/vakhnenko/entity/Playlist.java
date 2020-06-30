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

@Table(name = "playlists")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Playlist extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Long playlistId;

    @NotNull
    @NotEmpty
    @BasicNamesConstraint
    @Column(name = "playlist_name", columnDefinition = "varchar default 'Новый плейлист'")
    private String playlistName;

    @NotNull
    @NotEmpty
    @JoinColumn(name = "author_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User author;

    @ManyToMany
    @JoinTable(
            name = "songs_by_playlists",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id"))
    private Set<Song> songs = new HashSet<>();

}
