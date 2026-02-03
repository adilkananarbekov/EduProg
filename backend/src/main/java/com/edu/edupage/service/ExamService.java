package com.edu.edupage.service;

import com.edu.edupage.dto.CreateExamRequest;
import com.edu.edupage.dto.ExamDTO;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final ClassGroupRepository classGroupRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public List<ExamDTO> getExamsForClass(Long classGroupId) {
        return examRepository.findByClassGroupId(classGroupId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExamDTO> getUpcomingExamsForClass(Long classGroupId) {
        return examRepository.findUpcomingByClassGroup(classGroupId, LocalDate.now()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExamDTO> getExamsByTeacher(Long teacherId) {
        return examRepository.findByTeacherId(teacherId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExamDTO> getUpcomingExamsByTeacher(Long teacherId) {
        return examRepository.findUpcomingByTeacher(teacherId, LocalDate.now()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ExamDTO getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        return mapToDTO(exam);
    }

    public ExamDTO createExam(CreateExamRequest request, Long teacherId) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        ClassGroup classGroup = classGroupRepository.findById(request.getClassGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Class group not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        Exam exam = Exam.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(subject)
                .classGroup(classGroup)
                .teacher(teacher)
                .examDate(request.getExamDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .room(request.getRoom())
                .maxScore(request.getMaxScore())
                .duration(request.getDuration())
                .type(request.getType())
                .build();

        exam = examRepository.save(exam);

        // Notify students
        List<Student> students = studentRepository.findByClassGroupId(classGroup.getId());
        for (Student student : students) {
            notificationService.createNotification(
                    student.getUser().getId(),
                    "New Exam Scheduled: " + exam.getTitle(),
                    "Date: " + exam.getExamDate() + " - " + subject.getName(),
                    NotificationType.EVENT,
                    "EXAM",
                    exam.getId()
            );
        }

        return mapToDTO(exam);
    }

    public ExamDTO updateExam(Long id, CreateExamRequest request) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setExamDate(request.getExamDate());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setRoom(request.getRoom());
        exam.setMaxScore(request.getMaxScore());
        exam.setDuration(request.getDuration());
        exam.setType(request.getType());

        return mapToDTO(examRepository.save(exam));
    }

    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exam not found");
        }
        examRepository.deleteById(id);
    }

    private ExamDTO mapToDTO(Exam exam) {
        return ExamDTO.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .subjectId(exam.getSubject().getId())
                .subjectName(exam.getSubject().getName())
                .classGroupId(exam.getClassGroup().getId())
                .classGroupName(exam.getClassGroup().getName())
                .teacherId(exam.getTeacher().getId())
                .teacherName(exam.getTeacher().getUser().getFirstName() + " " + exam.getTeacher().getUser().getLastName())
                .examDate(exam.getExamDate())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .room(exam.getRoom())
                .maxScore(exam.getMaxScore())
                .duration(exam.getDuration())
                .type(exam.getType())
                .createdAt(exam.getCreatedAt())
                .build();
    }
}
