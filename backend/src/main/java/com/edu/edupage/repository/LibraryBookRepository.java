package com.edu.edupage.repository;

import com.edu.edupage.entity.BookCategory;
import com.edu.edupage.entity.LibraryBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryBookRepository extends JpaRepository<LibraryBook, Long> {

    List<LibraryBook> findByCategory(BookCategory category);
    
    List<LibraryBook> findByTitleContainingIgnoreCase(String title);
    
    List<LibraryBook> findByAuthorContainingIgnoreCase(String author);
    
    @Query("SELECT b FROM LibraryBook b WHERE b.availableCopies > 0")
    List<LibraryBook> findAvailableBooks();
    
    @Query("SELECT b FROM LibraryBook b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<LibraryBook> searchBooks(@Param("query") String query);
    
    List<LibraryBook> findByIsbnContaining(String isbn);
}
