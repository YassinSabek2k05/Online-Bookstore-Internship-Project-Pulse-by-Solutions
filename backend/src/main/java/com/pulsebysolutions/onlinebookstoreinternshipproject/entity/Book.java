package com.pulsebysolutions.onlinebookstoreinternshipproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Book title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Book author is required")
    @Column(nullable = false)
    private String author;

    @NotBlank(message = "Book category is required")
    @Column(nullable = false)
    private String category;

    @NotNull(message = "Book price is required")
    @DecimalMin(
            value = "0.01",
            message = "Book price must be greater than zero"
    )
    @Column(nullable = false)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;
}