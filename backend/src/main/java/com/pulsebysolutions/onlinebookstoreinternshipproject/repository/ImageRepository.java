package com.pulsebysolutions.onlinebookstoreinternshipproject.repository;

import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<BookImage, String> {
}
