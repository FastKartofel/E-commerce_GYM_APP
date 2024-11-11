//package com.ecommerce.backend.service;
//
//import com.ecommerce.backend.dto.CartItemDto;
//import com.ecommerce.backend.entity.Order;
//import com.ecommerce.backend.entity.Product;
//import com.ecommerce.backend.entity.User;
//import com.ecommerce.backend.exception.InsufficientStockException;
//import com.ecommerce.backend.exception.ProductNotFoundException;
//import com.ecommerce.backend.repository.OrderRepository;
//import com.ecommerce.backend.repository.ProductRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.dao.OptimisticLockingFailureException;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class OrderServiceTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    private User user;
//    private Product product1;
//    private Product product2;
//    private CartItemDto cartItem1;
//    private CartItemDto cartItem2;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        user = new User();
//        user.setId(1L);
//        user.setUsername("john_doe");
//
//        product1 = new Product();
//        product1.setId(1L);
//        product1.setName("Whey Protein");
//        product1.setPrice(29.99);
//        product1.setStockQuantity(100);
//
//        product2 = new Product();
//        product2.setId(2L);
//        product2.setName("Creatine Monohydrate");
//        product2.setPrice(19.99);
//        product2.setStockQuantity(50);
//
//        cartItem1 = new CartItemDto();
//        cartItem1.setProductId(1L);
//        cartItem1.setQuantity(2);
//
//        cartItem2 = new CartItemDto();
//        cartItem2.setProductId(2L);
//        cartItem2.setQuantity(1);
//    }
//
//    @Test
//    void placeOrder_Success() throws InsufficientStockException, ProductNotFoundException {
//        List<CartItemDto> cartItems = Arrays.asList(cartItem1, cartItem2);
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
//        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
//        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Order order = orderService.placeOrder(user, cartItems);
//
//        assertNotNull(order);
//        assertEquals(user, order.getUser());
//        assertEquals(2, order.getOrderItems().size());
//        assertEquals(29.99 * 2 + 19.99 * 1, order.getTotalAmount());
//
//        // Verify stock decrement
//        assertEquals(98, product1.getStockQuantity());
//        assertEquals(49, product2.getStockQuantity());
//
//        verify(productRepository, times(1)).save(product1);
//        verify(productRepository, times(1)).save(product2);
//        verify(orderRepository, times(1)).save(order);
//    }
//
//    @Test
//    void placeOrder_ProductNotFound() {
//        List<CartItemDto> cartItems = Arrays.asList(cartItem1);
//
//        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//
//        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
//            orderService.placeOrder(user, cartItems);
//        });
//
//        assertEquals("Product not found: 1", exception.getMessage());
//        verify(productRepository, never()).save(any(Product.class));
//        verify(orderRepository, never()).save(any(Order.class));
//    }
//
//    @Test
//    void placeOrder_InsufficientStock() {
//        List<CartItemDto> cartItems = Arrays.asList(cartItem1);
//
//        product1.setStockQuantity(1); // Less than requested quantity
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
//
//        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> {
//            orderService.placeOrder(user, cartItems);
//        });
//
//        assertEquals("Insufficient stock for product: Whey Protein", exception.getMessage());
//        verify(productRepository, never()).save(any(Product.class));
//        verify(orderRepository, never()).save(any(Order.class));
//    }
//
//    @Test
//    void placeOrder_OptimisticLockingFailure() throws InsufficientStockException, ProductNotFoundException {
//        List<CartItemDto> cartItems = Arrays.asList(cartItem1);
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
//        when(productRepository.save(product1)).thenThrow(new OptimisticLockingFailureException("Optimistic Locking Failed"));
//
//        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> {
//            orderService.placeOrder(user, cartItems);
//        });
//
//        assertEquals("Failed to place order due to stock changes. Please try again.", exception.getMessage());
//        verify(productRepository, times(1)).save(product1);
//        verify(orderRepository, never()).save(any(Order.class));
//    }
//}
