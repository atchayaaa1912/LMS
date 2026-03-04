package com.project.lms.controller;

import com.project.lms.entity.Module;
import com.project.lms.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping("/{courseId}/modules")
    public ResponseEntity<?> addModule(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> body) {

        String name = body.get("name");

        Module module = moduleService.addModule(courseId, name);

        return ResponseEntity.ok(module);
    }


    @GetMapping("/{courseId}")
    public ResponseEntity<?> getModules(@PathVariable Long courseId) {

        return ResponseEntity.ok(
                moduleService.getModules(courseId)
        );
    }


    @DeleteMapping("/modules/{moduleId}")
    public ResponseEntity<?> deleteModule(@PathVariable Long moduleId) {

        moduleService.deleteModule(moduleId);
        return ResponseEntity.ok("Module deleted successfully");
    }
}