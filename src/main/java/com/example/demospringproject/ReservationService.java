package com.example.demospringproject;

import com.example.demospringproject.database.ReservationEntity;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservationService {

    private final ReservationRepository repository;

    AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext("com.example.demospringproject");
    SessionFactory factory = context.getBean(SessionFactory.class);
    private final Session session = factory.openSession();

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation getReservationById(Long id) {

        ReservationEntity reservationEntity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found by id " + id)
        );

        return toDomainReservation(reservationEntity);
    }

    public List<Reservation> findAllReservations() {

        List<ReservationEntity> allEntities = repository.findAll();

        return allEntities.stream().map(this::toDomainReservation).toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (!reservationToCreate.endDate().isAfter(reservationToCreate.startDate())) {
            throw new IllegalArgumentException("Start date must be earlier than end date");
        }
        if (reservationToCreate.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );
        var savedEntity = repository.save(entityToSave);
        return toDomainReservation(savedEntity);
    }

    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {

        var reservationEntity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found id " + id)
        );

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot modify reservation " + reservationEntity.getStatus());
        }
        if (!reservationToUpdate.endDate().isAfter(reservationToUpdate.startDate())) {
            throw new IllegalArgumentException("Start date must be earlier than end date");
        }

        var reservationToSave = new ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );

        var updatedReservation = repository.save(reservationToSave);

        return toDomainReservation(updatedReservation);
    }

    public void cancelReservation(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Not found id " + id);
        }
        var reservation = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found reservation" + id));
        if (reservation.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new IllegalStateException("Cannot cancel approved reservation. Contact with manager");
        }
        if (reservation.getStatus().equals(ReservationStatus.CANCELLED)) {
            throw new IllegalArgumentException("Cannot cancel already cancelled reservation.");
        }
        repository.setStatus(id, ReservationStatus.CANCELLED);
    }


    public Reservation approveReservation(Long id) {

        var reservationEntity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found id " + id)
        );

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot approve reservation " + reservationEntity.getStatus());
        }

        var isConflict = isReservationConflict(reservationEntity);

        if (isConflict) {
            throw new IllegalStateException("Cannot approve reservation because of conflict");
        }

        reservationEntity.setStatus(ReservationStatus.APPROVED);

        repository.save(reservationEntity);

        return toDomainReservation(reservationEntity);
    }

    private boolean isReservationConflict(ReservationEntity reservation) {

        var allReservations = repository.findAll();

        for (ReservationEntity existingReservation : allReservations) {
            if (reservation.getId().equals(existingReservation.getId())) {
                continue;
            }
            if (!reservation.getRoomId().equals(existingReservation.getRoomId())) {
                continue;
            }
            if (!existingReservation.getStatus().equals(ReservationStatus.APPROVED)) {
                continue;
            }
            if (reservation.getStartDate().isBefore(existingReservation.getEndDate())
                    && existingReservation.getStartDate().isBefore(reservation.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    private Reservation toDomainReservation(
            ReservationEntity reservationEntity
    ) {
        return new Reservation(
                reservationEntity.getId(),
                reservationEntity.getUserId(),
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate(),
                reservationEntity.getStatus()
        );
    }
}
