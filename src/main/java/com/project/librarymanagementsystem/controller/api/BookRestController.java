package com.project.librarymanagementsystem.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.librarymanagementsystem.dto.BookDTO;
import com.project.librarymanagementsystem.entity.Author;
import com.project.librarymanagementsystem.entity.Book;
import com.project.librarymanagementsystem.entity.Category;
import com.project.librarymanagementsystem.entity.Publisher;
import com.project.librarymanagementsystem.mapper.BookMapper;
import com.project.librarymanagementsystem.repository.BookRepository;
import com.project.librarymanagementsystem.service.BookService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookRestController {
    private final BookService bookService;
    private final BookRepository bookRepository;

    public BookRestController(BookService bookService, BookRepository bookRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<BookDTO> listBooks(@RequestParam(value = "q", required = false) String q) {
        List<Book> books = (q == null || q.isBlank()) ? bookRepository.findAll() : bookService.searchByTitle(q);
        return books.stream().map(BookMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        return bookService.findById(id)
                .map(b -> ResponseEntity.ok(BookMapper.toDTO(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        if (dto.getPublisherId() != null) {
            Publisher p = new Publisher();
            p.setPublisherId(dto.getPublisherId());
            book.setPublisher(p);
        }
        if (dto.getCategoryId() != null) {
            Category c = new Category();
            c.setCategoryId(dto.getCategoryId());
            book.setCategory(c);
        }
        if (dto.getAuthorIds() != null && !dto.getAuthorIds().isEmpty()) {
            Set<Author> authors = new HashSet<>();
            dto.getAuthorIds().forEach(id -> {
                Author a = new Author();
                a.setAuthorId(id);
                authors.add(a);
            });
            book.setAuthors(authors);
        }
        Book saved = bookService.create(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        Book toUpdate = new Book();
        toUpdate.setBookId(id);
        toUpdate.setTitle(dto.getTitle());
        toUpdate.setIsbn(dto.getIsbn());
        toUpdate.setDescription(dto.getDescription());
        if (dto.getPublisherId() != null) {
            Publisher p = new Publisher();
            p.setPublisherId(dto.getPublisherId());
            toUpdate.setPublisher(p);
        }
        if (dto.getCategoryId() != null) {
            Category c = new Category();
            c.setCategoryId(dto.getCategoryId());
            toUpdate.setCategory(c);
        }
        if (dto.getAuthorIds() != null) {
            Set<Author> authors = dto.getAuthorIds().stream().map(aid -> {
                Author a = new Author();
                a.setAuthorId(aid);
                return a;
            }).collect(Collectors.toSet());
            toUpdate.setAuthors(authors);
        }
        Book saved = bookService.update(toUpdate);
        return ResponseEntity.ok(BookMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}