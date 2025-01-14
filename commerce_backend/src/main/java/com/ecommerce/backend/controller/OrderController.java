// OrderController.java
package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.CartItemDto;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.InsufficientStockException;
import com.ecommerce.backend.exception.ProductNotFoundException;
import com.ecommerce.backend.exception.UsernameNotFoundException;
import com.ecommerce.backend.service.OrderService;
import com.ecommerce.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }


    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody List<CartItemDto> cartItems, Principal principal) throws ProductNotFoundException, InsufficientStockException, UsernameNotFoundException {
        // Validate cart items
        for (CartItemDto item : cartItems) {
            if (item.getProductId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Invalid cart item: " + item);
            }
        }

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));

        Order order = orderService.placeOrder(user, cartItems);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/current")
    public ResponseEntity<List<Order>> getCurrentOrders(Principal principal) throws UsernameNotFoundException {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));

        List<Order> currentOrders = orderService.getCurrentOrders(user);
        return ResponseEntity.ok(currentOrders);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Order>> getOrderHistory(Principal principal) throws UsernameNotFoundException {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));

        List<Order> orderHistory = orderService.getOrderHistory(user);
        return ResponseEntity.ok(orderHistory);
    }



}
