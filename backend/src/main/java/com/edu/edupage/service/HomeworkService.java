package com.edu.edupage.service;

import com.edu.edupage.dto.*;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final SubmissionRepository submissionRepository;
    private final SubjectRepository subjectRepository;
    private final ClassGroupRepository classGroupRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public List<HomeworkDTO> getHomeworkForClass(Long classGroupId, Long studentId) {
        return homeworkRepository.findByClassGroupId(classGroupId).stream()
                .map(h -> mapToDTO(h, studentId))
                .collect(Collectors.toList());
    }

    public List<HomeworkDTO> getPendingHomeworkForClass(Long classGroupId, Long studentId) {
        return homeworkRepository.findPendingByClassGroup(classGroupId, LocalDate.now()).stream()
                .map(h -> mapToDTO(h, studentId))
                .collect(Collectors.toList());
    }

    public List<HomeworkDTO> getHomeworkByTeacher(Long teacherId) {
        return homeworkRepository.findByTeacherId(teacherId).stream()
                .map(h -> mapToDTO(h, null))
                .collect(Collectors.toList());
    }

    public List<HomeworkDTO> getPendingHomeworkByTeacher(Long teacherId) {
        return homeworkRepository.findPendingByTeacher(teacherId, LocalDate.now()).stream()
                .map(h -> mapToDTO(h, null))
                .collect(Collectors.toList());
    }

    public HomeworkDTO getHomeworkById(Long id, Long studentId) {
        Homework homework = homeworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Homework not found"));
        return mapToDTO(homework, studentId);
    }

    public HomeworkDTO createHomework(CreateHomeworkRequest request, Long teacherId) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        ClassGroup classGroup = classGroupRepository.findById(request.getClassGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Class group not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        Homework homework = Homework.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(subject)
                .classGroup(classGroup)
                .teacher(teacher)
                .dueDate(request.getDueDate())
                .maxScore(request.getMaxScore())
                .type(request.getType())
                .build();

        homework = homeworkRepository.save(homework);

        // Notify students in the class
        List<Student> students = studentRepository.findByClassGroupId(classGroup.getId());
        for (Student student : students) {
            notificationService.createNotification(
                    student.getUser().getId(),
                    "New Homework: " + homework.getTitle(),
                    "Due: " + homework.getDueDate() + " - " + subject.getName(),
                    NotificationType.HOMEWORK,
                    "HOMEWORK",
                    homework.getId()
            );
        }

        return mapToDTO(homework, null);
    }

    public HomeworkDTO updateHomework(Long id, CreateHomeworkRequest request) {
        Homework homework = homeworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Homework not found"));

        homework.setTitle(request.getTitle());
        homework.setDescription(request.getDescription());
        homework.setDueDate(request.getDueDate());
        homework.setMaxScore(request.getMaxScore());
        homework.setType(request.getType());

        return mapToDTO(homeworkRepository.save(homework), null);
    }

    public void deleteHomework(Long id) {
        if (!homeworkRepository.existsById(id)) {
            throw new ResourceNotFoundException("Homework not found");
        }
        homeworkRepository.deleteById(id);
    }

    // Submission methods
    public SubmissionDTO submitHomework(SubmitHomeworkRequest request, Long studentId) {
        Homework homework = homeworkRepository.findById(request.getHomeworkId())
                .orElseThrow(() -> new ResourceNotFoundException("Homework not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Check if already submitted
        if (submissionRepository.existsByHomeworkIdAndStudentId(homework.getId(), studentId)) {
            throw new IllegalArgumentException("You have already submitted this homework");
        }

        SubmissionStatus status = LocalDate.now().isAfter(homework.getDueDate()) 
                ? SubmissionStatus.LATE 
                : SubmissionStatus.SUBMITTED;

        Submission submission = Submission.builder()
                .homework(homework)
                .student(student)
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .status(status)
                .build();

        submission = submissionRepository.save(submission);

        // Notify teacher
        notificationService.createNotification(
                homework.getTeacher().getUser().getId(),
                "New Submission: " + homework.getTitle(),
                student.getUser().getFirstName() + " " + student.getUser().getLastName() + " submitted homework",
                NotificationType.HOMEWORK,
                "SUBMISSION",
                submission.getId()
        );

        return mapSubmissionToDTO(submission);
    }

    public List<SubmissionDTO> getSubmissionsForHomework(Long homeworkId) {
        return submissionRepository.findByHomeworkId(homeworkId).stream()
                .map(this::mapSubmissionToDTO)
                .collect(Collectors.toList());
    }

    public List<SubmissionDTO> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId).stream()
                .map(this::mapSubmissionToDTO)
                .collect(Collectors.toList());
    }

    public SubmissionDTO gradeSubmission(Long submissionId, GradeSubmissionRequest request, Long teacherId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setGradedBy(teacher);
        submission.setGradedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.GRADED);

        submission = submissionRepository.save(submission);

        // Notify student
        notificationService.createNotification(
                submission.getStudent().getUser().getId(),
                "Homework Graded: " + submission.getHomework().getTitle(),
                "Score: " + request.getScore() + "/" + submission.getHomework().getMaxScore(),
                NotificationType.GRADE,
                "SUBMISSION",
                submission.getId()
        );

        return mapSubmissionToDTO(submission);
    }

    private HomeworkDTO mapToDTO(Homework homework, Long studentId) {
        boolean isSubmitted = false;
        if (studentId != null) {
            isSubmitted = submissionRepository.existsByHomeworkIdAndStudentId(homework.getId(), studentId);
        }

        return HomeworkDTO.builder()
                .id(homework.getId())
                .title(homework.getTitle())
                .description(homework.getDescription())
                .subjectId(homework.getSubject().getId())
                .subjectName(homework.getSubject().getName())
                .classGroupId(homework.getClassGroup().getId())
                .classGroupName(homework.getClassGroup().getName())
                .teacherId(homework.getTeacher().getId())
                .teacherName(homework.getTeacher().getUser().getFirstName() + " " + homework.getTeacher().getUser().getLastName())
                .dueDate(homework.getDueDate())
                .maxScore(homework.getMaxScore())
                .type(homework.getType())
                .totalSubmissions((int) submissionRepository.countByHomeworkId(homework.getId()))
                .gradedSubmissions((int) submissionRepository.countByHomeworkIdAndStatus(homework.getId(), SubmissionStatus.GRADED))
                .isSubmitted(isSubmitted)
                .createdAt(homework.getCreatedAt())
                .build();
    }

    private SubmissionDTO mapSubmissionToDTO(Submission submission) {
        return SubmissionDTO.builder()
                .id(submission.getId())
                .homeworkId(submission.getHomework().getId())
                .homeworkTitle(submission.getHomework().getTitle())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getUser().getFirstName() + " " + submission.getStudent().getUser().getLastName())
                .content(submission.getContent())
                .attachmentUrl(submission.getAttachmentUrl())
                .submittedAt(submission.getSubmittedAt())
                .score(submission.getScore())
                .maxScore(submission.getHomework().getMaxScore())
                .feedback(submission.getFeedback())
                .gradedByName(submission.getGradedBy() != null ? 
                        submission.getGradedBy().getUser().getFirstName() + " " + submission.getGradedBy().getUser().getLastName() : null)
                .gradedAt(submission.getGradedAt())
                .status(submission.getStatus())
                .build();
    }
}
