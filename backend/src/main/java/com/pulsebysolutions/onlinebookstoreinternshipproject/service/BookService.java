package com.pulsebysolutions.onlinebookstoreinternshipproject.service;

import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.request.BookRequest;
import com.pulsebysolutions.onlinebookstoreinternshipproject.dto.response.BookResponse;
import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.Book;
import com.pulsebysolutions.onlinebookstoreinternshipproject.exception.ResourceNotFoundException;
import com.pulsebysolutions.onlinebookstoreinternshipproject.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::fromBook)
                .toList();
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found with id: " + id));

        return BookResponse.fromBook(book);
    }

    public BookResponse createBook(BookRequest request) {

        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .category(request.category())
                .price(request.price())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .build();

        Book savedBook = bookRepository.save(book);

        return BookResponse.fromBook(savedBook);
    }

    public BookResponse updateBook(Long id, BookRequest request) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found with id: " + id));

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setCategory(request.category());
        book.setPrice(request.price());
        book.setDescription(request.description());
        book.setImageUrl(request.imageUrl());

        Book updatedBook = bookRepository.save(book);

        return BookResponse.fromBook(updatedBook);
    }

    public void deleteBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found with id: " + id));

        bookRepository.delete(book);
    }
}