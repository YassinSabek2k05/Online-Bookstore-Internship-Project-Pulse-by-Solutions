package com.pulsebysolutions.onlinebookstoreinternshipproject.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 *we used this approach instead of another approaches like storing the image in the file system or using a cloud storage service(R2 container for example) because it allows us to keep all the data in one place
 */
@Entity
@Table(name = "book_images")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookImage {
    @Id
    private String key;           // UUID + extension, e.g. "3f9a2b1c-...-e21.jpg" — acts as the "object key"

    @Column(columnDefinition = "BYTEA")
    private byte[] data;

    private String contentType;
}