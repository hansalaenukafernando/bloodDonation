package com.blooddonation.service;

import com.blooddonation.entity.BloodBag;
import com.blooddonation.repository.BloodBagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles pending blood bag screening, test history and result submission.
 * Split out of the original LabTestService to keep this concern isolated.
 */
@Service
public class LabTestOperationsService {

    @Autowired
    private BloodBagRepository bloodBagRepository;

    public List<BloodBag> getPendingBloodBags() {
        return bloodBagRepository.findByStatus("Untested");
    }

    public List<BloodBag> getTestedBloodHistory() {
        List<BloodBag> historyList = new ArrayList<>();
        historyList.addAll(bloodBagRepository.findByStatus("Available"));
        historyList.addAll(bloodBagRepository.findByStatus("Discarded"));
        return historyList;
    }

    public void submitTestResults(Integer bagId, String hiv, String hepatitis, String syphilis, String testOutcome) throws Exception {
        BloodBag bag = bloodBagRepository.findById(bagId)
                .orElseThrow(() -> new Exception("Blood bag not found"));

        bag.setHivResult(hiv);
        bag.setHepatitisResult(hepatitis);
        bag.setSyphilisResult(syphilis);
        bag.setTestedDate(LocalDate.now());

        if ("Pass".equals(testOutcome)) {
            bag.setStatus("Available");
        } else {
            bag.setStatus("Discarded");
        }

        bloodBagRepository.save(bag);
    }
}
