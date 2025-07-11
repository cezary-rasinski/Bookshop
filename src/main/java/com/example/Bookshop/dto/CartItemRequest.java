package com.example.Bookshop.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Used both for adding new Item(Book) and updating Item(CartItem)
public class CartItemRequest {
    private String id;
    private int quantity;
}
