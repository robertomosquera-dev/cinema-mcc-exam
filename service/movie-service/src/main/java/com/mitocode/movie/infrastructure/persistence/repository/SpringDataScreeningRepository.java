package com.mitocode.movie.infrastructure.persistence.repository;

import com.mitocode.movie.infrastructure.persistence.entity.ScreeningJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataScreeningRepository extends JpaRepository<ScreeningJpaEntity, UUID> {
}
