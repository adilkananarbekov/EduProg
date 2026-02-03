package com.edu.edupage.dto;

import com.edu.edupage.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    
    // For students
    private String studentNumber;
    private Long classGroupId;
    private String classGroupName;
    
    // For teachers
    private String employeeNumber;
    private java.util.List<String> subjects;
}
