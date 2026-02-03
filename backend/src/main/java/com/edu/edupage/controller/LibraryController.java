package com.edu.edupage.controller;

import com.edu.edupage.dto.BookLoanDTO;
import com.edu.edupage.dto.LibraryBookDTO;
import com.edu.edupage.entity.BookCategory;
import com.edu.edupage.entity.User;
import com.edu.edupage.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    // Book endpoints
    @GetMapping("/books")
    public ResponseEntity<List<LibraryBookDTO>> getAllBooks() {
        return ResponseEntity.ok(libraryService.getAllBooks());
    }

    @GetMapping("/books/available")
    public ResponseEntity<List<LibraryBookDTO>> getAvailableBooks() {
        return ResponseEntity.ok(libraryService.getAvailableBooks());
    }

    @GetMapping("/books/search")
    public ResponseEntity<List<LibraryBookDTO>> searchBooks(@RequestParam String query) {
        return ResponseEntity.ok(libraryService.searchBooks(query));
    }

    @GetMapping("/books/category/{category}")
    public ResponseEntity<List<LibraryBookDTO>> getBooksByCategory(@PathVariable BookCategory category) {
        return ResponseEntity.ok(libraryService.getBooksByCategory(category));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<LibraryBookDTO> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(libraryService.getBookById(id));
    }

    @PostMapping("/books")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LibraryBookDTO> addBook(@RequestBody LibraryBookDTO book) {
        return ResponseEntity.ok(libraryService.addBook(book));
    }

    @DeleteMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        libraryService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // Loan endpoints
    @PostMapping("/books/{bookId}/borrow")
    public ResponseEntity<BookLoanDTO> borrowBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(libraryService.borrowBook(bookId, user.getId()));
    }

    @PostMapping("/loans/{loanId}/return")
    public ResponseEntity<BookLoanDTO> returnBook(@PathVariable Long loanId) {
        return ResponseEntity.ok(libraryService.returnBook(loanId));
    }

    @PostMapping("/loans/{loanId}/renew")
    public ResponseEntity<BookLoanDTO> renewLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(libraryService.renewLoan(loanId));
    }

    @GetMapping("/my-loans")
    public ResponseEntity<List<BookLoanDTO>> getMyLoans(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(libraryService.getMyLoans(user.getId()));
    }

    @GetMapping("/my-loans/active")
    public ResponseEntity<List<BookLoanDTO>> getMyActiveLoans(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(libraryService.getMyActiveLoans(user.getId()));
    }

    @GetMapping("/loans/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookLoanDTO>> getOverdueLoans() {
        return ResponseEntity.ok(libraryService.getOverdueLoans());
    }
}
