package com.example.Bookshop.service;

import com.stripe.exception.StripeException;

public interface OrderService {
    String createCheckoutSession(String rentalId) throws StripeException;
    void handleWebhook(String payload, String signature);
}
