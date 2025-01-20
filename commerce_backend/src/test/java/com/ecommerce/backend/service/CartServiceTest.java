package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.*;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock User and Product
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(10.0);
    }

    @Test
    void testAddToCart_NewCartItem() {
        // Mock dependencies
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute service method
        cartService.addToCart(1L, 2, "testuser");

        // Verify interactions and state
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getValue();
        assertNotNull(savedCart);
        assertEquals(1, savedCart.getItems().size());
        CartItem item = savedCart.getItems().get(0);
        assertEquals(2, item.getQuantity());
        assertEquals(product, item.getProduct());
    }

    @Test
    void testViewCart_UserHasNoCart() {
        // Mock dependencies
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Execute service method
        Exception exception = assertThrows(RuntimeException.class, () -> cartService.viewCart("testuser"));
        assertEquals("Cart not found", exception.getMessage());
    }

}