package com.ecommerce.backend.security;

import com.ecommerce.backend.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Extract JWT token from Authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.debug("Extracted username from JWT: {}", username);
            } catch (ExpiredJwtException e) {
                logger.error("JWT token has expired", e);
            } catch (Exception e) {
                logger.error("Error extracting username from JWT", e);
            }
        } else {
            logger.debug("No JWT token found in request headers.");
        }

        // Validate the token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.debug("UserDetails loaded for user: {}", username);

            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                List<String> rolesFromJWT = jwtUtil.getRolesFromJWT(jwt);
                if (rolesFromJWT != null && !rolesFromJWT.isEmpty()) {
                    List<GrantedAuthority> authorities = rolesFromJWT.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.debug("Authentication token set for user: {}", username);
                } else {
                    logger.warn("No roles found in JWT token for user: {}", username);
                    // Optionally, you can throw an exception or handle it as per your requirements
                }
            } else {
                logger.warn("JWT token validation failed for user: {}", username);
            }
        }

        chain.doFilter(request, response);
    }
}
