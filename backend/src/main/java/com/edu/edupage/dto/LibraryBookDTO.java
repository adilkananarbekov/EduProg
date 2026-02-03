package com.edu.edupage.dto;

import com.edu.edupage.entity.BookCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryBookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private Integer publishYear;
    private BookCategory category;
    private String description;
    private String coverImageUrl;
    private Integer totalCopies;
    private Integer availableCopies;
    private String location;
    private boolean isAvailable;
    private LocalDateTime createdAt;
}
