package com.example.bookroom;

import com.example.bookroom.model.DTOs.RoomDTO;
import com.example.bookroom.model.Room;
import com.example.bookroom.repository.RoomRepository;
import com.example.bookroom.service.RoomService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class RoomTests {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Validator validator;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

//    @Ignore
//    @Test
//    public void testRoomAnnotations() {
//        Room room = new Room();
//        room.setName("A");
//        room.setIdentifier("RoomRoomRoom");
//        room.setId(1);
//        room.setAvailability(true);
//        room.setLevel(12);
//        room.setNumberOfSittingPlaces(10);
//        room.setNumberOfStandingPlaces(15);
//
//        Set<ConstraintViolation<Room>> violations = validator.validate(room);
//
//        assertEquals(3, violations.size());
//
//        List<String> expectedMessages = Arrays.asList(
//                "wielkość musi należeć do zakresu od 2 do 20",
//                "Identifier format is invalid",
//                "Level must be between 0 and 10"
//        );
//
//        for (ConstraintViolation<Room> violation : violations) {
//            String actualMessage = violation.getMessage();
//            System.out.println("Actual violation message: " + actualMessage);
//            assertTrue(expectedMessages.contains(actualMessage));
//        }
//    }

    @Test
    void convertToDTOShouldConvertRoomToDTO() {
        // Given
        Room room = new Room();
        room.setId(1);
        room.setName("Room 1");
        room.setIdentifier("ABC123");
        room.setLevel(1);
        room.setAvailability(true);
        room.setNumberOfSittingPlaces(10);
        room.setNumberOfStandingPlaces(20);

        // When
        RoomDTO result = roomService.convertToDTO(room);

        // Then
        assertEquals(1, result.getId());
        assertEquals("Room 1", result.getName());
        assertEquals("ABC123", result.getIdentifier());
        assertEquals(1, result.getLevel());
        assertEquals(true, result.isAvailability());
        assertEquals(10, result.getNumberOfSittingPlaces());
        assertEquals(20, result.getNumberOfStandingPlaces());
    }

    @Test
    void listRoomsShouldReturnListOfRoomDTOs() {
        // Given
        Room room1 = new Room();
        room1.setId(1);
        room1.setName("Room 1");
        room1.setIdentifier("ABC123");
        room1.setLevel(1);
        room1.setAvailability(true);
        room1.setNumberOfSittingPlaces(10);
        room1.setNumberOfStandingPlaces(20);

        Room room2 = new Room();
        room2.setId(2);
        room2.setName("Room 2");
        room2.setIdentifier("DEF456");
        room2.setLevel(2);
        room2.setAvailability(false);
        room2.setNumberOfSittingPlaces(5);
        room2.setNumberOfStandingPlaces(15);

        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2));

        // When
        List<RoomDTO> result = roomService.listRooms();

        // Then
        assertEquals(2, result.size());

        RoomDTO dto1 = result.get(0);
        assertEquals(1, dto1.getId());
        assertEquals("Room 1", dto1.getName());
        assertEquals("ABC123", dto1.getIdentifier());
        assertEquals(1, dto1.getLevel());
        assertEquals(true, dto1.isAvailability());
        assertEquals(10, dto1.getNumberOfSittingPlaces());
        assertEquals(20, dto1.getNumberOfStandingPlaces());

        RoomDTO dto2 = result.get(1);
        assertEquals(2, dto2.getId());
        assertEquals("Room 2", dto2.getName());
        assertEquals("DEF456", dto2.getIdentifier());
        assertEquals(2, dto2.getLevel());
        assertEquals(false, dto2.isAvailability());
        assertEquals(5, dto2.getNumberOfSittingPlaces());
        assertEquals(15, dto2.getNumberOfStandingPlaces());
    }


    @Test
    void getRoomByIdShouldReturnRoom() {
        // Given
        long id = 1;
        Room room = new Room(id, "Room1", "R001", 1, true, 10, 20);

        when(roomRepository.findById(id)).thenReturn(Optional.of(room));

        // When
        Optional<Room> result = Optional.ofNullable(roomService.getRoomById(id));

        // Then
        assertEquals(Optional.of(room), result);
    }

    @Test
    void getRoomByIdShouldThrowExceptionWhenIdDoesNotExist() {
        // Given
        long id = 1;
        when(roomRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> roomService.getRoomById(id));
    }

    @Test
    void deleteRoomShouldDeleteRoomWhenIdExists() {
        // Given
        long id = 1;
        when(roomRepository.existsById(id)).thenReturn(true);

        // When
        roomService.deleteRoom(id);

        // Then
        verify(roomRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteRoomShouldThrowExceptionWhenIdDoesNotExist() {
        // Given
        long id = 1;
        when(roomRepository.existsById(id)).thenReturn(false);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> roomService.deleteRoom(id));
        verify(roomRepository, never()).deleteById(id);
    }

    @Test
    public void addRoomShouldSaveRoomWhenAddingNewRoom() {
        // Given
        long id = 1;
        Room room = new Room(id, "Room1", "R001", 1, true, 10, 20);

        when(roomRepository.findByName("Room1")).thenReturn(Collections.emptyList());

        // When
        roomService.addRoom(room);

        // Then
        verify(roomRepository, never()).findById(id);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void addRoomShouldThrowExceptionWhenAddingExistingRoom() {
        // Given
        Room room = new Room();
        room.setName("Existing Room");
        room.setIdentifier("Room001");

        List<Room> existingRooms = new ArrayList<>();
        existingRooms.add(room);

        when(roomRepository.findByName("existing room")).thenReturn(existingRooms);
        when(roomRepository.findByIdentifier("Room001")).thenReturn(Collections.emptyList());

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            roomService.addRoom(room);
        });

        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    public void addRoomShouldThrowExceptionWhenAddingRoomWithExistingIdentifier() {
        // Given
        Room existingRoom = new Room();
        existingRoom.setName("Room1");
        existingRoom.setIdentifier("R001");

        when(roomRepository.findByName("Room1")).thenReturn(Collections.emptyList());
        when(roomRepository.findByIdentifier("R001")).thenReturn(Arrays.asList(existingRoom));

        Room newRoom = new Room();
        newRoom.setName("New Room");
        newRoom.setIdentifier("R001");

        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomService.addRoom(newRoom);
        });

        assertEquals("Room with the identifier 'R001' already exists", exception.getMessage());
        verify(roomRepository, never()).save(newRoom);
    }

    @Test
    void replaceRoomShouldReplaceRoom() {
        // Given
        long id = 1;
        Room existingRoom = new Room(id, "Room1", "R001", 1, true, 10, 20);
        Room newRoom = new Room(id, "Updated Room1", "R002", 2, false, 15, 25);

        when(roomRepository.existsById(id)).thenReturn(true);

        // When
        roomService.replaceRoom(id, newRoom);

        // Then
        verify(roomRepository, times(1)).existsById(id);
        verify(roomRepository, times(1)).save(newRoom);
        assertEquals(id, newRoom.getId());
    }

    @Test
    void replaceRoomShouldThrowExceptionWhenIdIsNotExisting() {
        // Given
        long id = 1;
        Room newRoom = new Room(id, "Updated Room1", "R002", 2, false, 15, 25);

        when(roomRepository.existsById(id)).thenReturn(false);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> roomService.replaceRoom(id, newRoom));
        verify(roomRepository, times(1)).existsById(id);
        verify(roomRepository, never()).save(newRoom);
    }
}
