package com.example.bookroom.service;

import com.example.bookroom.model.Equipment;
import com.example.bookroom.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {
    @Autowired
    EquipmentRepository equipmentRepository;

    public List<Equipment> listEquipments() {
        return equipmentRepository.findAll();
    }

    public Equipment getEquipmentById(long id) {
        Optional<Equipment> equipmentOptional = equipmentRepository.findById(id);
        if (equipmentOptional.isEmpty()) {
            throw new IllegalArgumentException("Equipment not found for ID: " + id);
        }
        return equipmentOptional.get();
    }

    public void deleteEquipment(long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Equipment not found for ID: " + id);
        }
        equipmentRepository.deleteById(id);
    }

    public List<Equipment> getEquipmentByName(String name) {
        List<Equipment> equipmentList = equipmentRepository.findByName(name);
        if (equipmentList.isEmpty()) {
            throw new IllegalArgumentException("No equipment found for name: " + name);
        }
        return equipmentList;
    }

    public void addEquipment(Equipment equipment) {
        String equipmentName = equipment.getName().toLowerCase();

        List<Equipment> existingEquipments = getEquipmentByName(equipmentName);
        if (!existingEquipments.isEmpty()) {
            String errorMessage = "Equipment with the name '" + equipment.getName() + "' already exists";
            throw new IllegalArgumentException(errorMessage);
        }

        equipment.setName(equipmentName);
        equipmentRepository.save(equipment);
    }

    public void replaceEquipment(long id, Equipment newEquipment) {
        if(equipmentRepository.existsById(id)) {
            newEquipment.setId(id);
            equipmentRepository.save(newEquipment);
        }
        else throw new IllegalArgumentException("Equipment doesn't exists with id: " + id);
    }
}
