package com.example.Bookshop.service;

import com.example.Bookshop.dto.OrderStatus;
import com.example.Bookshop.model.Order;
import com.stripe.exception.StripeException;

import java.util.List;

public interface OrderService {
    String createCheckoutSession(String rentalId) throws StripeException;
    void handleWebhook(String payload, String signature);

    List<Order> findAll();
    Order updateStatus(String orderId, OrderStatus newStatus);
}
