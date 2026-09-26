package com.blooddonation.repository;

import com.blooddonation.entity.CoolBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoolBoxRepository extends JpaRepository<CoolBox, Integer> {
    List<CoolBox> findByBoxCodeContainingIgnoreCase(String boxCode);

    // Cool boxes that are not currently tied to an active (Delivering) delivery
    @Query("SELECT c FROM CoolBox c WHERE c.boxId NOT IN " +
           "(SELECT r.coolBox.boxId FROM BloodRequest r WHERE r.status = 'Delivering' AND r.coolBox IS NOT NULL)")
    List<CoolBox> findAvailableCoolBoxes();
}