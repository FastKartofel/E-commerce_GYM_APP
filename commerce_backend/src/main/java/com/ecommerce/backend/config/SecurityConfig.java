package com.ecommerce.backend.config;

import com.ecommerce.backend.security.CustomAuthenticationEntryPoint;
import com.ecommerce.backend.security.JwtAuthenticationFilter;
import com.ecommerce.backend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /**
     * Password encoder bean using BCrypt.
     *
     * @return PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DaoAuthenticationProvider bean setup.
     *
     * @return DaoAuthenticationProvider instance.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * CORS configuration to allow requests from the frontend.
     *
     * @return CorsConfigurationSource instance.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Frontend URL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints
        return source;
    }

    /**
     * Security filter chain configuration.
     *
     * @param http HttpSecurity instance.
     * @return SecurityFilterChain instance.
     * @throws Exception in case of configuration errors.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF as JWT is used
                .csrf(csrf -> csrf.disable())

                // Handle unauthorized access
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )

                // Set session management to stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Set authorization rules
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/users/register", "/api/auth/login").permitAll()

                        // Allow GET requests on /api/products/** to any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated()

                        // Restrict POST and DELETE requests on /api/products/** to ADMIN role
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // Admin-only access to all orders
                        .requestMatchers(HttpMethod.GET, "/api/orders/all").hasRole("ADMIN")

                        // Authenticated users can place orders
                        .requestMatchers(HttpMethod.POST, "/api/orders/place").authenticated()

                        // Users and admins can update user information
                        .requestMatchers(HttpMethod.PUT, "/api/users/update").hasAnyRole("USER", "ADMIN")

                        // Profile endpoint requires authentication
                        .requestMatchers("/api/users/profile").authenticated()

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )

                // Add JWT authentication filter before the UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Set the authentication provider
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    /**
     * AuthenticationManager bean setup.
     *
     * @param config AuthenticationConfiguration instance.
     * @return AuthenticationManager instance.
     * @throws Exception in case of configuration errors.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
