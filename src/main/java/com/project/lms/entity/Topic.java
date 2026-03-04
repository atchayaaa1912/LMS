package com.project.lms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "topics")
@Getter
@Setter
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long moduleId;

    private String name;

    private Integer topicOrder = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}