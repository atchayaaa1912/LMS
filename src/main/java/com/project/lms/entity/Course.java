package com.project.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @Column(nullable = false)
    private Boolean active = true;

    private Long createdBy;
    private Long updatedBy;
    private Long organizationId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum CourseStatus {
        DRAFT,
        PUBLISHED
    }

}