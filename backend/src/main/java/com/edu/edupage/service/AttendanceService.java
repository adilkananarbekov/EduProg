package com.edu.edupage.service;

import com.edu.edupage.dto.AttendanceDTO;
import com.edu.edupage.dto.MarkAttendanceRequest;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

        private final AttendanceRepository attendanceRepository;
        private final StudentRepository studentRepository;
        private final ScheduleRepository scheduleRepository;
        private final UserRepository userRepository;

        public List<AttendanceDTO> getStudentAttendance(Long studentId) {
                return attendanceRepository.findByStudentId(studentId)
                                .stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        public List<AttendanceDTO> getStudentAttendanceByDateRange(Long studentId, LocalDate startDate,
                        LocalDate endDate) {
                return attendanceRepository.findByStudentAndDateRange(studentId, startDate, endDate)
                                .stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        public Map<String, Long> getStudentAttendanceStats(Long studentId) {
                Map<String, Long> stats = new HashMap<>();
                stats.put("present", attendanceRepository.countByStudentAndStatus(studentId, AttendanceStatus.PRESENT));
                stats.put("absent", attendanceRepository.countByStudentAndStatus(studentId, AttendanceStatus.ABSENT));
                stats.put("late", attendanceRepository.countByStudentAndStatus(studentId, AttendanceStatus.LATE));
                stats.put("excused", attendanceRepository.countByStudentAndStatus(studentId, AttendanceStatus.EXCUSED));
                return stats;
        }

        @Transactional
        public List<AttendanceDTO> markAttendance(MarkAttendanceRequest request, Long markedByUserId) {
                Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id",
                                                request.getScheduleId()));

                User markedBy = userRepository.findById(markedByUserId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", markedByUserId));

                return request.getAttendanceRecords().stream()
                                .map(record -> {
                                        Student student = studentRepository.findById(record.getStudentId())
                                                        .orElseThrow(() -> new ResourceNotFoundException("Student",
                                                                        "id", record.getStudentId()));

                                        // Check if attendance already exists for this student/schedule/date
                                        Attendance attendance = attendanceRepository
                                                        .findByStudentIdAndScheduleIdAndDate(record.getStudentId(),
                                                                        request.getScheduleId(),
                                                                        request.getDate())
                                                        .orElse(Attendance.builder()
                                                                        .student(student)
                                                                        .schedule(schedule)
                                                                        .date(request.getDate())
                                                                        .build());

                                        // VALIDATION START
                                        // 1. Verify that 'markedBy' is the teacher assigned to this schedule (unless
                                        // Admin)
                                        if (!markedBy.getRole().equals(Role.ADMIN)) {
                                                Long assignedTeacherUserId = schedule.getTeacher().getUser().getId();
                                                if (!markedBy.getId().equals(assignedTeacherUserId)) {
                                                        // Optional: Allow if 'markedBy' is a substitute? taking strict
                                                        // Approach for MVP
                                                        throw new IllegalArgumentException(
                                                                        "Only the assigned teacher can mark attendance for this class.");
                                                }
                                        }

                                        // 2. Verify that the date matches the schedule's DayOfWeek
                                        if (schedule.getDayOfWeek() != request.getDate().getDayOfWeek()) {
                                                throw new IllegalArgumentException(
                                                                "Schedule is on " + schedule.getDayOfWeek() +
                                                                                ", but you are marking attendance for "
                                                                                + request.getDate().getDayOfWeek());
                                        }

                                        // 3. Verify date is not in the future
                                        if (request.getDate().isAfter(LocalDate.now())) {
                                                throw new IllegalArgumentException(
                                                                "Cannot mark attendance for future dates.");
                                        }
                                        // VALIDATION END

                                        attendance.setStatus(record.getStatus());
                                        attendance.setNotes(record.getNotes());
                                        attendance.setMarkedBy(markedBy);

                                        attendance = attendanceRepository.save(attendance);
                                        return mapToDTO(attendance);
                                })
                                .collect(Collectors.toList());
        }

        public List<AttendanceDTO> getScheduleAttendance(Long scheduleId, LocalDate date) {
                return attendanceRepository.findByScheduleId(scheduleId)
                                .stream()
                                .filter(a -> a.getDate().equals(date))
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        private AttendanceDTO mapToDTO(Attendance attendance) {
                return AttendanceDTO.builder()
                                .id(attendance.getId())
                                .studentId(attendance.getStudent().getId())
                                .studentName(attendance.getStudent().getUser().getFullName())
                                .scheduleId(attendance.getSchedule().getId())
                                .subjectName(attendance.getSchedule().getSubject().getName())
                                .date(attendance.getDate())
                                .status(attendance.getStatus())
                                .notes(attendance.getNotes())
                                .markedByName(attendance.getMarkedBy() != null ? attendance.getMarkedBy().getFullName()
                                                : null)
                                .markedAt(attendance.getMarkedAt())
                                .build();
        }
}
