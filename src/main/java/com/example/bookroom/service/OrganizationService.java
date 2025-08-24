package com.example.bookroom.service;

import com.example.bookroom.model.*;
import com.example.bookroom.model.DTOs.OrganizationDTO;
import com.example.bookroom.repository.OrganizationRepository;
import com.example.bookroom.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    RoomRepository roomRepository;

    public OrganizationDTO convertToDTO(Organization organization) {
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId(organization.getId());
        organizationDTO.setName(organization.getName());

        List<String> roomNames = new ArrayList<>();
        List<Room> rooms = organization.getRooms();
        if (rooms != null) {
            for (Room room : rooms) {
                roomNames.add(room.getName());
            }
        }
        organizationDTO.setRoomNames(roomNames);

        return organizationDTO;
    }

    public List<OrganizationDTO> listOrganizations() {
        List<Organization> allOrganizations = organizationRepository.findAll();
        List<OrganizationDTO> processedReservations = new ArrayList<>();
        for (Organization organization : allOrganizations) {
            OrganizationDTO organizationDTO = convertToDTO(organization);
            processedReservations.add(organizationDTO);
        }
        return processedReservations;
    }

    public Organization getOrganizationById(long id) {
        Optional<Organization> organization = organizationRepository.findById(id);
        if (organization.isPresent()) {
            return organization.get();
        } else {
            throw new IllegalArgumentException("Organization not found for ID: " + id);
        }
    }

    public void deleteOrganization(long id) {
        Optional<Organization> organization = organizationRepository.findById(id);
        if (organization.isPresent()) {
            organizationRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Organization not found for ID: " + id);
        }
    }

    public List<Organization> getOrganizationByName(String name) {
        return organizationRepository.findByName(name);
    }

    public void addOrganization(Organization organization) {
        String organizationName = organization.getName().toLowerCase();

        List<Organization> existingOrganizations = getOrganizationByName(organizationName);
        if (!existingOrganizations.isEmpty()) {
            String errorMessage = "Organization with the name '" + organization.getName() + "' already exists";
            throw new IllegalArgumentException(errorMessage);
        }

        organization.setName(organizationName);
        organizationRepository.save(organization);
    }

    public void replaceOrganization(long id, Organization newOrganization) {
        String organizationName = newOrganization.getName().toLowerCase();

        List<Organization> existingOrganizations = getOrganizationByName(organizationName);
        if (!existingOrganizations.isEmpty()) {
            for (Organization organization : existingOrganizations) {
                if (organization.getId() != id) {
                    String errorMessage = "An organization with the name '" + newOrganization.getName() + "' already exists";
                    throw new IllegalArgumentException(errorMessage);
                }
            }
        }

        if (organizationRepository.existsById(id)) {
            newOrganization.setId(id);
            organizationRepository.save(newOrganization);
        } else {
            throw new IllegalArgumentException("Organization not found for id: " + id);
        }
    }

    public void addRoomToOrganization(long organizationId, long roomId) {
        Optional<Organization> organizationOptional = organizationRepository.findById(organizationId);
        Optional<Room> roomOptional = roomRepository.findById(roomId);

        if (organizationOptional.isPresent() && roomOptional.isPresent()) {
            Organization organization = organizationOptional.get();
            Room room = roomOptional.get();

            if (!room.getAvailability()) {
                throw new IllegalArgumentException("This room is already added to a different organization");
            }

            room.setOrganization(organization);
            room.setAvailability(false);

            if (organization.getRooms() == null) {
                organization.setRooms(new ArrayList<>());
            }
            organization.getRooms().add(room);

            roomRepository.save(room);
            organizationRepository.save(organization);
        } else {
            throw new IllegalArgumentException("Organization or Room not found");
        }
    }

    public void removeRoomFromOrganization(long organizationId, String roomName) {
        Optional<Organization> organizationOptional = organizationRepository.findById(organizationId);

        if (organizationOptional.isPresent()) {
            Organization organization = organizationOptional.get();
            List<Room> rooms = organization.getRooms();

            Optional<Room> roomOptional = rooms.stream()
                    .filter(room -> room.getName().equalsIgnoreCase(roomName))
                    .findFirst();

            if (roomOptional.isPresent()) {
                Room room = roomOptional.get();
                room.setOrganization(null);
                room.setAvailability(true);
                rooms.remove(room);
                roomRepository.save(room);
                organizationRepository.save(organization);
            } else {
                throw new IllegalArgumentException("Room is not associated with the organization");
            }
        } else {
            throw new IllegalArgumentException("Organization not found");
        }
    }
}
