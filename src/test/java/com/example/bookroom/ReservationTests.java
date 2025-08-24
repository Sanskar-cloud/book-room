package com.example.bookroom;

import com.example.bookroom.model.Organization;
import com.example.bookroom.model.Reservation;
import com.example.bookroom.model.DTOs.ReservationDTO;
import com.example.bookroom.model.Room;
import com.example.bookroom.repository.OrganizationRepository;
import com.example.bookroom.repository.ReservationRepository;
import com.example.bookroom.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationTests {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void convertToDTOShouldConvertReservationToDTO() {
        // Given
        Organization organization = new Organization();
        organization.setId(1);
        organization.setName("Test Org");

        Room room = new Room();
        room.setId(1);
        room.setName("Test Room");

        Reservation reservation = new Reservation();
        reservation.setId(1);
        reservation.setIdentifier("ABC123");
        reservation.setDate(LocalDate.of(2023, 6, 4));
        reservation.setStartTime(LocalTime.of(10, 0));
        reservation.setEndTime(LocalTime.of(12, 0));
        reservation.setRoom(room);
        reservation.setOrganization(organization);

        // When
        ReservationDTO result = reservationService.convertToDTO(reservation);

        // Then
        assertEquals(1, result.getId());
        assertEquals("ABC123", result.getIdentifier());
        assertEquals(LocalDate.of(2023, 6, 4), result.getDate());
        assertEquals(LocalTime.of(10, 0), result.getStartTime());
        assertEquals(LocalTime.of(12, 0), result.getEndTime());
        assertEquals("Test Room", result.getRoomName());
        assertEquals("Test Org", result.getOrganizationName());
    }

    @Test
    public void listReservationsShouldReturnListOfReservationDTOs() {
        // Given
        Organization organization = new Organization();
        organization.setId(1);
        organization.setName("Test Org");

        Room room = new Room();
        room.setId(1);
        room.setName("Test Room");

        Reservation reservation1 = new Reservation();
        reservation1.setId(1);
        reservation1.setIdentifier("ABC123");
        reservation1.setDate(LocalDate.of(2023, 6, 4));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(12, 0));
        reservation1.setRoom(room);
        reservation1.setOrganization(organization);

        Reservation reservation2 = new Reservation();
        reservation2.setId(2);
        reservation2.setIdentifier("DEF456");
        reservation2.setDate(LocalDate.of(2023, 6, 5));
        reservation2.setStartTime(LocalTime.of(14, 0));
        reservation2.setEndTime(LocalTime.of(16, 0));
        reservation2.setRoom(room);
        reservation2.setOrganization(organization);

        when(reservationRepository.findAll()).thenReturn(Arrays.asList(reservation1, reservation2));

        // When
        List<ReservationDTO> result = reservationService.listReservations();

        // Then
        assertEquals(2, result.size());

        ReservationDTO dto1 = result.get(0);
        assertEquals(1, dto1.getId());
        assertEquals("ABC123", dto1.getIdentifier());
        assertEquals(LocalDate.of(2023, 6, 4), dto1.getDate());
        assertEquals(LocalTime.of(10, 0), dto1.getStartTime());
        assertEquals(LocalTime.of(12, 0), dto1.getEndTime());
        assertEquals("Test Room", dto1.getRoomName());
        assertEquals("Test Org", dto1.getOrganizationName());

        ReservationDTO dto2 = result.get(1);
        assertEquals(2, dto2.getId());
        assertEquals("DEF456", dto2.getIdentifier());
        assertEquals(LocalDate.of(2023, 6, 5), dto2.getDate());
        assertEquals(LocalTime.of(14, 0), dto2.getStartTime());
        assertEquals(LocalTime.of(16, 0), dto2.getEndTime());
        assertEquals("Test Room", dto2.getRoomName());
        assertEquals("Test Org", dto2.getOrganizationName());
    }
    @Test
    public void getReservationByIdShouldReturnEmptyOptionalWhenNotFound() {
        // Given
        long id = 1;
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Reservation> result = reservationService.getReservationById(id);

        // Then
        verify(reservationRepository).findById(id);
        assertFalse(result.isPresent());
    }

    @Test
    public void getReservationByIdShouldReturnReservationOptionalWhenFound() {
        // Given
        long id = 1;
        Reservation reservation = new Reservation();
        reservation.setId(id);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        // When
        Optional<Reservation> result = reservationService.getReservationById(id);

        // Then
        verify(reservationRepository).findById(id);
        assertTrue(result.isPresent());
        assertEquals(reservation, result.get());
    }
    @Test
    void deleteReservationShouldDeleteReservationWhenFound() {
        // Given
        long id = 1;
        Reservation reservation = new Reservation();
        reservation.setId(id);
        when(reservationRepository.existsById(id)).thenReturn(true);

        // When
        reservationService.deleteReservation(id);

        // Then
        verify(reservationRepository).deleteById(id);
    }

    @Test
    void deleteReservationShouldThrowExceptionWhenNotFound() {
        // Given
        long id = 1;
        when(reservationRepository.existsById(id)).thenReturn(false);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> reservationService.deleteReservation(id));
        verify(reservationRepository, never()).deleteById(id);
    }

    @Test
    void replaceReservationShouldUpdateReservationWhenValid() {
        // Given
        long id = 1;
        String existingIdentifier = "Existing Reservation";
        LocalDate existingDate = LocalDate.now().plusDays(2);
        LocalTime existingStartTime = LocalTime.now().plusHours(2);
        LocalTime existingEndTime = LocalTime.now().plusHours(1);

        Organization existingOrganization = new Organization();
        existingOrganization.setId(4);
        existingOrganization.setName("Example Organization");

        Room existingRoom = new Room();
        existingRoom.setId(4);
        existingRoom.setName("Example Room");

        Reservation existingReservation = new Reservation();
        existingReservation.setId(id);
        existingReservation.setIdentifier(existingIdentifier);
        existingReservation.setOrganization(existingOrganization);
        existingReservation.setRoom(existingRoom);
        existingReservation.setDate(existingDate);
        existingReservation.setStartTime(existingStartTime);
        existingReservation.setEndTime(existingEndTime);

        Reservation newReservation = new Reservation();
        String newIdentifier = "new reservation";
        LocalDate newDate = LocalDate.now().plusDays(1);
        LocalTime newStartTime = LocalTime.now().plusHours(1);
        LocalTime newEndTime = LocalTime.now().plusHours(2);

        newReservation.setIdentifier(newIdentifier);
        newReservation.setDate(newDate);
        newReservation.setStartTime(newStartTime);
        newReservation.setEndTime(newEndTime);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(existingReservation));

        // When
        reservationService.replaceReservation(id, newReservation);

        // Then
        verify(reservationRepository).save(existingReservation);
        assertEquals(newIdentifier, existingReservation.getIdentifier());
        assertEquals(newDate, existingReservation.getDate());
        assertEquals(newStartTime, existingReservation.getStartTime());
        assertEquals(newEndTime, existingReservation.getEndTime());
    }

    @Test
    void replaceReservationShouldThrowExceptionWhenReservationNotFound() {
        // Given
        long id = 1;
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        Reservation newReservation = new Reservation();
        String newIdentifier = "New Reservation";
        LocalDate newDate = LocalDate.now().plusDays(1);
        LocalTime newStartTime = LocalTime.now().plusHours(1);
        LocalTime newEndTime = LocalTime.now().plusHours(2);

        newReservation.setIdentifier(newIdentifier);
        newReservation.setDate(newDate);
        newReservation.setStartTime(newStartTime);
        newReservation.setEndTime(newEndTime);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> reservationService.replaceReservation(id, newReservation));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}


