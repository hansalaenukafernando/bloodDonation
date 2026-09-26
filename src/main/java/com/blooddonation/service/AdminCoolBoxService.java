package com.blooddonation.service;

import com.blooddonation.entity.CoolBox;
import com.blooddonation.repository.CoolBoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCoolBoxService {

    @Autowired
    private CoolBoxRepository coolBoxRepository;

    public List<CoolBox> getAllCoolBoxes(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return coolBoxRepository.findByBoxCodeContainingIgnoreCase(keyword);
        }
        return coolBoxRepository.findAll();
    }

    public void addCoolBox(String boxCode, String currentTemp) throws Exception {
        // box_code is unique in the DB, so check it here and give a friendly message
        boolean exists = coolBoxRepository.findByBoxCodeContainingIgnoreCase(boxCode)
                .stream()
                .anyMatch(b -> b.getBoxCode().equalsIgnoreCase(boxCode));
        if (exists) {
            throw new Exception("A cool box with this code already exists!");
        }

        CoolBox box = new CoolBox();
        box.setBoxCode(boxCode);
        box.setCurrentTemp(currentTemp != null && !currentTemp.trim().isEmpty() ? currentTemp.trim() : "4.0°C");
        box.setStatus("Active");
        coolBoxRepository.save(box);
    }

    public void updateCoolBox(Integer boxId, String boxCode, String currentTemp) throws Exception {
        CoolBox box = coolBoxRepository.findById(boxId)
                .orElseThrow(() -> new Exception("Cool box not found"));

        // the same code must not already belong to another box
        boolean takenByAnother = coolBoxRepository.findByBoxCodeContainingIgnoreCase(boxCode)
                .stream()
                .anyMatch(b -> b.getBoxCode().equalsIgnoreCase(boxCode) && !b.getBoxId().equals(boxId));
        if (takenByAnother) {
            throw new Exception("A cool box with this code already exists!");
        }

        box.setBoxCode(boxCode);
        box.setCurrentTemp(currentTemp != null && !currentTemp.trim().isEmpty() ? currentTemp.trim() : "4.0°C");
        coolBoxRepository.save(box);
    }

    public void updateStatus(Integer boxId, String status) throws Exception {
        CoolBox box = coolBoxRepository.findById(boxId)
                .orElseThrow(() -> new Exception("Cool box not found"));
        box.setStatus(status);
        coolBoxRepository.save(box);
    }

    public void deleteCoolBox(Integer boxId) {
        coolBoxRepository.deleteById(boxId);
    }
}
