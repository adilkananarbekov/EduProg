package com.edu.edupage.controller;

import com.edu.edupage.entity.ClassGroup;
import com.edu.edupage.repository.ClassGroupRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "Class Groups", description = "Class Group information")
public class ClassGroupController {

    private final ClassGroupRepository classGroupRepository;

    @GetMapping
    @Operation(summary = "Get all class groups", description = "Returns a list of all class groups")
    public ResponseEntity<List<ClassGroup>> getAllClassGroups() {
        return ResponseEntity.ok(classGroupRepository.findAll());
    }
}
