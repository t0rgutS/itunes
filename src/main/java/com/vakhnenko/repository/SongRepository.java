package com.vakhnenko.repository;

import com.vakhnenko.entity.Album;
import com.vakhnenko.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SongRepository extends JpaRepository<Song, Long>, JpaSpecificationExecutor<Song> {
    boolean existsBySongNameAndAlbum_AlbumName(String songName, String albumName);

    boolean existsBySongNameAndAlbum(String songName, Album album);
}
