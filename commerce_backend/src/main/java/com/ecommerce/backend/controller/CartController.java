package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.CartItemDto;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.service.CartService;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItemDto cartItemDto, @AuthenticationPrincipal UserDetails userDetails) {
        cartService.addToCart(cartItemDto.getProductId(), cartItemDto.getQuantity(), userDetails.getUsername());
        return ResponseEntity.ok("Item added to cart");
    }

    @GetMapping
    public ResponseEntity<Cart> viewCart(@AuthenticationPrincipal UserDetails userDetails) {
        Cart cart = cartService.viewCart(userDetails.getUsername());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCart(@RequestParam Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("Authorities: " + userDetails.getAuthorities());
        cartService.removeFromCart(productId, userDetails.getUsername());
        return ResponseEntity.ok("Item removed from cart");
    }


}