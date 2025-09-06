package com.project.librarymanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.project.librarymanagementsystem.entity.Loan;
import com.project.librarymanagementsystem.entity.LoanStatus;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByBorrowerBorrowerId(Long borrowerId);

    @Query("SELECT l FROM Loan l WHERE l.borrower.borrowerId = :borrowerId AND (l.status = 'BORROWED' OR l.status = 'OVERDUE')")
    List<Loan> findActiveLoansByBorrowerId(Long borrowerId);

    List<Loan> findByStatus(LoanStatus status);
}