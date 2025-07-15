package com.example.Bookshop.service.impl;

import com.example.Bookshop.model.Book;
import com.example.Bookshop.model.Cart;
import com.example.Bookshop.model.CartItem;
import com.example.Bookshop.repository.BookRepository;
import com.example.Bookshop.repository.CartRepository;
import com.example.Bookshop.repository.UserRepository;
import com.example.Bookshop.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Override
    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    @Override
    public Cart getOrBuild(String userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = Cart.builder()
                    .id(UUID.randomUUID().toString())
                    .user(userRepository.findById(userId).get())
                    .build();
            return cartRepository.save(c);
        });
    }

    @Override
    public Cart addItem(String userId, String bookId, int quantity) {
        Cart cart = getOrBuild(userId);

        Book book = bookRepository
                .findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book " + bookId + " is not available"));

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            CartItem item = CartItem.builder()
                    .id(UUID.randomUUID().toString())
                    .cart(cart)
                    .book(book)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(item);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItem(String userId, String cartItemId) {
        Cart cart = getOrBuild(userId);
        cart.getItems().removeIf(i -> i.getId().equals(cartItemId));
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateQuantity(String userId, String cartItemId, int newQuantity) {
        Cart cart = getOrBuild(userId);
        cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .ifPresent(i -> {
                    if (newQuantity <= 0) cart.getItems().remove(i);
                    else i.setQuantity(newQuantity);
                });
        return cartRepository.save(cart);
    }

    @Override
    public void deleteCart(String userId) {
        Cart cart = getOrBuild(userId);
        cartRepository.delete(cart);
    }
}
