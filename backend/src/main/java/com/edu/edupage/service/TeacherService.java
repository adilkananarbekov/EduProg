package com.edu.edupage.service;

import com.edu.edupage.dto.ScheduleDTO;
import com.edu.edupage.dto.SubjectDTO;
import com.edu.edupage.dto.TeacherDTO;
import com.edu.edupage.entity.Teacher;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.ScheduleRepository;
import com.edu.edupage.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherService {

        private final TeacherRepository teacherRepository;
        private final ScheduleRepository scheduleRepository;

        public List<TeacherDTO> getAllTeachers() {
                return teacherRepository.findAll().stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        public TeacherDTO getTeacherById(Long id) {
                Teacher teacher = teacherRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));
                return mapToDTO(teacher);
        }

        public TeacherDTO getTeacherByUserId(Long userId) {
                Teacher teacher = teacherRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "userId", userId));
                return mapToDTO(teacher);
        }

        public List<ScheduleDTO> getTeacherSchedule(Long teacherId) {
                if (!teacherRepository.existsById(teacherId)) {
                        throw new ResourceNotFoundException("Teacher", "id", teacherId);
                }
                return scheduleRepository.findWeeklyScheduleByTeacher(teacherId).stream()
                                .map(schedule -> ScheduleDTO.builder()
                                                .id(schedule.getId())
                                                .classGroupId(schedule.getClassGroup().getId())
                                                .classGroupName(schedule.getClassGroup().getName())
                                                .teacherId(schedule.getTeacher().getId())
                                                .teacherName(schedule.getTeacher().getUser().getFullName())
                                                .subjectId(schedule.getSubject().getId())
                                                .subjectName(schedule.getSubject().getName())
                                                .dayOfWeek(schedule.getDayOfWeek())
                                                .startTime(schedule.getStartTime())
                                                .endTime(schedule.getEndTime())
                                                .room(schedule.getClassroom() != null
                                                                ? (schedule.getClassroom().getName() != null
                                                                                ? schedule.getClassroom().getName()
                                                                                : schedule.getClassroom()
                                                                                                .getRoomNumber())
                                                                : null)
                                                .lessonNumber(schedule.getLessonNumber())
                                                .build())
                                .collect(Collectors.toList());
        }

        private TeacherDTO mapToDTO(Teacher teacher) {
                return TeacherDTO.builder()
                                .id(teacher.getId())
                                .userId(teacher.getUser().getId())
                                .firstName(teacher.getUser().getFirstName())
                                .lastName(teacher.getUser().getLastName())
                                .fullName(teacher.getUser().getFullName())
                                .email(teacher.getUser().getEmail())
                                .employeeNumber(teacher.getEmployeeNumber())
                                .subjects(teacher.getSubjects().stream()
                                                .map(subject -> SubjectDTO.builder()
                                                                .id(subject.getId())
                                                                .name(subject.getName())
                                                                .description(subject.getDescription())
                                                                .hoursPerWeek(subject.getHoursPerWeek())
                                                                .build())
                                                .collect(Collectors.toList()))
                                .build();
        }
}
