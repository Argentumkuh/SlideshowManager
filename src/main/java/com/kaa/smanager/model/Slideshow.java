package com.kaa.smanager.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Slideshow {
    @Id
    private Long id;
    private Integer totalDuration;

    public Slideshow(Long totalDuration) {
        this.totalDuration = Math.toIntExact(totalDuration);
    }
}
