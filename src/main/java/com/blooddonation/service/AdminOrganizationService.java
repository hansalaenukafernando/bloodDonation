package com.blooddonation.service;

import com.blooddonation.entity.Organization;
import com.blooddonation.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminOrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    public void updateOrganization(Integer orgId, String orgName, String orgType, String regNumber,
                                   String address, String coordinatorName, String email, String phone) throws Exception {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new Exception("Organization not found"));

        org.setOrgName(orgName);
        org.setOrgType(orgType);
        org.setRegNumber(regNumber);
        org.setAddress(address);
        org.setCoordinatorName(coordinatorName);
        org.setEmail(email);
        org.setPhone(phone);

        organizationRepository.save(org);
    }

    public void updateOrganizationStatus(Integer orgId, String status) throws Exception {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new Exception("Organization not found"));

        org.setStatus(status);
        organizationRepository.save(org);
    }
}