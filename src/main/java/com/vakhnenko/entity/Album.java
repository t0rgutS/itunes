package com.vakhnenko.entity;

import com.vakhnenko.constraint.DateConstraint;
import com.vakhnenko.constraint.BasicNamesConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Table(name = "albums")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Album extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private Long albumId;

    @NotNull
    @NotEmpty
    @BasicNamesConstraint
    @Column(name = "album_name")
    private String albumName;

    @NotNull
    @Column(name = "album_date")
    @Temporal(TemporalType.DATE)
    @DateConstraint
    private Date albumDate;

    @NotNull
    @JoinColumn(name = "genre_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Genre genre;

    @NotNull
    @JoinColumn(name = "performer_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Performer performer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "album")
    private Set<Song> songs;

    public String getFormattedDate() {
        if (albumDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(albumDate);
        } else return null;
    }

}
