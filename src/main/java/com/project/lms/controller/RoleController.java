package com.project.lms.controller;

import com.project.lms.dto.RoleResponseDTO;
import com.project.lms.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> listRoles() {

        log.info("GET /api/roles called");

        List<RoleResponseDTO> roles = roleService.getAllRoles();

        return ResponseEntity.ok(roles);
    }
}