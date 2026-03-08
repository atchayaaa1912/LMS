package com.project.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddModuleDTO {

    @NotNull(message = "COURSE_ID_REQUIRED")
    private Long course_id;   // reference to course

    @NotBlank(message = "MODULE_CODE_REQUIRED")
    @Size(max = 50, message = "MODULE_CODE_TOO_LONG")
    private String code;

    @NotBlank(message = "MODULE_TITLE_REQUIRED")
    @Size(max = 255, message = "MODULE_TITLE_TOO_LONG")
    private String title;

    @NotBlank(message = "MODULE_STATUS_REQUIRED")
    private String status;

    @NotNull(message = "CREATED_BY_REQUIRED")
    private Long created_by;
}