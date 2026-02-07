package com.example.demospringproject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>
{

    @Query("UPDATE ReservationEntity r set r.status = :status where r.id = :id")
    void setStatus(@Param("id") Long id, @Param("status") ReservationStatus reservationStatus);
}
