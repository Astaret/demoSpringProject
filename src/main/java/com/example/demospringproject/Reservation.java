package com.example.demospringproject;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;

record Reservation(
        @Null
        Long id,
        @NotNull
        Long userId,
        @NotNull
        Long roomId,
        @FutureOrPresent
        @NotNull
        LocalDate startDate,
        @FutureOrPresent
        @NotNull
        LocalDate endDate,
        ReservationStatus status
) {

}
