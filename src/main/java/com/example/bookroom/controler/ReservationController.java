package com.example.bookroom.controler;

import com.example.bookroom.model.Reservation;
import com.example.bookroom.model.DTOs.ReservationDTO;
import com.example.bookroom.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservation")
@CrossOrigin(origins="*")
public class ReservationController {
    @Autowired
    ReservationService reservationService;

    @GetMapping("/all")
    public List<ReservationDTO> listReservations() {
        return reservationService.listReservations();
    }

    @GetMapping("/{id}")
    public Optional<Reservation> getReservationById(@PathVariable long id) {
        return reservationService.getReservationById(id);
    }

    @GetMapping("/named/{identifier}")
    public List<Reservation> getReservationByIdentifier(@PathVariable String identifier) {
        return reservationService.getReservationByIdentifier(identifier);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/new/{organizationId}/{roomId}")
    public void addReservation(@RequestBody Reservation reservation,
                               @PathVariable long organizationId,
                               @PathVariable long roomId) {
        reservationService.addReservation(reservation, organizationId, roomId);
    }

    @PatchMapping("/replace/{id}")
    public void replaceReservation(@PathVariable long id, @RequestBody Reservation newReservation) {
        reservationService.replaceReservation(id, newReservation);
    }
    @DeleteMapping("/delete/{id}")
    public void deleteReservation(@PathVariable long id) {
        reservationService.deleteReservation(id);
    }

}
