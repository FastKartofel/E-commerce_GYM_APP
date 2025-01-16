package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.CartItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
//
    @Transactional
    public void addToCart(Long productId, int quantity, String username) {
        // Find the user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find the product by productId
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Get the user's cart, or create a new one if it doesn't exist
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
        }

        // Check if the product is already in the cart
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        // Update quantity if the product is already in the cart
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity); // Update quantity
        } else {
            // Add new item to the cart if not already present
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            newCartItem.setCart(cart);
            cart.getItems().add(newCartItem);
        }

        // Save the cart with the updated items
        cartRepository.save(cart);
    }

    public Cart viewCart(String username) {
        // Find the user and return their cart
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return Optional.ofNullable(user.getCart()).orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Transactional
    public void removeFromCart(Long productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = user.getCart();
        if (cart != null) {
            boolean itemRemoved = cart.getItems().removeIf(item -> {
                boolean match = item.getProduct().getId().equals(productId);
                System.out.println("Attempting to remove product with id: " + productId + ", match: " + match);
                return match;
            });

            if (itemRemoved) {
                System.out.println("Product removed successfully from cart.");
                cartRepository.saveAndFlush(cart);
            } else {
                throw new RuntimeException("Product not found in cart.");
            }
        } else {
            throw new RuntimeException("Cart not found");
        }
    }
}
