package com.project.librarymanagementsystem.controller.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.project.librarymanagementsystem.dto.AuthorDTO;
import com.project.librarymanagementsystem.entity.Author;
import com.project.librarymanagementsystem.mapper.AuthorMapper;
import com.project.librarymanagementsystem.repository.AuthorRepository;
import java.util.List;

@Controller
@RequestMapping("/authors")
public class AuthorWebController {
    private final AuthorRepository authorRepository;

    public AuthorWebController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public String list(Model model) {
        List<Author> authors = authorRepository.findAll();
        model.addAttribute("authors", authors);
        return "authors/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        if (!model.containsAttribute("authorDto"))
            model.addAttribute("authorDto", new AuthorDTO());
        return "authors/create";
    }

    @PostMapping("/create")
    public String createSubmit(@Valid @ModelAttribute("authorDto") AuthorDTO dto,
            BindingResult br,
            RedirectAttributes redirectAttributes) {
        if (br.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.authorDto", br);
            redirectAttributes.addFlashAttribute("authorDto", dto);
            return "redirect:/authors/create";
        }
        Author author = AuthorMapper.toEntity(dto);
        authorRepository.save(author);
        redirectAttributes.addFlashAttribute("flashMessage", "Author created");
        return "redirect:/authors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Author a = authorRepository.findById(id).orElse(null);
        if (a == null) {
            model.addAttribute("flashMessage", "Author not found");
            return "redirect:/authors";
        }
        AuthorDTO dto = AuthorMapper.toDTO(a);
        model.addAttribute("authorDto", dto);
        return "authors/create";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
            @Valid @ModelAttribute("authorDto") AuthorDTO dto,
            BindingResult br,
            RedirectAttributes redirectAttributes) {
        if (br.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.authorDto", br);
            redirectAttributes.addFlashAttribute("authorDto", dto);
            return "redirect:/authors/" + id + "/edit";
        }
        Author existing = authorRepository.findById(id).orElse(null);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("flashMessage", "Author not found");
            return "redirect:/authors";
        }
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setBio(dto.getBio());
        if (dto.getDateOfBirth() != null)
            existing.setDateOfBirth(java.time.LocalDate.parse(dto.getDateOfBirth()));
        authorRepository.save(existing);
        redirectAttributes.addFlashAttribute("flashMessage", "Author updated");
        return "redirect:/authors";
    }
}