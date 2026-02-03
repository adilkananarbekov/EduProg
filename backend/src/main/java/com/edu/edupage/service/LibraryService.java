package com.edu.edupage.service;

import com.edu.edupage.dto.BookLoanDTO;
import com.edu.edupage.dto.LibraryBookDTO;
import com.edu.edupage.entity.*;
import com.edu.edupage.exception.ResourceNotFoundException;
import com.edu.edupage.repository.BookLoanRepository;
import com.edu.edupage.repository.LibraryBookRepository;
import com.edu.edupage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryService {

    private final LibraryBookRepository bookRepository;
    private final BookLoanRepository loanRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final int MAX_LOANS_PER_USER = 5;
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final int MAX_RENEWALS = 2;

    // Book management
    public List<LibraryBookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapBookToDTO)
                .collect(Collectors.toList());
    }

    public List<LibraryBookDTO> getAvailableBooks() {
        return bookRepository.findAvailableBooks().stream()
                .map(this::mapBookToDTO)
                .collect(Collectors.toList());
    }

    public List<LibraryBookDTO> searchBooks(String query) {
        return bookRepository.searchBooks(query).stream()
                .map(this::mapBookToDTO)
                .collect(Collectors.toList());
    }

    public List<LibraryBookDTO> getBooksByCategory(BookCategory category) {
        return bookRepository.findByCategory(category).stream()
                .map(this::mapBookToDTO)
                .collect(Collectors.toList());
    }

    public LibraryBookDTO getBookById(Long id) {
        LibraryBook book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return mapBookToDTO(book);
    }

    public LibraryBookDTO addBook(LibraryBookDTO request) {
        LibraryBook book = LibraryBook.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .publisher(request.getPublisher())
                .publishYear(request.getPublishYear())
                .category(request.getCategory())
                .description(request.getDescription())
                .coverImageUrl(request.getCoverImageUrl())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .location(request.getLocation())
                .build();
        return mapBookToDTO(bookRepository.save(book));
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    // Loan management
    public BookLoanDTO borrowBook(Long bookId, Long userId) {
        LibraryBook book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("Book is not available");
        }

        long activeLoans = loanRepository.countActiveLoans(userId);
        if (activeLoans >= MAX_LOANS_PER_USER) {
            throw new IllegalStateException("Maximum loan limit reached (" + MAX_LOANS_PER_USER + ")");
        }

        if (loanRepository.existsByBookIdAndUserIdAndStatus(bookId, userId, LoanStatus.BORROWED)) {
            throw new IllegalStateException("You already have this book borrowed");
        }

        // Create loan
        BookLoan loan = BookLoan.builder()
                .book(book)
                .user(user)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(LOAN_PERIOD_DAYS))
                .status(LoanStatus.BORROWED)
                .build();
        loan = loanRepository.save(loan);

        // Update available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return mapLoanToDTO(loan);
    }

    public BookLoanDTO returnBook(Long loanId) {
        BookLoan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalStateException("Book already returned");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);

        // Update available copies
        LibraryBook book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return mapLoanToDTO(loan);
    }

    public BookLoanDTO renewLoan(Long loanId) {
        BookLoan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.BORROWED) {
            throw new IllegalStateException("Cannot renew - book not currently borrowed");
        }

        if (loan.getRenewCount() >= MAX_RENEWALS) {
            throw new IllegalStateException("Maximum renewals reached (" + MAX_RENEWALS + ")");
        }

        loan.setDueDate(loan.getDueDate().plusDays(LOAN_PERIOD_DAYS));
        loan.setRenewCount(loan.getRenewCount() + 1);
        loanRepository.save(loan);

        return mapLoanToDTO(loan);
    }

    public List<BookLoanDTO> getMyLoans(Long userId) {
        return loanRepository.findByUserId(userId).stream()
                .map(this::mapLoanToDTO)
                .collect(Collectors.toList());
    }

    public List<BookLoanDTO> getMyActiveLoans(Long userId) {
        return loanRepository.findActiveLoansByUser(userId).stream()
                .map(this::mapLoanToDTO)
                .collect(Collectors.toList());
    }

    public List<BookLoanDTO> getOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now()).stream()
                .map(this::mapLoanToDTO)
                .collect(Collectors.toList());
    }

    private LibraryBookDTO mapBookToDTO(LibraryBook book) {
        return LibraryBookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .publishYear(book.getPublishYear())
                .category(book.getCategory())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .location(book.getLocation())
                .isAvailable(book.getAvailableCopies() > 0)
                .createdAt(book.getCreatedAt())
                .build();
    }

    private BookLoanDTO mapLoanToDTO(BookLoan loan) {
        return BookLoanDTO.builder()
                .id(loan.getId())
                .bookId(loan.getBook().getId())
                .bookTitle(loan.getBook().getTitle())
                .bookAuthor(loan.getBook().getAuthor())
                .userId(loan.getUser().getId())
                .userName(loan.getUser().getFirstName() + " " + loan.getUser().getLastName())
                .borrowDate(loan.getBorrowDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus())
                .renewCount(loan.getRenewCount())
                .isOverdue(loan.getStatus() == LoanStatus.BORROWED && loan.getDueDate().isBefore(LocalDate.now()))
                .createdAt(loan.getCreatedAt())
                .build();
    }
}
