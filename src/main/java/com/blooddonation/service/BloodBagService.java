package com.blooddonation.service;

import com.blooddonation.entity.BloodBag;
import com.blooddonation.entity.Donor;
import com.blooddonation.repository.BloodBagRepository;
import com.blooddonation.repository.DonorRepository; // ඔබේ Donor repository එක මෙහි import කරන්න
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BloodBagService {

    @Autowired
    private BloodBagRepository bloodBagRepository;

    @Autowired
    private DonorRepository donorRepository; // Donor data ලබාගැනීමට

    public List<BloodBag> getAllBloodBags() {
        // Blood Inventory Control page — live stock only. "Used" bags have their own history page.
        return bloodBagRepository.findByStatusNot("Used");
    }

    public List<BloodBag> getUsedBloodBagsHistory() {
        return bloodBagRepository.findByStatus("Used");
    }

    public void addBloodBag(Integer donorId, String bloodGroup, LocalDate collectionDate, LocalDate expiryDate) {
        BloodBag bag = new BloodBag();

        if (donorId != null) {
            Donor donor = donorRepository.findById(donorId).orElse(null);
            bag.setDonor(donor);
        }

        bag.setBloodGroup(bloodGroup);
        bag.setCollectionDate(collectionDate);
        bag.setExpiryDate(expiryDate);
        bag.setStatus("Available");

        bloodBagRepository.save(bag);
    }

    public void deleteBloodBag(Integer bagId) {
        bloodBagRepository.deleteById(bagId);
    }

    public void cleanupExpiredBags() {
        LocalDate today = LocalDate.now();
        List<BloodBag> expiredBags = bloodBagRepository.findExpiredAvailableBags(today);

        for (BloodBag bag : expiredBags) {
            bag.setStatus("Discarded");
            bloodBagRepository.save(bag);
        }
    }

    public int getStockCount(String bloodGroup) {
        Integer count = bloodBagRepository.countAvailableByBloodGroup(bloodGroup);
        return count != null ? count : 0;
    }
}