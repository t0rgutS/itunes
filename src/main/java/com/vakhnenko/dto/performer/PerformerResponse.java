package com.vakhnenko.dto.performer;

import com.vakhnenko.dto.EntityResponse;
import com.vakhnenko.entity.Performer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformerResponse extends EntityResponse {
    private final Long performerId;
    private final String performerName;
    //private final Set<Album> albums;

    public PerformerResponse(Performer performer) {
        super(performer);
        this.performerId = performer.getPerformerId();
        this.performerName = performer.getPerformerName();
        //this.albums = performer.getAlbums();
    }
}
