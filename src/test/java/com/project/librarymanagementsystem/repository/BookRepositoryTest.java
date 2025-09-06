package com.project.librarymanagementsystem.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.project.librarymanagementsystem.entity.Book;
import com.project.librarymanagementsystem.entity.Category;
import com.project.librarymanagementsystem.entity.Publisher;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("findByIsbn returns book when present")
    void findByIsbn() {
        Publisher p = new Publisher();
        p.setName("TestPub");
        publisherRepository.save(p);
        Category c = new Category();
        c.setName("TestCategory");
        categoryRepository.save(c);
        Book book = new Book();
        book.setTitle("Test Driven Development");
        book.setIsbn("ISBN-12345");
        book.setPublisher(p);
        book.setCategory(c);
        bookRepository.save(book);
        var found = bookRepository.findByIsbn("ISBN-12345");
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Driven Development");
    }

    @Test
    @DisplayName("search by title containing")
    void findByTitleContainingIgnoreCase() {
        Book b1 = new Book();
        b1.setTitle("Spring in Action");
        bookRepository.save(b1);
        Book b2 = new Book();
        b2.setTitle("Pro Spring Boot");
        bookRepository.save(b2);
        var results = bookRepository.findByTitleContainingIgnoreCase("spring");
        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
    }
}