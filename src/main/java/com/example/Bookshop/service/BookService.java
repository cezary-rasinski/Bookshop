package com.example.Bookshop.service;

import com.example.Bookshop.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> findAll();
    List<Book> findAvailableBooks();
    Optional<Book> findById(String id);



    //List<Book> findBoughtBooks();
    //List<Book> findOrderedBooks();

    boolean isAvailable(String bookId);

    Book save(Book book);
    void deleteById(String id);
}
