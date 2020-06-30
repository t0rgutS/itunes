package com.vakhnenko.repository;

import com.vakhnenko.entity.Performer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PerformerRepository extends JpaRepository<Performer, Long>, JpaSpecificationExecutor<Performer> {
    Optional<Performer> findByPerformerName(String performerName);

    boolean existsByPerformerName(String performerName);
}
