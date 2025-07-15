package com.example.Bookshop.repository;

import com.example.Bookshop.dto.OrderStatus;
import com.example.Bookshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByStripeSessionId(String stripeSessionId);
    Optional<Order> findByUserIdAndStatus(String userId, OrderStatus status);
}
