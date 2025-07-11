package com.example.Bookshop.service;

import com.example.Bookshop.model.Cart;
import com.example.Bookshop.model.User;

import java.util.List;
import java.util.Optional;

public interface CartService {
    List<Cart> findAll();

    Cart getOrBuild(String userId);

    Cart addItem(String userId, String bookId, int quantity);
    Cart removeItem(String userId, String cartItemId);

    Cart updateQuantity(String userId, String carItemId, int newQuantity);

    void deleteCart(String cartId);
}
