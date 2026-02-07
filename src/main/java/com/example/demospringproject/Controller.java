package com.example.demospringproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reservation")
public class Controller {
    private final ReservationService service;
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    public Controller(ReservationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") Long id
    ) {
        log.info("called getReservationById: id = " + id);
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    service.getReservationById(id)
            );
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        log.info("called getAllReservations");
        return ResponseEntity.ok(
                service.findAllReservations()
        );
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestBody Reservation reservationToCreate
    ) {
        log.info("called create reservation method invoked");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createReservation(reservationToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") Long id,
            @RequestBody Reservation reservationToUpdate
    ){
        log.info("Update called with id = {}, reservation = {}",id, reservationToUpdate );
        var updated = service.updateReservation(id, reservationToUpdate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeReservation(
            @PathVariable("id") Long id
    ){
        log.info("Delete called with id = {}", id);
        try {
            service.cancelReservation(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e){
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable("id") Long id
    ){
        log.info("Called approveReservation id={}", id);
        var reservation = service.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }
}
