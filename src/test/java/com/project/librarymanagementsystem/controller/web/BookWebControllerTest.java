package com.project.librarymanagementsystem.controller.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import com.project.librarymanagementsystem.repository.AuthorRepository;
import com.project.librarymanagementsystem.repository.BookRepository;
import com.project.librarymanagementsystem.repository.CategoryRepository;
import com.project.librarymanagementsystem.repository.PublisherRepository;
import com.project.librarymanagementsystem.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collections;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookWebController.class)
class BookWebControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private BookService bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private PublisherRepository publisherRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void listBooksPageLoads() throws Exception {
        Mockito.when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attributeExists("books"));
    }
}