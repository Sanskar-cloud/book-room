package com.example.bookroom;

import com.example.bookroom.model.DTOs.OrganizationDTO;
import com.example.bookroom.model.Organization;
import com.example.bookroom.model.Room;
import com.example.bookroom.repository.OrganizationRepository;
import com.example.bookroom.repository.RoomRepository;
import com.example.bookroom.service.OrganizationService;
import jakarta.validation.ConstraintViolation;
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

public class OrganizationTests {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private OrganizationService organizationService;

    private Validator validator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void organizationNameBlankShouldCauseTwoViolations() {
        Organization organization = new Organization();
        organization.setId(1);
        organization.setName("");

        Set<ConstraintViolation<Organization>> violations = validator.validate(organization);

        assertEquals(2, violations.size());

        List<String> expectedMessages = Arrays.asList(
                "wielkość musi należeć do zakresu od 2 do 20",
                "Conference room name is required"
        );

        for (ConstraintViolation<Organization> violation : violations) {
            String actualMessage = violation.getMessage();
            System.out.println("Actual violation message: " + actualMessage);
            assertTrue(expectedMessages.contains(actualMessage));
        }
    }

//    @Ignore
//    @Test
//    public void oneLetterOrganizationNameShouldCauseViolation() {
//        Organization organization = new Organization();
//        organization.setId(1);
//        organization.setName("A");
//
//        Set<ConstraintViolation<Organization>> violations = validator.validate(organization);
//        assertEquals(1, violations.size());
//
//        ConstraintViolation<Organization> violation = violations.iterator().next();
//        String expectedMessage = "wielkość musi należeć do zakresu od 2 do 20";
//        assertTrue(violation.getMessage().contains(expectedMessage));
//    }

    @Test
    public void validOrganizationNameShouldNotCauseViolation() {
        Organization organization = new Organization();
        organization.setId(1);
        organization.setName("Organization");

        Set<ConstraintViolation<Organization>> violations = validator.validate(organization);
        assertTrue(violations.isEmpty());
    }

    @Test
    void convertToDTOShouldConvertOrganizationToDTO() {
        // Given
        Organization organization = new Organization();
        organization.setId(1);
        organization.setName("Test Org");
        Room room1 = new Room();
        room1.setName("Room 1");
        Room room2 = new Room();
        room2.setName("Room 2");
        organization.setRooms(Arrays.asList(room1, room2));

        // When
        OrganizationDTO result = organizationService.convertToDTO(organization);

        // Then
        assertEquals(1, result.getId());
        assertEquals("Test Org", result.getName());
        assertEquals(Arrays.asList("Room 1", "Room 2"), result.getRoomNames());
    }

    @Test
    void listOrganizationsShouldReturnListOfOrganizationDTOs() {
        // Given
        Organization organization1 = new Organization();
        organization1.setId(1);
        organization1.setName("Org 1");
        Organization organization2 = new Organization();
        organization2.setId(2);
        organization2.setName("Org 2");
        organization2.setRooms(null);
        when(organizationRepository.findAll()).thenReturn(Arrays.asList(organization1, organization2));

        // When
        List<OrganizationDTO> result = organizationService.listOrganizations();

        // Then
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Org 1", result.get(0).getName());
        assertEquals(2, result.get(1).getId());
        assertEquals("Org 2", result.get(1).getName());
        assertEquals(Collections.emptyList(), result.get(1).getRoomNames());
    }


    @Test
    void getOrganizationByIdShouldReturnOrganizationWhenIdExists() {
        // Given
        long id = 1;
        Organization organization = new Organization();
        organization.setId(id);
        organization.setName("Test Organization");
        when(organizationRepository.findById(id)).thenReturn(Optional.of(organization));

        // When
        Organization result = organizationService.getOrganizationById(id);

        // Then
        assertEquals(id, result.getId());
        assertEquals("Test Organization", result.getName());
    }

    @Test
    void getOrganizationByIdShouldThrowExceptionWhenIdDoesNotExist() {
        // Given
        long id = 1;
        when(organizationRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            organizationService.getOrganizationById(id);
        });
    }

    @Test
    void deleteOrganizationShouldDeleteExistingOrganization() {
        // Given
        long id = 1;
        Organization organization = new Organization();
        organization.setId(id);
        organization.setName("Test Organization");
        when(organizationRepository.findById(id)).thenReturn(Optional.of(organization));

        // When
        assertDoesNotThrow(() -> {
            organizationService.deleteOrganization(id);
        });

        // Then
        verify(organizationRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteOrganizationShouldThrowExceptionWhenOrganizationNotFound() {
        // Given
        long id = 1;
        when(organizationRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            organizationService.deleteOrganization(id);
        });
    }


    @Test
    void addOrganizationShouldAddNewOrganization() {
        // Given
        Organization organization = new Organization();
        organization.setName("Test Organization");

        when(organizationService.getOrganizationByName("test organization")).thenReturn(Collections.emptyList());
        when(organizationRepository.save(organization)).thenReturn(organization);

        // When
        assertDoesNotThrow(() -> {
            organizationService.addOrganization(organization);
        });

        // Then
        verify(organizationRepository, times(1)).save(organization);
    }

    @Test
    public void addOrganizationShouldThrowExceptionWhenAddingExistingOrganization() {
        // Given
        Organization organization = new Organization();
        organization.setName("Existing Organization");

        List<Organization> existingOrganizations = new ArrayList<>();
        existingOrganizations.add(organization);

        when(organizationService.getOrganizationByName("existing organization")).thenReturn(existingOrganizations);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            organizationService.addOrganization(organization);
        });
    }

    @Test
    void replaceOrganizationShouldReplaceOrganization() {
        // Given
        long id = 1;
        Organization existingOrg = new Organization(id, "Org1");
        Organization newOrg = new Organization(id, "Updated Org1");

        when(organizationRepository.existsById(id)).thenReturn(true);

        // When
        organizationService.replaceOrganization(id, newOrg);

        // Then
        verify(organizationRepository, times(1)).existsById(id);
        verify(organizationRepository, times(1)).save(newOrg);
        assertEquals(id, newOrg.getId());
    }

    @Test
    void replaceOrganizationShouldThrowExceptionWhenIdDoestExists() {
        // Given
        long id = 1;
        Organization newOrg = new Organization(id, "Updated Org1");

        when(organizationRepository.existsById(id)).thenReturn(false);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> organizationService.replaceOrganization(id, newOrg));
        verify(organizationRepository, times(1)).existsById(id);
        verify(organizationRepository, never()).save(newOrg);
    }

    @Test
    void addRoomToOrganizationShouldAddRoomToOrganizationWhenRoomIsAvailable() {
        // Given
        long organizationId = 1;
        long roomId = 2;

        Organization organization = new Organization();
        organization.setId(organizationId);

        Room room = new Room();
        room.setId(roomId);
        room.setAvailability(true);

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // When
        organizationService.addRoomToOrganization(organizationId, roomId);

        // Then
        verify(roomRepository, times(1)).save(room);
        verify(organizationRepository, times(1)).save(organization);
        assertFalse(room.getAvailability());
        assertEquals(organization, room.getOrganization());
        assertTrue(organization.getRooms().contains(room));
    }

    @Test
    void addRoomToOrganizationShouldThrowExceptionWhenOrganizationOrRoomNotFound() {
        // Given
        long organizationId = 1;
        long roomId = 2;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            organizationService.addRoomToOrganization(organizationId, roomId);
        });

        verify(roomRepository, never()).save(any(Room.class));
        verify(organizationRepository, never()).save(any(Organization.class));
    }

}
