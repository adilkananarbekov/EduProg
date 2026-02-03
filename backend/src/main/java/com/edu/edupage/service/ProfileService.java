package com.edu.edupage.service;

import com.edu.edupage.dto.*;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProfileDTO.ProfileDTOBuilder builder = ProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole());

        switch (user.getRole()) {
            case STUDENT -> {
                studentRepository.findByUserId(userId).ifPresent(student -> {
                    builder.studentNumber(student.getStudentNumber());
                    builder.classGroupId(student.getClassGroup() != null ? student.getClassGroup().getId() : null);
                    builder.classGroupName(student.getClassGroup() != null ? student.getClassGroup().getName() : null);
                });
            }
            case TEACHER -> {
                teacherRepository.findByUserId(userId).ifPresent(teacher -> {
                    builder.employeeNumber(teacher.getEmployeeNumber());
                    builder.subjects(teacher.getSubjects().stream()
                            .map(Subject::getName)
                            .collect(Collectors.toList()));
                });
            }
            default -> {
                // Admin - no extra fields
            }
        }

        return builder.build();
    }

    public ProfileDTO updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        userRepository.save(user);

        return getProfile(userId);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Verify new passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
