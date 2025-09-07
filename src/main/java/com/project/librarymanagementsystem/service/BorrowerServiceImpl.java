package com.project.librarymanagementsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.librarymanagementsystem.entity.Borrower;
import com.project.librarymanagementsystem.repository.BorrowerRepository;
import java.util.Optional;

@Service
public class BorrowerServiceImpl implements BorrowerService {
    private final BorrowerRepository borrowerRepository;

    public BorrowerServiceImpl(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    @Override
    @Transactional
    public Borrower create(Borrower borrower) {
        return borrowerRepository.save(borrower);
    }

    @Override
    public Optional<Borrower> findById(Long id) {
        return borrowerRepository.findById(id);
    }
}