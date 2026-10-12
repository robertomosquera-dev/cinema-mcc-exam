package com.mitocode.movie.infrastructure.persistence.repository;

import com.mitocode.movie.infrastructure.persistence.entity.MovieJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataMovieRepository extends JpaRepository<MovieJpaEntity, UUID> {
}
