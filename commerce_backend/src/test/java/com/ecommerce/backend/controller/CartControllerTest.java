package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.CartItemDto;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToCart() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProductId(1L);
        cartItemDto.setQuantity(2);

        // Execute the method
        ResponseEntity<String> response = cartController.addToCart(cartItemDto, mock(User.class));

        // Assert the results
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Item added to cart", response.getBody());
    }

    @Test
    void testViewCart() {
        // Mock a User and username
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");

        // Mock the Cart object
        Cart mockCart = new Cart();
        when(cartService.viewCart("testuser")).thenReturn(mockCart);

        // Execute the method
        ResponseEntity<Cart> response = cartController.viewCart(user);

        // Assert the results
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCart, response.getBody());

        // Verify interactions
        verify(cartService, times(1)).viewCart("testuser");
    }

    @Test
    void testRemoveFromCart() {
        // Mock a User
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");

        // Execute the method
        ResponseEntity<String> response = cartController.removeFromCart(1L, user);

        // Assert the results
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Item removed from cart", response.getBody());

        // Verify interactions
        verify(cartService, times(1)).removeFromCart(1L, "testuser");
    }

    @Test
    void testRemoveFromCart_ItemNotFound() {
        // Mock a User
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");

        // Mock behavior
        doThrow(new RuntimeException("Item not found")).when(cartService).removeFromCart(1L, "testuser");

        // Execute method and assert exception
        Exception exception = assertThrows(RuntimeException.class,
                () -> cartController.removeFromCart(1L, user));
        assertEquals("Item not found", exception.getMessage());
    }

}
