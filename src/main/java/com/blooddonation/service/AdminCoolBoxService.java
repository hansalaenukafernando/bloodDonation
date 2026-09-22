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

    public void addCoolBox(String boxCode, String currentTemp) {
        CoolBox box = new CoolBox();
        box.setBoxCode(boxCode);
        box.setCurrentTemp(currentTemp != null && !currentTemp.isEmpty() ? currentTemp : "4.0°C");
        box.setStatus("Active");
        coolBoxRepository.save(box);
    }

    public void updateCoolBox(Integer boxId, String boxCode, String currentTemp) throws Exception {
        CoolBox box = coolBoxRepository.findById(boxId)
                .orElseThrow(() -> new Exception("Cool box not found"));
        box.setBoxCode(boxCode);
        box.setCurrentTemp(currentTemp);
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