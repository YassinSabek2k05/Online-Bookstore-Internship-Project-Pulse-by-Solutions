package com.pulsebysolutions.onlinebookstoreinternshipproject.dto.response;

import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.Book;

import java.math.BigDecimal;

public record BookResponse(
        Long id,
        String title,
        String author,
        String category,
        BigDecimal price,
        String description,
        String imageUrl
) {

    public static BookResponse fromBook(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getPrice(),
                book.getDescription(),
                book.getImageUrl()
        );
    }
}