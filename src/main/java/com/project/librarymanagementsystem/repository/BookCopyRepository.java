package com.project.librarymanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.librarymanagementsystem.entity.BookCopy;
import com.project.librarymanagementsystem.entity.CopyStatus;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    Optional<BookCopy> findByBarcode(String barcode);

    List<BookCopy> findByBookBookIdAndStatus(Long bookId, CopyStatus status);

    long countByBookBookIdAndStatus(Long bookId, CopyStatus status);
}