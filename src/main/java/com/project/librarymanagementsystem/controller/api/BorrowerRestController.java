package com.project.librarymanagementsystem.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.librarymanagementsystem.dto.BorrowerDTO;
import com.project.librarymanagementsystem.entity.Borrower;
import com.project.librarymanagementsystem.mapper.BorrowerMapper;
import com.project.librarymanagementsystem.repository.BorrowerRepository;
import com.project.librarymanagementsystem.service.BorrowerService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowerRestController {
    private final BorrowerRepository borrowerRepository;
    private final BorrowerService borrowerService;

    public BorrowerRestController(BorrowerRepository borrowerRepository, BorrowerService borrowerService) {
        this.borrowerRepository = borrowerRepository;
        this.borrowerService = borrowerService;
    }

    @GetMapping
    public List<BorrowerDTO> listBorrowers() {
        return borrowerRepository.findAll().stream().map(BorrowerMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowerDTO> getBorrower(@PathVariable Long id) {
        return borrowerRepository.findById(id).map(BorrowerMapper::toDTO).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BorrowerDTO> createBorrower(@Valid @RequestBody BorrowerDTO dto) {
        Borrower b = BorrowerMapper.toEntity(dto);
        Borrower saved = borrowerService.create(b);
        return ResponseEntity.status(HttpStatus.CREATED).body(BorrowerMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BorrowerDTO> updateBorrower(@PathVariable Long id, @Valid @RequestBody BorrowerDTO dto) {
        Borrower existing = borrowerRepository.findById(id).orElse(null);
        if (existing == null)
            return ResponseEntity.notFound().build();
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setAddress(dto.getAddress());
        Borrower saved = borrowerRepository.save(existing);
        return ResponseEntity.ok(BorrowerMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrower(@PathVariable Long id) {
        if (!borrowerRepository.existsById(id))
            return ResponseEntity.notFound().build();
        borrowerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}