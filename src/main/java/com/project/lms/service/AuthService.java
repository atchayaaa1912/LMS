package com.project.lms.service;

import com.project.lms.dto.*;

import com.project.lms.entity.Organization;
import com.project.lms.entity.Role;
import com.project.lms.entity.User;
import com.project.lms.entity.UserStatus;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.repository.*;
import com.project.lms.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public Map<String, Object> register(RegisterDTO request) {

        log.info("Register request received for email: {}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }


        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .organization(organization)
                .status(UserStatus.PENDING)
                .build();

        userRepository.save(user);

        return Map.of(
                "message", "Registration successful. Pending admin approval.",
                "id", user.getId()
        );
    }



    public Map<String, Object> login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getOrganization().getId().equals(request.getOrganizationId())) {
            throw new RuntimeException("Invalid organization");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            return Map.of(
                    "message", "Account pending approval",
                    "code", "ACCOUNT_PENDING"
            );
        }

        if (user.getStatus() == UserStatus.REJECTED) {
            return Map.of(
                    "message", "Account rejected",
                    "code", "ACCOUNT_REJECTED"
            );
        }

        String token = jwtService.generateToken(user);


        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .organizationId(user.getOrganization().getId())
                .build();

        return Map.of(
                "token", token,
                "user", responseDTO
        );
    }

}