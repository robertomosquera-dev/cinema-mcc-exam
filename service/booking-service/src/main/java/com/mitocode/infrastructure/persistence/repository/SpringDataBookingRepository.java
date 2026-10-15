package com.mitocode.infrastructure.persistence.repository;

import com.mitocode.domain.model.BookingStatus;
import com.mitocode.infrastructure.persistence.entity.BookingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataBookingRepository extends JpaRepository<BookingJpaEntity, UUID> {
    @Transactional(readOnly = true)
    List<BookingJpaEntity> findByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(BookingStatus status, Instant cutoff);
}
