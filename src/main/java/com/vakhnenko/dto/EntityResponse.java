package com.vakhnenko.dto;

import com.vakhnenko.entity.BaseEntity;
import com.vakhnenko.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class EntityResponse {
   // private final Long id;
    private final Long createdAt;
    private final Long updatedAt;

    public EntityResponse(BaseEntity entity) {
        //this.id = entity.getId();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    public LocalDateTime creationDate(){
        return DateUtils.fromUnix(this.getCreatedAt());
    }

    public LocalDateTime updateDate(){
        return DateUtils.fromUnix(this.getUpdatedAt());
    }
}
