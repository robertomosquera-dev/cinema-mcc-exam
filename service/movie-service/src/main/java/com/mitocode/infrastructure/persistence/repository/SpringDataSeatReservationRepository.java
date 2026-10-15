package com.mitocode.infrastructure.persistence.repository;

import com.mitocode.infrastructure.persistence.entity.SeatReservationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataSeatReservationRepository extends JpaRepository<SeatReservationJpaEntity, UUID> {}
