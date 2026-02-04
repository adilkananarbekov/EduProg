package com.edu.edupage.controller;

import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Transactional(readOnly = true)
public class AdminController {

        private final UserRepository userRepository;
        private final StudentRepository studentRepository;
        private final TeacherRepository teacherRepository;
        private final ClassGroupRepository classGroupRepository;
        private final SubjectRepository subjectRepository;

        // ========== Users ==========
        @GetMapping("/users")
        public ResponseEntity<List<UserDTO>> getAllUsers() {
                return ResponseEntity.ok(
                                userRepository.findAll().stream()
                                                .map(this::mapToUserDTO)
                                                .collect(Collectors.toList()));
        }

        // ========== Students ==========
        @GetMapping("/students")
        public ResponseEntity<List<StudentDTO>> getAllStudents() {
                return ResponseEntity.ok(
                                studentRepository.findAll().stream()
                                                .map(this::mapToStudentDTO)
                                                .collect(Collectors.toList()));
        }

        @GetMapping("/students/class/{classGroupId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        public ResponseEntity<List<StudentDTO>> getStudentsByClass(@PathVariable Long classGroupId) {
                return ResponseEntity.ok(
                                studentRepository.findByClassGroupId(classGroupId).stream()
                                                .map(this::mapToStudentDTO)
                                                .collect(Collectors.toList()));
        }

        @GetMapping("/students/unassigned")
        public ResponseEntity<List<StudentDTO>> getUnassignedStudents() {
                return ResponseEntity.ok(
                                studentRepository.findAll().stream()
                                                .filter(s -> s.getClassGroup() == null)
                                                .map(this::mapToStudentDTO)
                                                .collect(Collectors.toList()));
        }

        @PutMapping("/students/{studentId}/class")
        public ResponseEntity<StudentDTO> updateStudentClass(
                        @PathVariable Long studentId,
                        @RequestBody UpdateStudentClassRequest request) {
                Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

                if (request.classGroupId() != null) {
                        ClassGroup classGroup = classGroupRepository.findById(request.classGroupId())
                                        .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", "id",
                                                        request.classGroupId()));
                        student.setClassGroup(classGroup);
                } else {
                        student.setClassGroup(null);
                }

                student = studentRepository.save(student);
                return ResponseEntity.ok(mapToStudentDTO(student));
        }

        @PutMapping("/students/bulk-assign")
        public ResponseEntity<List<StudentDTO>> bulkAssignStudentsToClass(@RequestBody BulkAssignRequest request) {
                ClassGroup classGroup = null;
                if (request.classGroupId() != null) {
                        classGroup = classGroupRepository.findById(request.classGroupId())
                                        .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", "id",
                                                        request.classGroupId()));
                }

                final ClassGroup finalClassGroup = classGroup;
                List<Student> students = studentRepository.findAllById(request.studentIds());
                if (students.size() != request.studentIds().size()) {
                        Set<Long> foundIds = students.stream().map(Student::getId).collect(Collectors.toSet());
                        for (Long id : request.studentIds()) {
                                if (!foundIds.contains(id)) {
                                        throw new ResourceNotFoundException("Student", "id", id);
                                }
                        }
                }

                students.forEach(s -> s.setClassGroup(finalClassGroup));
                students = studentRepository.saveAll(students);

                return ResponseEntity.ok(students.stream().map(this::mapToStudentDTO).collect(Collectors.toList()));
        }

        // ========== Teachers ==========
        @GetMapping("/teachers")
        public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
                return ResponseEntity.ok(
                                teacherRepository.findAll().stream()
                                                .map(this::mapToTeacherDTO)
                                                .collect(Collectors.toList()));
        }

        @PutMapping("/teachers/{teacherId}/subjects")
        public ResponseEntity<TeacherDTO> updateTeacherSubjects(
                        @PathVariable Long teacherId,
                        @RequestBody UpdateTeacherSubjectsRequest request) {
                Teacher teacher = teacherRepository.findById(teacherId)
                                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));

                Set<Long> subjectIds = new java.util.HashSet<>(request.subjectIds());
                List<Subject> subjects = subjectRepository.findAllById(subjectIds);

                if (subjects.size() != subjectIds.size()) {
                        Set<Long> foundIds = subjects.stream().map(Subject::getId).collect(Collectors.toSet());
                        for (Long id : subjectIds) {
                                if (!foundIds.contains(id)) {
                                        throw new ResourceNotFoundException("Subject", "id", id);
                                }
                        }
                }

                teacher.setSubjects(new java.util.HashSet<>(subjects));
                teacher = teacherRepository.save(teacher);
                return ResponseEntity.ok(mapToTeacherDTO(teacher));
        }

        // ========== Class Groups ==========
        @GetMapping("/class-groups")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        public ResponseEntity<List<ClassGroupDTO>> getAllClassGroups() {
                return ResponseEntity.ok(
                                classGroupRepository.findAll().stream()
                                                .map(this::mapToClassGroupDTO)
                                                .collect(Collectors.toList()));
        }

        @PostMapping("/class-groups")
        public ResponseEntity<ClassGroupDTO> createClassGroup(@RequestBody CreateClassGroupRequest request) {
                ClassGroup classGroup = ClassGroup.builder()
                                .name(request.name())
                                .grade(request.grade())
                                .build();
                classGroup = classGroupRepository.save(classGroup);
                return ResponseEntity.ok(mapToClassGroupDTO(classGroup));
        }

        @PutMapping("/class-groups/{id}")
        public ResponseEntity<ClassGroupDTO> updateClassGroup(
                        @PathVariable Long id,
                        @RequestBody CreateClassGroupRequest request) {
                ClassGroup classGroup = classGroupRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", "id", id));

                classGroup.setName(request.name());
                classGroup.setGrade(request.grade());
                classGroup = classGroupRepository.save(classGroup);
                return ResponseEntity.ok(mapToClassGroupDTO(classGroup));
        }

        @DeleteMapping("/class-groups/{id}")
        public ResponseEntity<Void> deleteClassGroup(@PathVariable Long id) {
                ClassGroup classGroup = classGroupRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("ClassGroup", "id", id));

                // Remove students from this class first
                studentRepository.findByClassGroupId(id).forEach(student -> {
                        student.setClassGroup(null);
                        studentRepository.save(student);
                });

                classGroupRepository.delete(classGroup);
                return ResponseEntity.noContent().build();
        }

        // ========== Subjects ==========
        @GetMapping("/subjects")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
                return ResponseEntity.ok(
                                subjectRepository.findAll().stream()
                                                .map(this::mapToSubjectDTO)
                                                .collect(Collectors.toList()));
        }

        @PostMapping("/subjects")
        public ResponseEntity<SubjectDTO> createSubject(@RequestBody CreateSubjectRequest request) {
                Subject subject = Subject.builder()
                                .name(request.name())
                                .description(request.description())
                                .hoursPerWeek(request.hoursPerWeek() != null ? request.hoursPerWeek() : 2)
                                .build();
                subject = subjectRepository.save(subject);
                return ResponseEntity.ok(mapToSubjectDTO(subject));
        }

        @PutMapping("/subjects/{id}")
        public ResponseEntity<SubjectDTO> updateSubject(
                        @PathVariable Long id,
                        @RequestBody CreateSubjectRequest request) {
                Subject subject = subjectRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));

                subject.setName(request.name());
                subject.setDescription(request.description());
                if (request.hoursPerWeek() != null) {
                        subject.setHoursPerWeek(request.hoursPerWeek());
                }
                subject = subjectRepository.save(subject);
                return ResponseEntity.ok(mapToSubjectDTO(subject));
        }

        @DeleteMapping("/subjects/{id}")
        public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
                if (!subjectRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Subject", "id", id);
                }
                subjectRepository.deleteById(id);
                return ResponseEntity.noContent().build();
        }

        // ========== Mappers ==========
        private UserDTO mapToUserDTO(User user) {
                return new UserDTO(
                                user.getId(),
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getRole());
        }

        private StudentDTO mapToStudentDTO(Student student) {
                return new StudentDTO(
                                student.getId(),
                                student.getUser().getId(),
                                student.getUser().getFullName(),
                                student.getUser().getEmail(),
                                student.getClassGroup() != null ? student.getClassGroup().getId() : null,
                                student.getClassGroup() != null ? student.getClassGroup().getName() : null,
                                student.getStudentNumber());
        }

        private TeacherDTO mapToTeacherDTO(Teacher teacher) {
                return new TeacherDTO(
                                teacher.getId(),
                                teacher.getUser().getId(),
                                teacher.getUser().getFullName(),
                                teacher.getUser().getEmail(),
                                teacher.getSubjects().stream().map(Subject::getName).collect(Collectors.toList()),
                                teacher.getEmployeeNumber());
        }

        private ClassGroupDTO mapToClassGroupDTO(ClassGroup classGroup) {
                long studentCount = studentRepository.findByClassGroupId(classGroup.getId()).size();
                return new ClassGroupDTO(
                                classGroup.getId(),
                                classGroup.getName(),
                                classGroup.getGrade(),
                                studentCount);
        }

        private SubjectDTO mapToSubjectDTO(Subject subject) {
                return new SubjectDTO(
                                subject.getId(),
                                subject.getName(),
                                subject.getDescription(),
                                subject.getHoursPerWeek());
        }

        // ========== DTOs ==========
        public record UserDTO(Long id, String email, String firstName, String lastName, Role role) {
        }

        public record StudentDTO(Long id, Long userId, String name, String email, Long classGroupId,
                        String classGroupName,
                        String studentNumber) {
        }

        public record TeacherDTO(Long id, Long userId, String name, String email, List<String> subjects,
                        String employeeNumber) {
        }

        public record ClassGroupDTO(Long id, String name, Integer grade, Long studentCount) {
        }

        public record SubjectDTO(Long id, String name, String description, Integer hoursPerWeek) {
        }

        // ========== Request DTOs ==========
        public record UpdateStudentClassRequest(Long classGroupId) {
        }

        public record BulkAssignRequest(List<Long> studentIds, Long classGroupId) {
        }

        public record CreateClassGroupRequest(String name, Integer grade) {
        }

        public record CreateSubjectRequest(String name, String description, Integer hoursPerWeek) {
        }

        public record UpdateTeacherSubjectsRequest(List<Long> subjectIds) {
        }
}
