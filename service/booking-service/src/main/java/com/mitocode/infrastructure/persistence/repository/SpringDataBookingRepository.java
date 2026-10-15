package com.mitocode.infrastructure.persistence.repository;

import com.mitocode.infrastructure.persistence.entity.BookingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataBookingRepository extends JpaRepository<BookingJpaEntity, UUID> {
}
