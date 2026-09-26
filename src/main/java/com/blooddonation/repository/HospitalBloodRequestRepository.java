package com.blooddonation.repository;

import com.blooddonation.entity.BloodRequest;
import com.blooddonation.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface HospitalBloodRequestRepository extends JpaRepository<BloodRequest, Integer> {
    List<BloodRequest> findByHospital(Hospital hospital);

    // අලුතින් එකතු කළ යුතු methods දෙක:
    List<BloodRequest> findByStatus(String status);
    List<BloodRequest> findByStatusIn(List<String> statuses);

    // Repository එක තුළ මෙය එකතු කරන්න:
    // Admin "Hospital Requests" management page: still-in-progress requests only.
    // Delivered/Rejected are finalized and belong in the Delivery History page instead.
    @Query("SELECT r FROM BloodRequest r WHERE r.status != 'Delivered' AND r.status != 'Rejected'")
    List<BloodRequest> findAllActiveRequests();

    // හොස්පිටල් එකකට අදාළව Delivered නොවූ (Active) ඉල්ලීම් පමණක් ලබා ගැනීමට
    List<BloodRequest> findByHospitalAndStatusNot(Hospital hospital, String status);

    // හොස්පිටල් එකකට අදාළව Delivered වූ (History) ඉල්ලීම් පමණක් ලබා ගැනීමට
    List<BloodRequest> findByHospitalAndStatus(Hospital hospital, String status);

    // හොස්පිටල් එකකට අදාළව given statuses කිහිපයකින් පිටත ඇති (Active, e.g. not Delivered/Rejected) ඉල්ලීම් ලබා ගැනීමට
    List<BloodRequest> findByHospitalAndStatusNotIn(Hospital hospital, List<String> statuses);

    // හොස්පිටල් එකකට අදාළව given statuses (History, e.g. Delivered/Rejected) වලට අයත් ඉල්ලීම් ලබා ගැනීමට
    List<BloodRequest> findByHospitalAndStatusIn(Hospital hospital, List<String> statuses);

    long countByHospitalAndStatusNot(Hospital hospital, String status);
    long countByHospitalAndStatus(Hospital hospital, String status);

    // Used to block re-assigning a driver / cool box that is already on an active delivery
    boolean existsByDriver_DriverIdAndStatus(Integer driverId, String status);
    boolean existsByCoolBox_BoxIdAndStatus(Integer boxId, String status);
}



