package com.example.Bookshop.controller;

import com.example.Bookshop.dto.OrderStatus;
import com.example.Bookshop.model.Book;
import com.example.Bookshop.model.Order;
import com.example.Bookshop.service.OrderService;
import com.example.Bookshop.service.UserService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    private String currentUserId(UserDetails user) {
        return userService.findByLogin(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @PostMapping()
    public ResponseEntity<String> createCheckoutSession(@AuthenticationPrincipal UserDetails user) throws StripeException {
        String userId = currentUserId(user);
        String url = orderService.createCheckoutSession(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        orderService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{orderId}/status/{newStatus}")
    public ResponseEntity<Order> changeStatus(
            @PathVariable String orderId,
            @PathVariable OrderStatus newStatus
    ) {
        Order updated = orderService.updateStatus(orderId, newStatus);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("seeOrders")
    public List<Order> seeAllOrders() {
        return orderService.findAll();
    }

    @GetMapping("/success")
    public ResponseEntity<String> success(){
        return ResponseEntity.ok("success");
    }
    @GetMapping("/cancel")
    public ResponseEntity<String> cancel(){
        return ResponseEntity.ok("cancel");
    }

}
