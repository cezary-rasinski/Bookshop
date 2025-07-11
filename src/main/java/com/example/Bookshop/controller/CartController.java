package com.example.Bookshop.controller;


import com.example.Bookshop.dto.CartItemRequest;
import com.example.Bookshop.service.CartService;
import com.example.Bookshop.model.Cart;
import com.example.Bookshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    //Admin can view all carts
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/seeAll")
    public List<Cart> seeAllCarts(){
        return cartService.findAll();
    }

    //User sees/creates his cart
    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails user) {
        Cart cart = cartService.getOrBuild(user.getUsername());
        return ResponseEntity.ok(cart);
    }

    //Adding an item
    @PostMapping()
    public ResponseEntity<Cart> addItem(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody CartItemRequest dto
    ) {
        Cart cart = cartService.addItem(
                user.getUsername(),
                dto.getId(),
                dto.getQuantity()
        );
        return ResponseEntity.ok(cart);
    }

    //Changing quantity of an item in cart
    @PutMapping()
    public ResponseEntity<Cart> updateItem(@RequestBody CartItemRequest dto, @AuthenticationPrincipal UserDetails user) {
        Cart cart = cartService.updateQuantity(
                user.getUsername(), dto.getId(), dto.getQuantity());
        return ResponseEntity.ok(cart);
    }

    //Deleting an item from cart
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> removeItem(@PathVariable String itemId, @AuthenticationPrincipal UserDetails user) {
        Cart cart = cartService.removeItem(
                user.getUsername(), itemId);
        return ResponseEntity.ok(cart);
    }

    //Deleting the entire cart
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails user) {
        cartService.deleteCart(user.getUsername());
        return ResponseEntity.noContent().build();
    }
}