package com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BookRequest(

        @NotBlank(message = "Book title is required")
        String title,

        @NotBlank(message = "Book author is required")
        String author,

        @NotBlank(message = "Book category is required")
        String category,

        @NotNull(message = "Book price is required")
        @DecimalMin(
                value = "0.01",
                message = "Book price must be greater than zero"
        )
        BigDecimal price,

        String description,

        String imageUrl

) {
}