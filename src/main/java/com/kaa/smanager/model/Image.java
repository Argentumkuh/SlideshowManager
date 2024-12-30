package com.kaa.smanager.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Getter
@Setter
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id
    private Long id;
    private String url;
    private Integer duration;
    private Timestamp addedOn;
    private Long slideshowId;
}
