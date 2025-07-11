package com.example.Bookshop.service.impl;

import com.example.Bookshop.model.Book;
import com.example.Bookshop.repository.BookRepository;
import com.example.Bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {

        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAvailableBooks() {

        return bookRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(String bookId) {

        return bookRepository.findById(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAvailable(String bookId) {

        return bookRepository.findByIdAndIsActiveTrue(bookId).isPresent();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null || book.getId().isBlank()) {
            book.setId(UUID.randomUUID().toString());
            book.setActive(true);
        }
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        bookRepository.findById(id).ifPresent(b -> {
            b.setActive(false);
            bookRepository.save(b);
        });
    }
}
