package com.project.librarymanagementsystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.librarymanagementsystem.entity.BookCopy;
import com.project.librarymanagementsystem.entity.Borrower;
import com.project.librarymanagementsystem.entity.CopyStatus;
import com.project.librarymanagementsystem.entity.Loan;
import com.project.librarymanagementsystem.entity.LoanStatus;
import com.project.librarymanagementsystem.exception.BusinessException;
import com.project.librarymanagementsystem.exception.NotFoundException;
import com.project.librarymanagementsystem.repository.BookCopyRepository;
import com.project.librarymanagementsystem.repository.BorrowerRepository;
import com.project.librarymanagementsystem.repository.LoanRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final BookCopyRepository copyRepository;
    private final BorrowerRepository borrowerRepository;
    private final double finePerDay;

    public LoanServiceImpl(LoanRepository loanRepository,
            BookCopyRepository copyRepository,
            BorrowerRepository borrowerRepository,
            @Value("${library.fine-per-day:1.0}") double finePerDay) {
        this.loanRepository = loanRepository;
        this.copyRepository = copyRepository;
        this.borrowerRepository = borrowerRepository;
        this.finePerDay = finePerDay;
    }

    @Override
    @Transactional
    public Loan issueLoan(Long copyId, Long borrowerId, LocalDate dueDate) {
        BookCopy copy = copyRepository.findById(copyId)
                .orElseThrow(() -> new NotFoundException("Book copy not found: " + copyId));
        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new BusinessException("Book copy is not available for loan (status=" + copy.getStatus() + ")");
        }
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new NotFoundException("Borrower not found: " + borrowerId));
        if (!Boolean.TRUE.equals(borrower.getActive())) {
            throw new BusinessException("Borrower is not active");
        }
        List<?> activeLoans = loanRepository.findActiveLoansByBorrowerId(borrowerId);
        if (activeLoans.size() >= borrower.getMaxAllowedLoans()) {
            throw new BusinessException("Borrower has reached maximum allowed loans");
        }
        copy.setStatus(CopyStatus.ON_LOAN);
        copyRepository.save(copy);
        Loan loan = new Loan();
        loan.setCopy(copy);
        loan.setBorrower(borrower);
        LocalDate loanDate = LocalDate.now();
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate != null ? dueDate : loanDate.plusDays(14));
        loan.setStatus(LoanStatus.BORROWED);
        loan.setFineAmount(BigDecimal.ZERO);
        return loanRepository.save(loan);
    }

    @Override
    @Transactional
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found: " + loanId));
        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new BusinessException("Loan already returned");
        }
        LocalDate returnDate = LocalDate.now();
        loan.setReturnDate(returnDate);
        BigDecimal fine = calculateFineAmount(loan.getDueDate(), returnDate);
        loan.setFineAmount(fine);
        loan.setStatus(LoanStatus.RETURNED);
        BookCopy copy = loan.getCopy();
        if (copy == null) {
            throw new NotFoundException("Associated book copy not found for loan: " + loanId);
        }
        copy.setStatus(CopyStatus.AVAILABLE);
        copyRepository.save(copy);

        return loanRepository.save(loan);
    }

    @Override
    public BigDecimal calculateFineAmount(LocalDate dueDate, LocalDate returnedDate) {
        if (dueDate == null || returnedDate == null)
            return BigDecimal.ZERO;
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnedDate);
        if (daysLate <= 0)
            return BigDecimal.ZERO;
        return new BigDecimal(daysLate * finePerDay);
    }
}