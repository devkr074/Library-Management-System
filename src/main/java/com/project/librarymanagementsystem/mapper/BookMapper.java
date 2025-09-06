package com.project.librarymanagementsystem.mapper;

import java.util.stream.Collectors;
import com.project.librarymanagementsystem.dto.BookDTO;
import com.project.librarymanagementsystem.entity.Book;

public final class BookMapper {
    private BookMapper() {
    }

    public static BookDTO toDTO(Book book) {
        if (book == null)
            return null;
        BookDTO dto = new BookDTO();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        if (book.getPublisher() != null)
            dto.setPublisherId(book.getPublisher().getPublisherId());
        if (book.getCategory() != null)
            dto.setCategoryId(book.getCategory().getCategoryId());
        if (book.getAuthors() != null) {
            dto.setAuthorIds(book.getAuthors().stream().map(a -> a.getAuthorId()).collect(Collectors.toSet()));
        }
        return dto;
    }
}