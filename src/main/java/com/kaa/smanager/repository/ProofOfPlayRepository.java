package com.kaa.smanager.repository;

import com.kaa.smanager.model.ProofOfPlay;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProofOfPlayRepository extends R2dbcRepository<ProofOfPlay, Long> {
}
