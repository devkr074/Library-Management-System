package com.project.librarymanagementsystem.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.project.librarymanagementsystem.entity.Book;
import com.project.librarymanagementsystem.entity.BookCopy;
import com.project.librarymanagementsystem.entity.CopyStatus;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

@DataJpaTest
class BookCopyRepositoryTest {
    @Autowired
    private BookCopyRepository copyRepository;
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("find available copies for a book")
    void findAvailableCopies() {
        Book b = new Book();
        b.setTitle("Some Book");
        bookRepository.save(b);
        BookCopy c1 = new BookCopy();
        c1.setBook(b);
        c1.setBarcode("BC-100");
        c1.setStatus(CopyStatus.AVAILABLE);
        copyRepository.save(c1);
        BookCopy c2 = new BookCopy();
        c2.setBook(b);
        c2.setBarcode("BC-101");
        c2.setStatus(CopyStatus.ON_LOAN);
        copyRepository.save(c2);
        List<BookCopy> available = copyRepository.findByBookBookIdAndStatus(b.getBookId(), CopyStatus.AVAILABLE);
        assertThat(available).hasSize(1);
        assertThat(available.get(0).getBarcode()).isEqualTo("BC-100");
    }

    @Test
    @DisplayName("count available copies")
    void countAvailableCopies() {
        Book b = new Book();
        b.setTitle("Counting Book");
        bookRepository.save(b);
        BookCopy c1 = new BookCopy();
        c1.setBook(b);
        c1.setBarcode("BC-200");
        c1.setStatus(CopyStatus.AVAILABLE);
        copyRepository.save(c1);
        long cnt = copyRepository.countByBookBookIdAndStatus(b.getBookId(), CopyStatus.AVAILABLE);
        assertThat(cnt).isEqualTo(1L);
    }
}