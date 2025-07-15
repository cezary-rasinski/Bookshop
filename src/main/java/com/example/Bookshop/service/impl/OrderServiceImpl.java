package com.example.Bookshop.service.impl;

import com.example.Bookshop.dto.OrderStatus;
import com.example.Bookshop.model.Cart;
import com.example.Bookshop.model.CartItem;
import com.example.Bookshop.model.Order;
import com.example.Bookshop.model.OrderItem;
import com.example.Bookshop.repository.CartRepository;
import com.example.Bookshop.repository.OrderRepository;
import com.example.Bookshop.service.CartService;
import com.example.Bookshop.service.OrderService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public String createCheckoutSession(String userId) throws StripeException {

//        Optional<Order> pending = orderRepository.findByUserIdAndStatus(userId, OrderStatus.PENDING);
//        if (pending.isPresent()){
//            Stripe.apiKey = apiKey;
//            Session existing = Session.retrieve(pending.get().getStripeSessionId());
//            return existing.getUrl();
//        }

        //Check if there is an existing cart
        Optional<Cart> opt_cart = cartRepository.findByUserId(userId);
        if (opt_cart.isEmpty()){
            throw new IllegalStateException("Cart is empty");
        }
        Cart cart = opt_cart.get();

        //Build an order based on user's cart
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .user(cart.getUser())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        double total = 0;
        for (CartItem cart_item : cart.getItems()) {
            OrderItem order_item = OrderItem.builder()
                    .id(UUID.randomUUID().toString())
                    .order(order)
                    .book(cart_item.getBook())
                    .quantity(cart_item.getQuantity())
                    .priceSnapshot(cart_item.getBook().getPrice())
                    .build();
            order.getItems().add(order_item);
            total += order_item.getQuantity() * order_item.getPriceSnapshot();
        }
        order.setPrice(total);

        orderRepository.save(order);

        Stripe.apiKey = apiKey;

        // Build line items
        var lineItems = order.getItems().stream()
                .map(i -> SessionCreateParams.LineItem.builder()
                        .setQuantity((long) i.getQuantity())
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("pln")
                                        .setUnitAmount((long)(i.getPriceSnapshot() * 100))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(i.getBook().getTitle())
                                                        .build())
                                        .build())
                        .build())
                .toList();

        // Create Stripe session
        var params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://your.app/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("https://your.app/cancel")
                .addAllLineItem(lineItems)
                .build();

        Session session = Session.create(params);

        // Persist the session ID & update status
        order.setStripeSessionId(session.getId());
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        return session.getUrl();
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        Stripe.apiKey = apiKey;
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid signature", e);
        }
        if ("checkout.session.completed".equals(event.getType())) {
            StripeObject stripeObject =
                    event.getDataObjectDeserializer().getObject().orElseThrow();
            String sessionId = ((Session) stripeObject).getId();
            if (sessionId != null) {
                orderRepository.findByStripeSessionId(sessionId).ifPresent(order -> {
                    order.setStatus(OrderStatus.PAID);
                    order.setPaidAt(LocalDateTime.now());
                    orderRepository.save(order);
                    cartService.deleteCart(order.getUser().getId());
                });
            }
        }
    }
}
