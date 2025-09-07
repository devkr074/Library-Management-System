package com.project.librarymanagementsystem.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.librarymanagementsystem.dto.AuthorDTO;
import com.project.librarymanagementsystem.entity.Author;
import com.project.librarymanagementsystem.exception.NotFoundException;
import com.project.librarymanagementsystem.mapper.AuthorMapper;
import com.project.librarymanagementsystem.repository.AuthorRepository;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authors")
public class AuthorRestController {
    private final AuthorRepository authorRepository;

    public AuthorRestController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public List<AuthorDTO> listAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(AuthorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AuthorDTO getAuthor(@PathVariable Long id) {
        Author a = authorRepository.findById(id).orElseThrow(() -> new NotFoundException("Author not found: " + id));
        return AuthorMapper.toDTO(a);
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO dto) {
        Author entity = AuthorMapper.toEntity(dto);
        Author saved = authorRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthorMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public AuthorDTO updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDTO dto) {
        Author existing = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found: " + id));
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setBio(dto.getBio());
        if (dto.getDateOfBirth() != null) {
            existing.setDateOfBirth(java.time.LocalDate.parse(dto.getDateOfBirth()));
        }
        Author saved = authorRepository.save(existing);
        return AuthorMapper.toDTO(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (!authorRepository.existsById(id)) {
            throw new NotFoundException("Author not found: " + id);
        }
        authorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}