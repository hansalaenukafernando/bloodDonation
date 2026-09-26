package com.blooddonation.service;

import com.blooddonation.entity.BloodBag;
import com.blooddonation.entity.BloodRequest;
import com.blooddonation.entity.CoolBox;
import com.blooddonation.entity.Driver;
import com.blooddonation.entity.Hospital;
import com.blooddonation.repository.BloodBagRepository;
import com.blooddonation.repository.CoolBoxRepository;
import com.blooddonation.repository.DriverRepository;
import com.blooddonation.repository.HospitalBloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalBloodRequestService {

    @Autowired
    private HospitalBloodRequestRepository bloodRequestRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CoolBoxRepository coolBoxRepository;

    @Autowired
    private BloodBagRepository bloodBagRepository;

    public List<BloodRequest> getAllRequests() {
        return bloodRequestRepository.findAllActiveRequests();
    }

    public List<BloodRequest> getRequestsByHospital(Hospital hospital) {
        return bloodRequestRepository.findByHospital(hospital);
    }

    public List<BloodRequest> getActiveRequestsByHospital(Hospital hospital) {
        // Active page: everything except finalized statuses (Delivered / Rejected)
        return bloodRequestRepository.findByHospitalAndStatusNotIn(hospital, List.of("Delivered", "Rejected"));
    }

    public List<BloodRequest> getDeliveredHistoryByHospital(Hospital hospital) {
        // History page: finalized requests, including those the admin rejected
        return bloodRequestRepository.findByHospitalAndStatusIn(hospital, List.of("Delivered", "Rejected"));
    }

    // Dashboard Methods
    public long getActiveCountByHospital(Hospital hospital) {
        return bloodRequestRepository.findByHospitalAndStatusNot(hospital, "Delivered").size();
    }

    public long getDeliveringCountByHospital(Hospital hospital) {
        return bloodRequestRepository.findByHospitalAndStatus(hospital, "Delivering").size();
    }

    public long getDeliveredCountByHospital(Hospital hospital) {
        return bloodRequestRepository.findByHospitalAndStatus(hospital, "Delivered").size();
    }

    public List<BloodRequest> getRecentActiveRequestsByHospital(Hospital hospital) {
        return bloodRequestRepository.findByHospitalAndStatusNot(hospital, "Delivered");
    }

    public List<BloodRequest> getApprovedRequests() {
        return bloodRequestRepository.findByStatus("Approved");
    }

    public List<BloodRequest> getAllDeliveries() {
        // "Approved Requests & Deliveries" page — only requests still in progress (not yet Delivered)
        return bloodRequestRepository.findByStatusIn(List.of("Approved", "Delivering"));
    }

    public List<BloodRequest> getDeliveryHistory() {
        // "Delivery History" page — finalized requests: delivered, and also rejected ones
        // (since Rejected no longer appears on the active Hospital Requests page)
        return bloodRequestRepository.findByStatusIn(List.of("Delivered", "Rejected"));
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers();
    }

    public List<CoolBox> getAvailableCoolBoxes() {
        return coolBoxRepository.findAvailableCoolBoxes();
    }

    public void createBloodRequest(Hospital hospital, String priority, String bloodGroup,
                                   Integer quantity, String ward,
                                   String patientName, Integer patientAge, String patientGender,
                                   String patientIdNumber, String clinicalNotes) {

        BloodRequest request = new BloodRequest();
        request.setHospital(hospital);
        request.setPriority(priority);
        request.setBloodGroup(bloodGroup);
        request.setQuantity(quantity);
        request.setWard(ward);
        request.setPatientName(patientName);
        request.setPatientAge(patientAge);
        request.setPatientGender(patientGender);
        request.setPatientIdNumber(patientIdNumber);
        request.setClinicalNotes(clinicalNotes);
        request.setStatus("Pending");

        bloodRequestRepository.save(request);
    }

    public void updateStatus(Integer requestId, String status) throws Exception {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Blood request not found with id: " + requestId));

        request.setStatus(status);
        bloodRequestRepository.save(request);
    }

    public void assignDeliveryAndDispatch(Integer requestId, Integer driverId, Integer coolBoxId, String bloodGroupUsed) throws Exception {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Request not found"));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new Exception("Driver not found"));

        CoolBox coolBox = coolBoxRepository.findById(coolBoxId)
                .orElseThrow(() -> new Exception("Cool Box not found"));

        // A driver / cool box already on an active (Delivering) delivery cannot be assigned again
        // until that delivery is marked as Delivered.
        if (bloodRequestRepository.existsByDriver_DriverIdAndStatus(driverId, "Delivering")) {
            throw new Exception("This driver is already handling an active delivery. Choose another driver.");
        }
        if (bloodRequestRepository.existsByCoolBox_BoxIdAndStatus(coolBoxId, "Delivering")) {
            throw new Exception("This cool box is already in use on an active delivery. Choose another cool box.");
        }

        // Deduct the dispatched quantity from the available blood bag stock of the selected group
        List<BloodBag> availableBags = bloodBagRepository.findByBloodGroupAndStatusOrderByExpiryDateAsc(bloodGroupUsed, "Available");
        int quantityNeeded = request.getQuantity() != null ? request.getQuantity() : 0;

        if (availableBags.size() < quantityNeeded) {
            throw new Exception("Not enough available " + bloodGroupUsed + " blood bags in stock. Available: " + availableBags.size() + ", Needed: " + quantityNeeded);
        }

        for (int i = 0; i < quantityNeeded; i++) {
            BloodBag bag = availableBags.get(i);
            bag.setStatus("Used");
            bloodBagRepository.save(bag);
        }

        request.setDriver(driver);
        request.setCoolBox(coolBox);
        request.setBloodGroup(bloodGroupUsed);
        request.setStatus("Delivering");

        bloodRequestRepository.save(request);
    }

    public void markAsDelivered(Integer requestId) throws Exception {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Request not found"));

        request.setStatus("Delivered");
        bloodRequestRepository.save(request);
    }

    // Hospital එකට request එකක් edit කරන්න පුළුවන් "Pending" තත්වයේ තියෙද්දී විතරයි.
    // Admin එක Approve/Reject කරාට පස්සේ, hospital එකට ඒක වෙනස් කරන්න බෑ.
    public void updateHospitalRequest(Integer requestId, Hospital hospital, String priority, String bloodGroup,
                                      Integer quantity, String ward,
                                      String patientName, Integer patientAge, String patientGender,
                                      String patientIdNumber, String clinicalNotes) throws Exception {

        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Blood request not found"));

        if (!request.getHospital().getHospitalId().equals(hospital.getHospitalId())) {
            throw new Exception("You are not authorized to edit this request.");
        }

        if (!"Pending".equals(request.getStatus())) {
            throw new Exception("This request has already been processed and can no longer be edited.");
        }

        request.setPriority(priority);
        request.setBloodGroup(bloodGroup);
        request.setQuantity(quantity);
        request.setWard(ward);
        request.setPatientName(patientName);
        request.setPatientAge(patientAge);
        request.setPatientGender(patientGender);
        request.setPatientIdNumber(patientIdNumber);
        request.setClinicalNotes(clinicalNotes);

        bloodRequestRepository.save(request);
    }

    // Hospital එකට request එකක් delete කරන්න පුළුවන් "Pending" තත්වයේ තියෙද්දී විතරයි.
    public void deleteHospitalRequest(Integer requestId, Hospital hospital) throws Exception {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Blood request not found"));

        if (!request.getHospital().getHospitalId().equals(hospital.getHospitalId())) {
            throw new Exception("You are not authorized to delete this request.");
        }

        if (!"Pending".equals(request.getStatus())) {
            throw new Exception("This request has already been processed and can no longer be deleted.");
        }

        bloodRequestRepository.delete(request);
    }
}