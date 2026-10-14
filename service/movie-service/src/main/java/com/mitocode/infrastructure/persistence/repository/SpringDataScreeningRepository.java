package com.mitocode.infrastructure.persistence.repository;

import com.mitocode.infrastructure.persistence.entity.ScreeningJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataScreeningRepository extends JpaRepository<ScreeningJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ScreeningJpaEntity s where s.id = :screeningId")
    Optional<ScreeningJpaEntity> findByIdForUpdate(@Param("screeningId") UUID screeningId);

    boolean existsByMovie_Id(UUID movieId);
}
