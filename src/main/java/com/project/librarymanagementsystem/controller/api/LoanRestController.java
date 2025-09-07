package com.project.librarymanagementsystem.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.librarymanagementsystem.dto.LoanDTO;
import com.project.librarymanagementsystem.entity.Loan;
import com.project.librarymanagementsystem.entity.LoanStatus;
import com.project.librarymanagementsystem.repository.LoanRepository;
import com.project.librarymanagementsystem.service.LoanService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanRestController {
    private final LoanService loanService;
    private final LoanRepository loanRepository;

    public LoanRestController(LoanService loanService, LoanRepository loanRepository) {
        this.loanService = loanService;
        this.loanRepository = loanRepository;
    }

    @PostMapping("/issue")
    public ResponseEntity<?> issueLoan(@Valid @RequestBody LoanDTO dto) {
        LocalDate due = LocalDate.parse(dto.getDueDate());
        Loan loan = loanService.issueLoan(dto.getCopyId(), dto.getBorrowerId(), due);
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnLoan(@PathVariable Long id) {
        Loan loan = loanService.returnLoan(id);
        return ResponseEntity.ok(loan);
    }

    @GetMapping
    public List<Loan> listLoans(@RequestParam(value = "status", required = false) LoanStatus status) {
        if (status == null)
            return loanRepository.findAll();
        return loanRepository.findByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoan(@PathVariable Long id) {
        return loanRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}