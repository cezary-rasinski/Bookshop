package com.example.Bookshop.model;

import com.example.Bookshop.dto.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(
            mappedBy="order",
            cascade=CascadeType.ALL,
            orphanRemoval=true
    )
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(columnDefinition = "NUMERIC")
    private double price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "paid_at", nullable = true)
    private LocalDateTime paidAt;

    @Column(name = "stripe_session_id", unique = true)
    private String stripeSessionId;
}
