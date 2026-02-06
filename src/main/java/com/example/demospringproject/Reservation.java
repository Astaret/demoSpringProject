package com.example.demospringproject;

import java.time.LocalDate;

record Reservation(
        Long id,
        Long userId,
        Long roomId,
        LocalDate startDate,
        LocalDate endDate,
        ReservationStatus status
) {

}
