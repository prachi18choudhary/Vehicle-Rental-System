package com.vrs.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;
    private final long refreshExpirationMs;
    private final String issuer;

    public JwtUtil(String secret, long expirationMs, long refreshExpirationMs, String issuer) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
        this.issuer = issuer;
    }

    public String generateAccessToken(Long userId, String email, List<String> roles) {
        return buildToken(userId, email, roles, expirationMs, "access");
    }

    public String generateRefreshToken(Long userId, String email) {
        return buildToken(userId, email, List.of(), refreshExpirationMs, "refresh");
    }

    private String buildToken(Long userId, String email, List<String> roles, long ttlMs, String type) {
        Date now = new Date();
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("roles", roles)
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlMs))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token);
    }

    public Long extractUserId(String token) {
        return Long.valueOf(parse(token).getPayload().getSubject());
    }

    public String extractEmail(String token) {
        return parse(token).getPayload().get("email", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = parse(token).getPayload().get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    public String extractType(String token) {
        return parse(token).getPayload().get("type", String.class);
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Map<String, Object> claimsAsMap(String token) {
        return parse(token).getPayload();
    }
}
