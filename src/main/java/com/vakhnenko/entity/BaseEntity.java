package com.vakhnenko.entity;

import com.vakhnenko.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
   // @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
   // private Long id;
    private Long createdAt;
    private Long updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = DateUtils.nowUnix();

        if (this.updatedAt == null)
            this.updatedAt = DateUtils.nowUnix();
    }

    public LocalDateTime creationDate(){
        return DateUtils.fromUnix(this.getCreatedAt());
    }

    public LocalDateTime updateDate(){
        return DateUtils.fromUnix(this.getUpdatedAt());
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = DateUtils.nowUnix();
    }
}
