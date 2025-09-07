package com.project.librarymanagementsystem.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.project.librarymanagementsystem.service.BookService;

@Controller
public class BookWebController {
    private final BookService bookService;

    public BookWebController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping({ "/books", "/" })
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.searchByTitle(null));
        return "books/list";
    }
}