package com.example.bookroom.service;

import com.example.bookroom.model.Organization;
import com.example.bookroom.model.Reservation;
import com.example.bookroom.model.DTOs.ReservationDTO;
import com.example.bookroom.model.Room;
import com.example.bookroom.repository.OrganizationRepository;
import com.example.bookroom.repository.ReservationRepository;
import com.example.bookroom.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    OrganizationRepository organizationRepository;


    public ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setId(reservation.getId());
        reservationDTO.setIdentifier(reservation.getIdentifier());
        reservationDTO.setDate(reservation.getDate());
        reservationDTO.setStartTime(reservation.getStartTime());
        reservationDTO.setEndTime(reservation.getEndTime());
        reservationDTO.setRoomName(reservation.getRoom().getName());
        reservationDTO.setOrganizationName(reservation.getOrganization().getName());
        return reservationDTO;
    }

    public List<ReservationDTO> listReservations() {
        List<Reservation> allReservations = reservationRepository.findAll();
        List<ReservationDTO> processedReservations = new ArrayList<>();
        for (Reservation reservation : allReservations) {
            ReservationDTO reservationDTO = convertToDTO(reservation);
            processedReservations.add(reservationDTO);
        }
        return processedReservations;
    }

    public Optional<Reservation> getReservationById(long id) {
        return reservationRepository.findById(id);
    }

    public void deleteReservation(long id) {
        if (!reservationRepository.existsById(id)) {
            throw new IllegalArgumentException("Reservation not found for ID: " + id);
        }
        reservationRepository.deleteById(id);
    }

    public List<Reservation> getReservationByIdentifier(String identifier) {
        return reservationRepository.findByIdentifier(identifier);
    }

    public void addReservation(Reservation reservation, long organizationId, long roomId) {
        String reservationIdentifier = reservation.getIdentifier().toLowerCase();
        List<Reservation> existingReservations = getReservationByIdentifier(reservationIdentifier);

        if (!existingReservations.isEmpty()) {
            String errorMessage = "Reservation with the identifier " + reservation.getIdentifier() + " already exists";
            throw new IllegalArgumentException(errorMessage);
        }

        Optional<Organization> organizationOptional = organizationRepository.findById(organizationId);
        if (organizationOptional.isEmpty()) {
            throw new IllegalArgumentException("Organization with id " + organizationId + " not found");
        }

        Organization organization = organizationOptional.get();
        List<Room> organizationRooms = organization.getRooms();

        boolean isRoomAssociatedWithOrganization = organizationRooms.stream()
                .anyMatch(room -> room.getId() == roomId);

        if (!isRoomAssociatedWithOrganization) {
            throw new IllegalArgumentException("This room is not associated with organization " + organization.getName());
        }

        Optional<Room> roomOptional = organizationRooms.stream()
                .filter(room -> room.getId() == roomId)
                .findFirst();

        Room room = roomOptional.get();

        List<Reservation> organizationReservations = reservationRepository.findByOrganizationId(organizationId);
        for (Reservation existingReservation : organizationReservations) {
            if (areReservationsOverlapping(existingReservation, reservation)) {
                throw new IllegalArgumentException("The new reservation overlaps with an existing reservation");
            }
        }

        reservation.setIdentifier(reservationIdentifier);
        reservation.setOrganization(organization);
        reservation.setRoom(room);

        reservationRepository.save(reservation);
    }

    private boolean areReservationsOverlapping(Reservation existingReservation, Reservation newReservation) {
        LocalDateTime existingStart = LocalDateTime.of(existingReservation.getDate(), existingReservation.getStartTime());
        LocalDateTime existingEnd = LocalDateTime.of(existingReservation.getDate(), existingReservation.getEndTime());
        LocalDateTime newStart = LocalDateTime.of(newReservation.getDate(), newReservation.getStartTime());
        LocalDateTime newEnd = LocalDateTime.of(newReservation.getDate(), newReservation.getEndTime());

        return (existingStart.isBefore(newEnd) && existingEnd.isAfter(newStart)) ||
                (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) ||
                (existingStart.isEqual(newStart) && existingEnd.isEqual(newEnd));
    }

    public void replaceReservation(long id, Reservation newReservation) {
        Optional<Reservation> existingReservationOptional = reservationRepository.findById(id);

        if (existingReservationOptional.isEmpty()) {
            throw new IllegalArgumentException("Reservation does not exist with id: " + id);
        }

        Reservation existingReservation = existingReservationOptional.get();

        String newReservationIdentifier = newReservation.getIdentifier().toLowerCase();
        List<Reservation> existingReservations = getReservationByIdentifier(newReservationIdentifier);

        if (!existingReservations.isEmpty() && !existingReservation.getIdentifier().equalsIgnoreCase(newReservationIdentifier)) {
            String errorMessage = "Reservation with the identifier " + newReservation.getIdentifier() + " already exists";
            throw new IllegalArgumentException(errorMessage);
        }

        if (!isValidReservationDate(newReservation.getDate())) {
            throw new IllegalArgumentException("Invalid reservation date. Cannot make a reservation for today, a past date, or more than two weeks in advance.");
        }

        if (!isValidIdentifierLength(newReservationIdentifier)) {
            throw new IllegalArgumentException("Invalid identifier length. The identifier must have at least 2 characters and at most 20 characters.");
        }

        if (!isValidReservationDate(newReservation.getDate())) {
            throw new IllegalArgumentException("Invalid reservation date. Cannot make a reservation for today, a past date, or more than two weeks in advance.");
        }

        List<Reservation> organizationReservations = reservationRepository.findByOrganizationId(existingReservation.getOrganization().getId());
        for (Reservation existingReservationInOrganization : organizationReservations) {
            if (existingReservationInOrganization.getId() != existingReservation.getId() &&
                    areReservationsOverlapping(existingReservationInOrganization, newReservation)) {
                throw new IllegalArgumentException("The new reservation overlaps with an existing reservation");
            }
        }

        existingReservation.setIdentifier(newReservationIdentifier);
        existingReservation.setDate(newReservation.getDate());
        existingReservation.setStartTime(newReservation.getStartTime());
        existingReservation.setEndTime(newReservation.getEndTime());

        reservationRepository.save(existingReservation);
    }

    private boolean isValidIdentifierLength(String identifier) {
        int identifierLength = identifier.length();
        return identifierLength >= 2 && identifierLength <= 20;
    }

    private boolean isValidReservationDate(LocalDate date) {
        LocalDate currentDate = LocalDate.now();
        LocalDate maxAllowedDate = currentDate.plusWeeks(2);

        if (date.isEqual(currentDate)) {
            return false;
        } else if (date.isBefore(currentDate)) {
            return false;
        } else if (date.isAfter(maxAllowedDate)) {
            return false;
        }
        return true;
    }
}

