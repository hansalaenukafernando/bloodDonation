package com.blooddonation.service;

import com.blooddonation.entity.Organization;
import com.blooddonation.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    public void registerOrganization(String orgName, String orgType, String regNumber,
                                     String address, String coordinatorName,
                                     String email, String phone, String password) throws Exception {

        if (organizationRepository.findByEmail(email).isPresent()) {
            throw new Exception("An organization with this email address already exists!");
        }

        Organization org = new Organization();
        org.setOrgName(orgName);
        org.setOrgType(orgType);
        org.setRegNumber(regNumber);
        org.setAddress(address);
        org.setCoordinatorName(coordinatorName);
        org.setEmail(email);
        org.setPhone(phone);
        org.setPasswordHash(password);
        org.setStatus("Active");

        organizationRepository.save(org);
    }

    public Organization authenticateOrganization(String email, String password) throws Exception {
        Optional<Organization> orgOpt = organizationRepository.findByEmail(email);

        if (orgOpt.isPresent()) {
            Organization org = orgOpt.get();

            if ("Deactivated".equalsIgnoreCase(org.getStatus())) {
                throw new Exception("Your organization account has been deactivated by the administrator.");
            }

            if (org.getPasswordHash().equals(password)) {
                return org;
            } else {
                throw new Exception("Invalid password. Please try again.");
            }
        } else {
            throw new Exception("No organization found with this email address.");
        }
    }

    public void updateOrganizationProfile(Integer orgId, String orgName, String orgType,
                                          String address, String coordinatorName,
                                          String email, String phone) throws Exception {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new Exception("Organization not found"));

        org.setOrgName(orgName);
        org.setOrgType(orgType);
        org.setAddress(address);
        org.setCoordinatorName(coordinatorName);
        org.setEmail(email);
        org.setPhone(phone);

        organizationRepository.save(org);
    }

    public void updateOrganizationPassword(Integer orgId, String newPassword) throws Exception {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new Exception("Organization not found"));

        org.setPasswordHash(newPassword);
        organizationRepository.save(org);
    }

    public Optional<Organization> findById(Integer orgId) {
        return organizationRepository.findById(orgId);
    }
}