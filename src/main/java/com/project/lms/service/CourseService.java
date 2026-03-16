package com.project.lms.service;

import com.project.lms.dto.AddCourseDTO;
import com.project.lms.dto.CourseExportDTO;
import com.project.lms.entity.Course;
import com.project.lms.entity.Course.CourseStatus;
import com.project.lms.entity.User;
import com.project.lms.entity.Visibility;
import com.project.lms.exception.DuplicateResourceException;
import com.project.lms.exception.ResourceNotFoundException;
import com.project.lms.exception.UnauthorizedActionException;
import com.project.lms.repository.CourseRepository;
import com.project.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private Course dtoToEntity(AddCourseDTO dto) {

        return Course.builder()
                .code(dto.getCode())
                .title(dto.getTitle())
                .status(CourseStatus.valueOf(dto.getStatus()))
                .visibility(Visibility.PUBLIC)
                .active(true)
                .createdBy(dto.getCreated_by())
                .updatedBy(dto.getCreated_by())
                .organizationId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }



    public Object addCourse(AddCourseDTO dto) {

        if (dto.getCode() == null ||
                dto.getTitle() == null ||
                dto.getStatus() == null ||
                dto.getCreated_by() == null) {

            return Map.of(
                    "message",
                    "COURSE_REQUIRED_FIELDS"
            );
        }

        if (courseRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("COURSE_CODE_EXISTS");
        }
        Authentication auth =SecurityContextHolder
                .getContext()
                .getAuthentication();
        String userName = auth.getName();
        User currentUser=userRepository.findByEmail(userName).orElseThrow(()->new ResourceNotFoundException("USER_NOT_FOUND"));
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
            throw new UnauthorizedActionException("ACCESS_DENIED");
        }
        Course saved = courseRepository.save(dtoToEntity(dto));
        if (saved.getStatus() == CourseStatus.PUBLISHED) {

            List<User> students = userRepository.findAll();

            for (User student : students) {
                try {
                    emailService.sendCoursePublishedEmail(
                            student.getEmail(),
                            student.getName(),
                            saved.getTitle()
                    );
                } catch (Exception e) {
                    log.error("Failed to send email to {}", student.getEmail(), e);
                }
            }
            System.out.println("Course publish emails sent");
        }
        Map<String, Object> res = new java.util.HashMap<>();

        res.put("id", saved.getId());
        res.put("code", saved.getCode());
        res.put("title", saved.getTitle());
        res.put("status", saved.getStatus() != null ? saved.getStatus().name() : null);
        res.put("active", saved.getActive());
        res.put("created_by", saved.getCreatedBy());
        res.put("updated_by", saved.getUpdatedBy());
        res.put("organization_id", saved.getOrganizationId());
        res.put("visibility", saved.getVisibility() != null ? saved.getVisibility().name() : "PUBLIC");

        return res;
    }
    public Map<String, String> publishCourse(Long id) {
        log.info("Publishing course ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found for publishing: {}", id);
                    return new ResourceNotFoundException("COURSE_NOT_FOUND");
                });

        if (course.getStatus() == CourseStatus.PUBLISHED) {
            log.warn("Course already published: {}", id);
            throw new IllegalStateException("COURSE_ALREADY_PUBLISHED");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);

        log.info("Course published successfully: {}", id);

        User creator = userRepository.findById(course.getCreatedBy())
                .orElseThrow(() -> {
                    log.error("Course creator not found: {}", course.getCreatedBy());
                    return new ResourceNotFoundException("USER_NOT_FOUND");
                });

        emailService.sendCoursePublishedEmail(
                creator.getEmail(),
                creator.getName(),
                course.getTitle()
        );
        log.info("Course publication email sent to: {}", creator.getEmail());

        return Map.of("message", "COURSE_PUBLISHED_SUCCESS");
    }

    public List<CourseExportDTO> getAllCourses() {

        return courseRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    public CourseExportDTO getCourseById(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("COURSE_NOT_FOUND"));

        return entityToDto(course);
    }

    public Map<String, Object> exportCourses() {

        List<CourseExportDTO> courses = courseRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();

        return Map.of(
                "message", "COURSES_EXPORTED_SUCCESS",
                "data", courses
        );
    }

    private CourseExportDTO entityToDto(Course course) {

        return CourseExportDTO.builder()
                .id(course.getId())
                .code(course.getCode())
                .title(course.getTitle())
                .status(course.getStatus().name())
                .active(course.getActive())
                .created_at(course.getCreatedAt())
                .created_by(course.getCreatedBy())
                .updated_at(course.getUpdatedAt())
                .updated_by(course.getUpdatedBy())
                .organization_id(course.getOrganizationId())
                .visibility(course.getVisibility() != null
                        ? course.getVisibility().name()
                        : "PUBLIC")
                .build();
    }
}