package com.project.lms.service;

import com.project.lms.dto.OrganizationDTO;
import com.project.lms.entity.Organization;
import com.project.lms.entity.User;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.exception.ResourceNotFoundException;
import com.project.lms.exception.UnauthorizedActionException;
import com.project.lms.repository.OrganizationRepository;
import com.project.lms.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    private Organization dtoToEntity(OrganizationDTO dto){
        Organization entity = new Organization();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        return entity;
    }

    private OrganizationDTO entityTODto(Organization entity){
        OrganizationDTO dto=new OrganizationDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        return dto;
    }

    public User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(()-> new ResourceNotFoundException("Logged-in user not found"));
    }

    public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream().map(this::entityTODto).toList();
    }

    public OrganizationDTO createOrganization(OrganizationDTO organization) throws UnauthorizedActionException {
        User currentUser = getCurrentUser();
        if(!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())){
            throw new UnauthorizedActionException("Only admin can create organization");
        }

        if(organizationRepository.existsByCode(organization.getCode())){
            throw new DuplicateResourceException("Organization code already exits");
        }

        if (organizationRepository.existsByName(organization.getName())){
            throw new DuplicateResourceException("Orgaization name already exists");
        }

        Organization saved= organizationRepository.save(dtoToEntity(organization));
        return entityTODto(saved);
    }
}
