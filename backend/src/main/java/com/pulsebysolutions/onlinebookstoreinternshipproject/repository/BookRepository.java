package com.pulsebysolutions.onlinebookstoreinternshipproject.repository;

import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}