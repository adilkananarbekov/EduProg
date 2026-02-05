package com.edu.edupage.config;

import com.edu.edupage.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/**", "/actuator/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml")
                        .permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public library book browsing (no auth required)
                        .requestMatchers(HttpMethod.GET, "/api/library/books/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/schedule/class/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/schedule/classroom/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/schedule/teacher/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/classes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/teachers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/admin/classrooms/**").permitAll()
                        // Shared endpoints for Teachers and Admins
                        // Shared endpoints for Teachers and Admins
                        .requestMatchers("/api/admin/students/class/**").hasAnyRole("TEACHER", "ADMIN", "OPERATOR")
                        .requestMatchers("/api/admin/class-groups").hasAnyRole("TEACHER", "ADMIN", "OPERATOR")
                        .requestMatchers("/api/admin/subjects").hasAnyRole("TEACHER", "ADMIN", "OPERATOR")
                        // Admin only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/teacher/**").hasAnyRole("TEACHER", "ADMIN", "OPERATOR")
                        // Schedule Management
                        .requestMatchers(HttpMethod.POST, "/api/schedule/**").hasAnyRole("ADMIN", "TEACHER", "OPERATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/schedule/**")
                        .hasAnyRole("ADMIN", "TEACHER", "OPERATOR")
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
