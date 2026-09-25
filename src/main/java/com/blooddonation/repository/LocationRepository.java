package com.blooddonation.repository;

import com.blooddonation.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByIsActiveTrue(); // To show only active locations in the dropdown

    // Public homepage: count of active blood banks/centers island-wide
    long countByIsActiveTrue();
}