package com.project.librarymanagementsystem.service;

import java.util.Optional;
import com.project.librarymanagementsystem.entity.Borrower;

public interface BorrowerService {
    Borrower create(Borrower borrower);

    Optional<Borrower> findById(Long id);
}