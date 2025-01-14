package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.CartItemDto;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.enums.OrderStatus;
import com.ecommerce.backend.exception.InsufficientStockException;
import com.ecommerce.backend.exception.ProductNotFoundException;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order placeOrder(User user, List<CartItemDto> cartItems) throws InsufficientStockException, ProductNotFoundException {
        logger.info("Placing order for user: {}", user.getUsername());

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING); // Set initial status

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemDto cartItemDto : cartItems) {
            Long productId = cartItemDto.getProductId();
            Integer quantity = cartItemDto.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

            if (product.getStockQuantity() < quantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());

            orderItems.add(orderItem);
            totalAmount += product.getPrice() * quantity;
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        return orderRepository.save(order);
    }

    // Simulate order processing
    public void processOrders() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        pendingOrders.forEach(order -> {
            if (order.getOrderDate().plusDays(3).isBefore(LocalDateTime.now())) {
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
            }
        });
    }

    // Fetch current (PENDING) orders for the user
    public List<Order> getCurrentOrders(User user) {
        logger.info("Fetching current orders for user: {}", user.getUsername());
        return orderRepository.findByUserAndStatus(user, OrderStatus.PENDING);
    }

    // Fetch completed orders (history) for the user
    public List<Order> getOrderHistory(User user) {
        logger.info("Fetching order history for user: {}", user.getUsername());
        return orderRepository.findByUserAndStatus(user, OrderStatus.COMPLETED);
    }

    // Run the processOrders method daily
    @Scheduled(cron = "0 0 0 * * ?") // Executes at midnight every day
    public void scheduleOrderProcessing() {
        processOrders();
    }
}
