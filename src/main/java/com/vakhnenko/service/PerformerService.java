package com.vakhnenko.service;

import com.vakhnenko.dto.performer.PerformerCreateRequest;
import com.vakhnenko.dto.performer.PerformerResponse;
import com.vakhnenko.dto.performer.PerformerSearchRequest;
import com.vakhnenko.dto.performer.PerformerUpdateRequest;
import com.vakhnenko.entity.Album;
import com.vakhnenko.entity.Performer;
import com.vakhnenko.entity.Song;
import com.vakhnenko.exception.BadRequestException;
import com.vakhnenko.repository.PerformerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static com.vakhnenko.utils.Exceptions.PERFORMER_NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class PerformerService {
    @Autowired
    private EntityManager manager;

    private final PerformerRepository performerRep;

    public PerformerResponse create(PerformerCreateRequest request) {
        if (performerRep.existsByPerformerName(request.getPerformerName()))
            throw new BadRequestException("Исполнитель " + request.getPerformerName() + " уже добавлен!");
        Performer performer = new Performer();
        performer.setPerformerName(request.getPerformerName());
        performerRep.save(performer);
        return new PerformerResponse(performer);
    }

    public PerformerResponse update(PerformerUpdateRequest request) {
        Performer performer = performerRep.findById(request.getPerformerId()).orElseThrow(PERFORMER_NOT_FOUND);
        if (request.getPerformerName() != null) {
            if (!performer.getPerformerName().equals(request.getPerformerName())) {
                if (performerRep.existsByPerformerName(request.getPerformerName()))
                    throw new BadRequestException("Исполнитель " + request.getPerformerName() + " уже добавлен!");
                performer.setPerformerName(request.getPerformerName());
            }
        }
        performerRep.save(performer);
        return new PerformerResponse(performer);
    }

    public Page<Performer> search(PerformerSearchRequest request) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Performer> query = builder.createQuery(Performer.class);
        Root<Performer> root = query.from(Performer.class);
        Predicate basePredicate = null;
        if (request.getSongName() != null) {
            Join<Performer, Album> albumJoin = root.join("albums");
            Join<Album, Song> songJoin = albumJoin.join("songs");
            basePredicate = builder.like(songJoin.get("songName"), "%" + request.getSongName() + "%");
        }
        if (request.getAlbumName() != null) {
            Join<Performer, Album> join = root.join("albums");
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.like(join.get("albumName"),
                        "%" + request.getAlbumName() + "%"));
            else
                basePredicate = builder.like(join.get("albumName"), "%" + request.getAlbumName() + "%");
        }
        if (request.getPerformerName() != null) {
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.like(root.get("performerName"),
                        "%" + request.getPerformerName() + "%"));
            else
                basePredicate = builder.like(root.get("performerName"), "%" + request.getPerformerName() + "%");
        }
        if (request.getGenre() != null) {
            Join<Performer, Album> join = root.join("albums");
            if (basePredicate != null)
                basePredicate = builder.and(basePredicate, builder.like(join.get("genre").get("genre"),
                        "%" + request.getGenre() + "%"));
            else
                basePredicate = builder.like(join.get("genre").get("genre"),
                        "%" + request.getGenre() + "%");
        }
        if (basePredicate != null)
            query.where(basePredicate);
        TypedQuery<Performer> typedQuery = manager.createQuery(query);
        typedQuery.setFirstResult(request.getPage() * request.getSize());
        typedQuery.setMaxResults(request.getPage() * request.getSize() + request.getSize());
        List<Performer> performers = typedQuery.getResultList();
        return new PageImpl<Performer>(performers, request.pageable(), performers.size());
    }

    public PerformerResponse view(Long id) {
        Performer performer = performerRep.findById(id).orElseThrow(PERFORMER_NOT_FOUND);
        return new PerformerResponse(performer);
    }

    public void delete(Long id) {
        performerRep.deleteById(id);
    }

}
