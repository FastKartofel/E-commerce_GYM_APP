package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.UserProfileUpdateDto;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already taken", HttpStatus.BAD_REQUEST);
        }
        userService.registerUser(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    /**we are using login in the AuthenticationController
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        boolean isAuthenticated = userService.authenticate(user.getUsername(), user.getPassword());
        if (isAuthenticated) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
    */

    @PutMapping("/update")
    public ResponseEntity<String> updateUserProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                    @RequestBody UserProfileUpdateDto profileUpdateDto) {
        userService.updateUserProfile(userDetails.getUsername(), profileUpdateDto);
        return ResponseEntity.ok("User profile updated successfully");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileUpdateDto> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileUpdateDto profile = new UserProfileUpdateDto();
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        profile.setShippingAddress(user.getShippingAddress());
        profile.setPaymentDetails(user.getPaymentDetails());
        profile.setRole(user.getRole().name());

        return ResponseEntity.ok(profile);
    }
}
