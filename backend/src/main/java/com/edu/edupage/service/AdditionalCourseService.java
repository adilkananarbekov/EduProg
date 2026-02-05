package com.edu.edupage.service;

import com.edu.edupage.dto.AdditionalCourseDTO;
import com.edu.edupage.entity.AdditionalCourse;
import com.edu.edupage.entity.Student;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.AdditionalCourseRepository;
import com.edu.edupage.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdditionalCourseService {

    private final AdditionalCourseRepository additionalCourseRepository;
    private final StudentRepository studentRepository;

    public List<AdditionalCourseDTO> getAllCourses() {
        return additionalCourseRepository.findAllByOrderByNameAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AdditionalCourseDTO getCourseById(Long id) {
        AdditionalCourse course = additionalCourseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdditionalCourse", "id", id));
        return mapToDTO(course);
    }

    public List<AdditionalCourseDTO> getStudentCourses(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        return student.getAdditionalCourses().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AdditionalCourseDTO> getStudentCoursesByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId", userId));
        return student.getAdditionalCourses().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        AdditionalCourse course = additionalCourseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("AdditionalCourse", "id", courseId));

        // Check capacity
        if (course.getMaxCapacity() != null &&
                course.getEnrolledStudents().size() >= course.getMaxCapacity()) {
            throw new IllegalStateException("Course has reached maximum capacity");
        }

        student.getAdditionalCourses().add(course);
        studentRepository.save(student);
    }

    public void unenrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        AdditionalCourse course = additionalCourseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("AdditionalCourse", "id", courseId));

        student.getAdditionalCourses().remove(course);
        studentRepository.save(student);
    }

    private AdditionalCourseDTO mapToDTO(AdditionalCourse course) {
        return AdditionalCourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .instructor(course.getInstructor())
                .dayOfWeek(course.getDayOfWeek())
                .startTime(course.getStartTime())
                .endTime(course.getEndTime())
                .room(course.getRoom())
                .maxCapacity(course.getMaxCapacity())
                .enrolledCount(course.getEnrolledStudents().size())
                .build();
    }
}
