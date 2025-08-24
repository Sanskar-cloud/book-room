package com.example.bookroom.service;

import com.example.bookroom.model.*;
import com.example.bookroom.model.DTOs.RoomDTO;
import com.example.bookroom.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    RoomRepository roomRepository;

    public RoomDTO convertToDTO(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(room.getId());
        roomDTO.setName(room.getName());
        roomDTO.setIdentifier(room.getIdentifier());
        roomDTO.setLevel(room.getLevel());
        roomDTO.setAvailability(room.getAvailability());
        roomDTO.setNumberOfSittingPlaces(room.getNumberOfSittingPlaces());
        roomDTO.setNumberOfStandingPlaces(room.getNumberOfStandingPlaces());
        return roomDTO;
    }

    public List<RoomDTO> listRooms() {
        List<Room> allRooms = roomRepository.findAll();
        List<RoomDTO> processedRooms = new ArrayList<>();
        for (Room room : allRooms) {
            RoomDTO reservationDTO = convertToDTO(room);
            processedRooms.add(reservationDTO);
        }
        return processedRooms;
    }

    public Room getRoomById(long id) {
        Optional<Room> roomOptional = roomRepository.findById(id);
        if (roomOptional.isEmpty()) {
            throw new IllegalArgumentException("Room not found for ID: " + id);
        }
        return roomOptional.get();
    }

    public List<Room> getRoomByName(String name) {
        return roomRepository.findByName(name);
    }

    public void deleteRoom(long id) {
        if (!roomRepository.existsById(id)) {
            throw new IllegalArgumentException("Room not found for ID: " + id);
        }
        roomRepository.deleteById(id);
    }

    public void addRoom(Room room) {
        String roomName = room.getName().toLowerCase();
        String roomIdentifier = room.getIdentifier();

        Optional<Room> existingRoom = getRoomByName(roomName)
                .stream()
                .filter(r -> r.getName().equalsIgnoreCase(roomName))
                .findFirst();

        if (existingRoom.isPresent()) {
            String errorMessage = "Room with the name '" + room.getName() + "' already exists";
            throw new IllegalArgumentException(errorMessage);
        }

        List<Room> roomsWithSameIdentifier = roomRepository.findByIdentifier(roomIdentifier);

        if (!roomsWithSameIdentifier.isEmpty()) {
            String errorMessage = "Room with the identifier '" + roomIdentifier + "' already exists";
            throw new IllegalArgumentException(errorMessage);
        }

        room.setName(roomName);
        roomRepository.save(room);
    }


    public void replaceRoom(long id, Room newRoom) {
        String roomName = newRoom.getName().toLowerCase();

        List<Room> existingRooms = getRoomByName(roomName);
        if (!existingRooms.isEmpty()) {
            for (Room room : existingRooms) {
                if (room.getId() != id) {
                    String errorMessage = "A room with the name '" + newRoom.getName() + "' already exists";
                    throw new IllegalArgumentException(errorMessage);
                }
            }
        }

        if (roomRepository.existsById(id)) {
            newRoom.setId(id);
            newRoom.setName(roomName);
            roomRepository.save(newRoom);
        } else {
            throw new IllegalArgumentException("Room doesn't exist with id: " + id);
        }
    }

}
