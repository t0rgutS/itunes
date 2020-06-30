package com.vakhnenko.repository;

import com.vakhnenko.entity.Album;
import com.vakhnenko.entity.Performer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long>, JpaSpecificationExecutor<Album> {
    Optional<Album> findByAlbumNameAndPerformer(String albumName, Performer performer);

    Optional<Album> findByAlbumNameAndPerformer_PerformerName(String albumName, String performerName);

    boolean existsByAlbumNameAndPerformer_PerformerName(String albumName, String performerName);

    boolean existsByAlbumNameAndPerformer(String albumName, Performer performer);
}