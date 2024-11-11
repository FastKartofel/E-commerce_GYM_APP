// OrderService.java
package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.CartItemDto;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.InsufficientStockException;
import com.ecommerce.backend.exception.ProductNotFoundException;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
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

        try {
            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(LocalDateTime.now());

            double totalAmount = 0.0;
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItemDto cartItemDto : cartItems) {
                Long productId = cartItemDto.getProductId();
                Integer quantity = cartItemDto.getQuantity();

                logger.debug("Processing CartItem - Product ID: {}, Quantity: {}", productId, quantity);

                if (productId == null) {
                    logger.error("Product ID is null in CartItem.");
                    throw new ProductNotFoundException("Product ID is null.");
                }

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> {
                            logger.error("Product not found: {}", productId);
                            return new ProductNotFoundException("Product not found: " + productId);
                        });

                if (product.getStockQuantity() < quantity) {
                    logger.warn("Insufficient stock for product: {}. Requested: {}, Available: {}",
                            product.getName(), quantity, product.getStockQuantity());
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
                }

                // Decrement stock
                product.setStockQuantity(product.getStockQuantity() - quantity);
                productRepository.save(product); // This will increment the version
                logger.debug("Decremented stock for product: {}. New stock: {}", product.getName(), product.getStockQuantity());

                // Create OrderItem
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(product.getPrice());

                orderItems.add(orderItem);

                // Calculate total
                totalAmount += product.getPrice() * quantity;
            }

            order.setTotalAmount(totalAmount);
            order.setOrderItems(orderItems);

            Order savedOrder = orderRepository.save(order);
            logger.info("Order placed successfully. Order ID: {}", savedOrder.getId());

            return savedOrder;
        } catch (OptimisticLockingFailureException e) {
            logger.error("Optimistic locking failed while placing order for user: {}", user.getUsername(), e);
            throw new InsufficientStockException("Failed to place order due to stock changes. Please try again.");
        }
    }
}
