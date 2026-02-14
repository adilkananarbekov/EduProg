package com.edu.edupage.service;

import com.edu.edupage.dto.*;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.*;
import com.edu.edupage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassGroupRepository classGroupRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ActivityLogService activityLogService;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(authentication);

        return buildAuthResponse(user, token);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .build();

        user = userRepository.save(user);

        // Create role-specific profile
        if (request.getRole() == Role.STUDENT) {
            Student student = Student.builder()
                    .user(user)
                    .build();

            if (request.getClassGroupId() != null) {
                ClassGroup classGroup = classGroupRepository.findById(request.getClassGroupId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("ClassGroup", "id", request.getClassGroupId()));
                student.setClassGroup(classGroup);
            }

            studentRepository.save(student);
        } else if (request.getRole() == Role.TEACHER) {
            Teacher teacher = Teacher.builder()
                    .user(user)
                    .build();

            if (request.getSubjectIds() != null && !request.getSubjectIds().isEmpty()) {
                Set<Subject> subjects = new HashSet<>();
                for (Long subjectId : request.getSubjectIds()) {
                    Subject subject = subjectRepository.findById(subjectId)
                            .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));
                    subjects.add(subject);
                }
                teacher.setSubjects(subjects);
            }

            teacherRepository.save(teacher);
        }

        activityLogService.logActivity(
                "USER_REGISTERED",
                "New " + request.getRole().toString().toLowerCase() + " registered: " + user.getFirstName() + " "
                        + user.getLastName(),
                user.getId());

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        AuthResponse.AuthResponseBuilder builder = AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole());

        if (user.getRole() == Role.STUDENT) {
            studentRepository.findByUserId(user.getId()).ifPresent(student -> {
                builder.profileId(student.getId());
                if (student.getClassGroup() != null) {
                    builder.classGroupId(student.getClassGroup().getId());
                }
            });
        } else if (user.getRole() == Role.TEACHER) {
            teacherRepository.findByUserId(user.getId()).ifPresent(teacher -> {
                builder.profileId(teacher.getId());
            });
        }

        return builder.build();
    }
}
