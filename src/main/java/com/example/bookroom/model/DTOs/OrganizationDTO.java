package com.example.bookroom.model.DTOs;

import java.util.List;

public class OrganizationDTO {
    private long id;
    private String name;
    private List<String> roomNames;
    public OrganizationDTO() {
    }

    public OrganizationDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoomNames() {
        return roomNames;
    }
    public void setRoomNames(List<String> roomNames) {
        this.roomNames = roomNames;
    }

}

