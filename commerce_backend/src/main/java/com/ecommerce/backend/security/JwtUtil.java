package com.ecommerce.backend.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime; // e.g., 86400000 for 1 day in milliseconds

    // Extract username from JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract a specific claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            logger.error("JWT token has expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            throw e;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token has expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generate JWT token with roles
    public String generateToken(String username, List<String> roles) {
        // Ensure roles have the "ROLE_" prefix
        List<String> prefixedRoles = roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", prefixedRoles); // Include roles in the token

        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // Validate JWT token
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = extractUsername(token);
            boolean isValid = (tokenUsername.equals(username) && !isTokenExpired(token));
            if (isValid) {
                logger.debug("JWT token is valid for user: {}", username);
            } else {
                logger.warn("JWT token validation failed for user: {}", username);
            }
            return isValid;
        } catch (Exception e) {
            logger.error("JWT token validation error: {}", e.getMessage());
            return false;
        }
    }

    // Extract roles from JWT
    public List<String> getRolesFromJWT(String token) {
        try {
            Claims claims = extractAllClaims(token);
            List<String> roles = claims.get("roles", List.class);
            if (roles == null) {
                logger.warn("Roles claim is missing in JWT token.");
                return Collections.emptyList();
            }
            return roles;
        } catch (Exception e) {
            logger.error("Error extracting roles from JWT: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
