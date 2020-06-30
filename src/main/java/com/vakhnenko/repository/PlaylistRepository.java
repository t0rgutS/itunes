package com.vakhnenko.repository;

import com.vakhnenko.dto.playlist.PlaylistSearchRequest;
import com.vakhnenko.entity.Playlist;
import com.vakhnenko.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long>, JpaSpecificationExecutor<Playlist> {
    Optional<Playlist> findByPlaylistNameAndAuthor_Login(String playlistName, String login);

    boolean existsByPlaylistNameAndAuthor_Login(String playlistName, String login);

    boolean existsByPlaylistNameAndAuthor(String playlistName, User author);
}
