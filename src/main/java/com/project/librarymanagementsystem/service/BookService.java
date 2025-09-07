package com.project.librarymanagementsystem.service;

import java.util.List;
import java.util.Optional;
import com.project.librarymanagementsystem.entity.Book;

public interface BookService {
    Book create(Book book);

    Book update(Book book);

    Optional<Book> findById(Long id);

    List<Book> searchByTitle(String q);
}