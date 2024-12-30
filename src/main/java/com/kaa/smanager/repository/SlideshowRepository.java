package com.kaa.smanager.repository;

import com.kaa.smanager.model.Slideshow;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideshowRepository extends R2dbcRepository<Slideshow, Long> {
}
