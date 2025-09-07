package com.project.librarymanagementsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.librarymanagementsystem.entity.Author;
import com.project.librarymanagementsystem.entity.Book;
import com.project.librarymanagementsystem.entity.Category;
import com.project.librarymanagementsystem.entity.Publisher;
import com.project.librarymanagementsystem.exception.NotFoundException;
import com.project.librarymanagementsystem.repository.AuthorRepository;
import com.project.librarymanagementsystem.repository.BookRepository;
import com.project.librarymanagementsystem.repository.CategoryRepository;
import com.project.librarymanagementsystem.repository.PublisherRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository,
            PublisherRepository publisherRepository,
            CategoryRepository categoryRepository,
            AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    @Transactional
    public Book create(Book book) {
        if (book.getPublisher() != null && book.getPublisher().getPublisherId() != null) {
            Publisher p = publisherRepository.findById(book.getPublisher().getPublisherId())
                    .orElseThrow(() -> new NotFoundException("Publisher not found"));
            book.setPublisher(p);
        }
        if (book.getCategory() != null && book.getCategory().getCategoryId() != null) {
            Category c = categoryRepository.findById(book.getCategory().getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            book.setCategory(c);
        }
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            Set<Author> resolved = new HashSet<>();
            for (Author a : book.getAuthors()) {
                if (a.getAuthorId() == null)
                    continue;
                Author found = authorRepository.findById(a.getAuthorId())
                        .orElseThrow(() -> new NotFoundException("Author not found: " + a.getAuthorId()));
                resolved.add(found);
            }
            book.setAuthors(resolved);
        }
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book update(Book book) {
        if (book.getBookId() == null) {
            throw new IllegalArgumentException("bookId required for update");
        }
        Book existing = bookRepository.findById(book.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));
        existing.setTitle(book.getTitle());
        existing.setIsbn(book.getIsbn());
        existing.setDescription(book.getDescription());
        existing.setPages(book.getPages());
        return bookRepository.save(existing);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> searchByTitle(String q) {
        return bookRepository.findByTitleContainingIgnoreCase(q == null ? "" : q);
    }
}