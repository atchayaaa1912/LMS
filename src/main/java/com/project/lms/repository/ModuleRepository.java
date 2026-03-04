package com.project.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.project.lms.entity.Module;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCourseId(Long courseId);
}