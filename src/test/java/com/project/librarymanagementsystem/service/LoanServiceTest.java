package com.project.librarymanagementsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.project.librarymanagementsystem.entity.BookCopy;
import com.project.librarymanagementsystem.entity.Borrower;
import com.project.librarymanagementsystem.entity.CopyStatus;
import com.project.librarymanagementsystem.entity.Loan;
import com.project.librarymanagementsystem.entity.LoanStatus;
import com.project.librarymanagementsystem.exception.BusinessException;
import com.project.librarymanagementsystem.repository.BookCopyRepository;
import com.project.librarymanagementsystem.repository.BorrowerRepository;
import com.project.librarymanagementsystem.repository.LoanRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private BookCopyRepository copyRepository;
    @Mock
    private BorrowerRepository borrowerRepository;
    @InjectMocks
    private LoanServiceImpl loanService;

    @BeforeEach
    void setUp() {
        loanService = new LoanServiceImpl(loanRepository, copyRepository, borrowerRepository, 2.5);
    }

    @Test
    void issueLoan_success() {
        Long copyId = 1L;
        Long borrowerId = 2L;
        BookCopy copy = new BookCopy();
        copy.setCopyId(copyId);
        copy.setBarcode("BC-001");
        copy.setStatus(CopyStatus.AVAILABLE);
        Borrower borrower = new Borrower();
        borrower.setBorrowerId(borrowerId);
        borrower.setActive(true);
        borrower.setMaxAllowedLoans(5);
        when(copyRepository.findById(copyId)).thenReturn(Optional.of(copy));
        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(loanRepository.findActiveLoansByBorrowerId(borrowerId)).thenReturn(Collections.emptyList());
        when(copyRepository.save(any(BookCopy.class))).thenAnswer(inv -> inv.getArgument(0));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l.setLoanId(100L);
            return l;
        });
        Loan created = loanService.issueLoan(copyId, borrowerId, LocalDate.now().plusDays(7));
        assertThat(created).isNotNull();
        assertThat(created.getLoanId()).isEqualTo(100L);
        assertThat(created.getStatus()).isEqualTo(LoanStatus.BORROWED);
        verify(copyRepository, times(1)).save(copy);
        assertThat(copy.getStatus()).isEqualTo(CopyStatus.ON_LOAN);
    }

    @Test
    void issueLoan_copyNotAvailable() {
        Long copyId = 3L;
        BookCopy copy = new BookCopy();
        copy.setCopyId(copyId);
        copy.setStatus(CopyStatus.ON_LOAN);
        when(copyRepository.findById(copyId)).thenReturn(Optional.of(copy));
        assertThatThrownBy(() -> loanService.issueLoan(copyId, 10L, LocalDate.now().plusDays(7)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void returnLoan_calculatesFineAndSetsAvailable() {
        Long loanId = 55L;
        BookCopy copy = new BookCopy();
        copy.setCopyId(10L);
        copy.setBarcode("BC-555");
        copy.setStatus(CopyStatus.ON_LOAN);
        Borrower borrower = new Borrower();
        borrower.setBorrowerId(2L);
        Loan loan = new Loan();
        loan.setLoanId(loanId);
        loan.setCopy(copy);
        loan.setBorrower(borrower);
        loan.setLoanDate(LocalDate.now().minusDays(20));
        loan.setDueDate(LocalDate.now().minusDays(5));
        loan.setStatus(LoanStatus.BORROWED);
        loan.setFineAmount(BigDecimal.ZERO);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(copyRepository.save(any(BookCopy.class))).thenAnswer(inv -> inv.getArgument(0));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
        Loan updated = loanService.returnLoan(loanId);
        assertThat(updated.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(updated.getReturnDate()).isNotNull();
        assertThat(updated.getFineAmount()).isEqualTo(12.5);
        assertThat(copy.getStatus()).isEqualTo(CopyStatus.AVAILABLE);
    }

    @Test
    void returnLoan_alreadyReturned_throws() {
        Long loanId = 99L;
        Loan loan = new Loan();
        loan.setLoanId(loanId);
        loan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        assertThatThrownBy(() -> loanService.returnLoan(loanId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already returned");
    }
}