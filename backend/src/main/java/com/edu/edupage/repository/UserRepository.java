package com.edu.edupage.repository;

import com.edu.edupage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    
    boolean existsByRole(com.edu.edupage.entity.Role role);
}
