package com.my.rental.repository;

import com.my.rental.domain.Rental;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Rental entity.
 */
@SuppressWarnings("unused")
//Rental 엔티티의 리포지토리 인터페이스
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByUserId(Long userId);
}
