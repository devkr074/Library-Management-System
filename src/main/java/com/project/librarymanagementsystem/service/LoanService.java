package com.project.librarymanagementsystem.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.project.librarymanagementsystem.entity.Loan;

public interface LoanService {
    Loan issueLoan(Long copyId, Long borrowerId, LocalDate dueDate);

    Loan returnLoan(Long loanId);

    BigDecimal calculateFineAmount(LocalDate dueDate, LocalDate returnedDate);
}