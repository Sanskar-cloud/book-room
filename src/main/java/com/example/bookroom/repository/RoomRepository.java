package com.example.bookroom.repository;

import com.example.bookroom.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByName(String name);
    List<Room> findByIdentifier(String identifier);
}
