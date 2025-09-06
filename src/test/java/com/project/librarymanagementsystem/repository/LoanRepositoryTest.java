package com.project.librarymanagementsystem.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.project.librarymanagementsystem.entity.Book;
import com.project.librarymanagementsystem.entity.BookCopy;
import com.project.librarymanagementsystem.entity.Borrower;
import com.project.librarymanagementsystem.entity.Loan;
import com.project.librarymanagementsystem.entity.LoanStatus;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LoanRepositoryTest {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookCopyRepository copyRepository;
    @Autowired
    private BorrowerRepository borrowerRepository;

    @Test
    @DisplayName("find active loans for borrower")
    void findActiveLoansByBorrower() {
        Book b = new Book();
        b.setTitle("Loanable");
        bookRepository.save(b);
        BookCopy copy = new BookCopy();
        copy.setBook(b);
        copy.setBarcode("BC-300");
        copyRepository.save(copy);
        Borrower br = new Borrower();
        br.setFirstName("Alice");
        br.setLastName("Wonder");
        borrowerRepository.save(br);
        Loan l = new Loan();
        l.setCopy(copy);
        l.setBorrower(br);
        l.setLoanDate(LocalDate.now());
        l.setDueDate(LocalDate.now().plusDays(7));
        l.setStatus(LoanStatus.BORROWED);
        loanRepository.save(l);
        List<Loan> active = loanRepository.findActiveLoansByBorrowerId(br.getBorrowerId());
        assertThat(active).hasSize(1);
        assertThat(active.get(0).getCopy().getBarcode()).isEqualTo("BC-300");
    }

    @Test
    @DisplayName("find loans by status")
    void findByStatus() {
        Loan l = new Loan();
        l.setLoanDate(LocalDate.now());
        l.setDueDate(LocalDate.now().plusDays(7));
        l.setStatus(LoanStatus.OVERDUE);
        loanRepository.save(l);
        List<Loan> overdue = loanRepository.findByStatus(LoanStatus.OVERDUE);
        assertThat(overdue).isNotEmpty();
    }
}