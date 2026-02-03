package com.edu.edupage.controller;

import com.edu.edupage.dto.AuthResponse;
import com.edu.edupage.dto.LoginRequest;
import com.edu.edupage.dto.RegisterRequest;
import com.edu.edupage.entity.Role;
import com.edu.edupage.repository.UserRepository;
import com.edu.edupage.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/register/initial")
    public ResponseEntity<AuthResponse> registerInitialAdmin(@Valid @RequestBody RegisterRequest request) {
        // Security check: only allow if no admin exists
        if (userRepository.existsByRole(Role.ADMIN)) {
            throw new IllegalStateException("Initial admin already exists. Use /api/auth/register with admin credentials.");
        }
        
        // Force the role to be ADMIN for initial setup
        request.setRole(Role.ADMIN);
        return ResponseEntity.ok(authService.register(request));
    }
}
