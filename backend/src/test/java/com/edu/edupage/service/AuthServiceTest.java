package com.edu.edupage.service;

import com.edu.edupage.dto.RegisterRequest;
import com.edu.edupage.entity.Role;
import com.edu.edupage.entity.Subject;
import com.edu.edupage.entity.Teacher;
import com.edu.edupage.entity.User;
import com.edu.edupage.repository.*;
import com.edu.edupage.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private ClassGroupRepository classGroupRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testRegisterTeacher_Optimized() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("teacher_opt@example.com");
        request.setPassword("password");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setRole(Role.TEACHER);
        request.setSubjectIds(Arrays.asList(1L, 2L, 3L));

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(2L)
                .email(request.getEmail())
                .role(Role.TEACHER)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Mock SubjectRepository findAllById
        List<Subject> subjects = Arrays.asList(
            Subject.builder().id(1L).name("Math").build(),
            Subject.builder().id(2L).name("Physics").build(),
            Subject.builder().id(3L).name("Chemistry").build()
        );
        when(subjectRepository.findAllById(any())).thenReturn(subjects);

        when(jwtTokenProvider.generateToken(anyString())).thenReturn("test-token");

        // When
        authService.register(request);

        // Then
        // Verify findAllById is called once
        verify(subjectRepository, times(1)).findAllById(any());
        // Verify findById is NOT called
        verify(subjectRepository, times(0)).findById(anyLong());
    }

    @Test
    public void testRegisterTeacher_MissingSubject() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("teacher_fail@example.com");
        request.setPassword("password");
        request.setFirstName("Jim");
        request.setLastName("Beam");
        request.setRole(Role.TEACHER);
        request.setSubjectIds(Arrays.asList(1L, 99L)); // 99L missing

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(3L)
                .email(request.getEmail())
                .role(Role.TEACHER)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Mock SubjectRepository findAllById returning only one subject
        List<Subject> subjects = Arrays.asList(
            Subject.builder().id(1L).name("Math").build()
        );
        when(subjectRepository.findAllById(any())).thenReturn(subjects);

        // When & Then
        try {
            authService.register(request);
        } catch (com.edu.edupage.exception.ResourceNotFoundException e) {
            // Expected
            return;
        }

        // Fail if no exception
        org.junit.jupiter.api.Assertions.fail("Should have thrown ResourceNotFoundException");
    }

}
