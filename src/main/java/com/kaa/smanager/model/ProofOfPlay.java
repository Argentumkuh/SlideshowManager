package com.kaa.smanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Getter
@Setter
@Table
@AllArgsConstructor
@NoArgsConstructor
public class ProofOfPlay {
    @Id
    private Long id;
    private Long imageId;
    private Long slideshowId;
    private Timestamp playedOn;

    public ProofOfPlay(Long slideshowId, Long imageId, Timestamp playedOn) {
        this.slideshowId = slideshowId;
        this.imageId = imageId;
        this.playedOn = playedOn;
    }
}
