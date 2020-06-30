package com.vakhnenko.repository;

import com.vakhnenko.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long>, JpaSpecificationExecutor<Genre> {
    Optional<Genre> findByGenre(String genre);

    boolean existsByGenre(String genre);
}
