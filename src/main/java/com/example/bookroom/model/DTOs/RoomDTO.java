package com.example.bookroom.model.DTOs;

public class RoomDTO {
    private long id;
    private String name;
    private String identifier;
    private int level;
    private boolean availability;
    private Integer numberOfSittingPlaces;
    private Integer numberOfStandingPlaces;
    private String organizationName;

    public RoomDTO(long id, String name, String identifier, int level, boolean availability, Integer numberOfSittingPlaces, Integer numberOfStandingPlaces, String organizationName) {
        this.id = id;
        this.name = name;
        this.identifier = identifier;
        this.level = level;
        this.availability = availability;
        this.numberOfSittingPlaces = numberOfSittingPlaces;
        this.numberOfStandingPlaces = numberOfStandingPlaces;
        this.organizationName = organizationName;
    }

    public RoomDTO() {

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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public Integer getNumberOfSittingPlaces() {
        return numberOfSittingPlaces;
    }

    public void setNumberOfSittingPlaces(Integer numberOfSittingPlaces) {
        this.numberOfSittingPlaces = numberOfSittingPlaces;
    }

    public Integer getNumberOfStandingPlaces() {
        return numberOfStandingPlaces;
    }

    public void setNumberOfStandingPlaces(Integer numberOfStandingPlaces) {
        this.numberOfStandingPlaces = numberOfStandingPlaces;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
