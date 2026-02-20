package com.project.lms.service;

import com.project.lms.dto.RoleResponseDTO;
import com.project.lms.repository.RoleRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Builder
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleResponseDTO> getAllRoles() {

        log.info("Fetching all roles");

        return roleRepository.findAll()
                .stream()
                .map(role -> {
                    RoleResponseDTO dto = new RoleResponseDTO();
                    dto.setId(role.getId());
                    dto.setName(role.getName());
                    dto.setCreatedAt(role.getCreatedAt());
                    dto.setUpdatedAt(role.getUpdatedAt());
                    return dto;
                })
                .toList();
    }
}