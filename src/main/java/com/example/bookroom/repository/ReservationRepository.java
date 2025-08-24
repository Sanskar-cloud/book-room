package com.example.bookroom.repository;

import com.example.bookroom.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation>findByIdentifier(String identifier);
    List<Reservation> findByOrganizationId(long organizationId);

}
