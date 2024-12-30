package com.kaa.smanager.repository;

import com.kaa.smanager.model.Image;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends R2dbcRepository<Image, Long> {
}
