package com.example.Bookshop.repository;

import com.example.Bookshop.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);

    @Query("SELECT c FROM Cart c WHERE c.user.login = :login")
    Optional<Cart> findByUsername(@Param("login") String login);
}