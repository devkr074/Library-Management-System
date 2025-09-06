package com.project.librarymanagementsystem.mapper;

import java.time.LocalDate;
import com.project.librarymanagementsystem.dto.AuthorDTO;
import com.project.librarymanagementsystem.entity.Author;

public final class AuthorMapper {
    private AuthorMapper() {
    }

    public static Author toEntity(AuthorDTO dto) {
        if (dto == null)
            return null;
        Author a = new Author();
        a.setAuthorId(dto.getAuthorId());
        a.setFirstName(dto.getFirstName());
        a.setLastName(dto.getLastName());
        a.setBio(dto.getBio());
        if (dto.getDateOfBirth() != null) {
            a.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        }
        return a;
    }

    public static AuthorDTO toDTO(Author a) {
        if (a == null)
            return null;
        AuthorDTO dto = new AuthorDTO();
        dto.setAuthorId(a.getAuthorId());
        dto.setFirstName(a.getFirstName());
        dto.setLastName(a.getLastName());
        dto.setBio(a.getBio());
        if (a.getDateOfBirth() != null) {
            dto.setDateOfBirth(a.getDateOfBirth().toString());
        }
        return dto;
    }
}