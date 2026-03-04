package com.project.lms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class OrganizationDTO {

    @NotBlank(message = "Organization name is required")
    private String name;

    @NotBlank(message = "Organization code is required")
    private String code;

    private Long id;
}

