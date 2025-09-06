package com.project.librarymanagementsystem.mapper;

import com.project.librarymanagementsystem.dto.BorrowerDTO;
import com.project.librarymanagementsystem.entity.Borrower;

public final class BorrowerMapper {
    private BorrowerMapper() {
    }

    public static BorrowerDTO toDTO(Borrower b) {
        if (b == null)
            return null;
        BorrowerDTO dto = new BorrowerDTO();
        dto.setBorrowerId(b.getBorrowerId());
        dto.setFirstName(b.getFirstName());
        dto.setLastName(b.getLastName());
        dto.setEmail(b.getEmail());
        dto.setPhone(b.getPhone());
        dto.setAddress(b.getAddress());
        return dto;
    }

    public static Borrower toEntity(BorrowerDTO dto) {
        if (dto == null)
            return null;
        Borrower b = new Borrower();
        b.setBorrowerId(dto.getBorrowerId());
        b.setFirstName(dto.getFirstName());
        b.setLastName(dto.getLastName());
        b.setEmail(dto.getEmail());
        b.setPhone(dto.getPhone());
        b.setAddress(dto.getAddress());
        return b;
    }
}