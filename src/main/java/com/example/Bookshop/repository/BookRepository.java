package com.example.Bookshop.repository;

import com.example.Bookshop.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByIsActiveTrue();

    Optional<Book> findByIdAndIsActiveTrue(String id);
}
