package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.CartItemDto;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.enums.OrderStatus;
import com.ecommerce.backend.exception.InsufficientStockException;
import com.ecommerce.backend.exception.ProductNotFoundException;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock user
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
    }

    @Test
    void testPlaceOrder_ValidOrder() throws InsufficientStockException, ProductNotFoundException {
        // Mock cart items and product
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);
        product.setPrice(20.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<CartItemDto> cartItems = new ArrayList<>();
        CartItemDto item = new CartItemDto();
        item.setProductId(1L);
        item.setQuantity(2);
        cartItems.add(item);

        // Execute service method
        Order order = orderService.placeOrder(user, cartItems);

        // Assertions
        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(40.0, order.getTotalAmount());

        // Verify interactions
        verify(productRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }
}
