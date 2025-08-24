package com.example.bookroom.model.DTOs;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationDTO {
    private long id;
    private String identifier;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String roomName;
    private String organizationName;

    public ReservationDTO() {

    }

    public ReservationDTO(long id, String identifier, LocalDate date, LocalTime startTime, LocalTime endTime, String roomName, String organizationName) {
        this.id = id;
        this.identifier = identifier;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
        this.organizationName = organizationName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}