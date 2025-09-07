package com.project.librarymanagementsystem.controller.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.project.librarymanagementsystem.dto.BookDTO;
import com.project.librarymanagementsystem.entity.Author;
import com.project.librarymanagementsystem.entity.Book;
import com.project.librarymanagementsystem.entity.Category;
import com.project.librarymanagementsystem.entity.Publisher;
import com.project.librarymanagementsystem.mapper.BookMapper;
import com.project.librarymanagementsystem.repository.AuthorRepository;
import com.project.librarymanagementsystem.repository.BookRepository;
import com.project.librarymanagementsystem.repository.CategoryRepository;
import com.project.librarymanagementsystem.repository.PublisherRepository;
import com.project.librarymanagementsystem.service.BookService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookWebController {
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;

    public BookWebController(BookService bookService,
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            PublisherRepository publisherRepository,
            CategoryRepository categoryRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model) {
        List<Book> books = (q == null || q.isBlank()) ? bookRepository.findAll() : bookService.searchByTitle(q);
        model.addAttribute("books", books);
        model.addAttribute("q", q);
        return "books/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        if (!model.containsAttribute("bookDto"))
            model.addAttribute("bookDto", new BookDTO());
        model.addAttribute("publishers", publisherRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("allAuthors", authorRepository.findAll());
        return "books/create";
    }

    @PostMapping("/create")
    public String createSubmit(@Valid @ModelAttribute("bookDto") BookDTO bookDto,
            BindingResult br,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (br.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookDto", br);
            redirectAttributes.addFlashAttribute("bookDto", bookDto);
            return "redirect:/books/create";
        }
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setIsbn(bookDto.getIsbn());
        book.setDescription(bookDto.getDescription());
        if (bookDto.getPublisherId() != null) {
            Publisher p = new Publisher();
            p.setPublisherId(bookDto.getPublisherId());
            book.setPublisher(p);
        }
        if (bookDto.getCategoryId() != null) {
            Category c = new Category();
            c.setCategoryId(bookDto.getCategoryId());
            book.setCategory(c);
        }
        if (bookDto.getAuthorIds() != null) {
            Set<Author> authors = bookDto.getAuthorIds().stream().map(id -> {
                Author a = new Author();
                a.setAuthorId(id);
                return a;
            }).collect(Collectors.toSet());
            book.setAuthors(authors);
        }
        Book saved = bookService.create(book);
        redirectAttributes.addFlashAttribute("flashMessage", "Book created successfully");
        return "redirect:/books/" + saved.getBookId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            model.addAttribute("flashMessage", "Book not found");
            return "redirect:/books";
        }
        model.addAttribute("book", book);
        return "books/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            model.addAttribute("flashMessage", "Book not found");
            return "redirect:/books";
        }
        BookDTO dto = BookMapper.toDTO(book);
        model.addAttribute("bookDto", dto);
        model.addAttribute("publishers", publisherRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("allAuthors", authorRepository.findAll());
        return "books/edit";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
            @Valid @ModelAttribute("bookDto") BookDTO bookDto,
            BindingResult br,
            RedirectAttributes redirectAttributes) {
        if (br.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookDto", br);
            redirectAttributes.addFlashAttribute("bookDto", bookDto);
            return "redirect:/books/" + id + "/edit";
        }
        Book toUpdate = new Book();
        toUpdate.setBookId(id);
        toUpdate.setTitle(bookDto.getTitle());
        toUpdate.setIsbn(bookDto.getIsbn());
        toUpdate.setDescription(bookDto.getDescription());
        if (bookDto.getPublisherId() != null) {
            Publisher p = new Publisher();
            p.setPublisherId(bookDto.getPublisherId());
            toUpdate.setPublisher(p);
        }
        if (bookDto.getCategoryId() != null) {
            Category c = new Category();
            c.setCategoryId(bookDto.getCategoryId());
            toUpdate.setCategory(c);
        }
        if (bookDto.getAuthorIds() != null) {
            Set<Author> authors = bookDto.getAuthorIds().stream().map(aid -> {
                Author a = new Author();
                a.setAuthorId(aid);
                return a;
            }).collect(Collectors.toSet());
            toUpdate.setAuthors(authors);
        }
        Book saved = bookService.update(toUpdate);
        redirectAttributes.addFlashAttribute("flashMessage", "Book updated");
        return "redirect:/books/" + saved.getBookId();
    }
}