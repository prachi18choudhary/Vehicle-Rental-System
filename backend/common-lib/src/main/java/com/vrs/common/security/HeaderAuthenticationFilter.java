package com.vrs.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads X-User-Id, X-User-Email, X-User-Roles headers (set by API Gateway after JWT validation)
 * and populates the SecurityContext. Used by all downstream services.
 */
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String userId = request.getHeader(AuthHeaders.USER_ID);
        String email = request.getHeader(AuthHeaders.USER_EMAIL);
        String roles = request.getHeader(AuthHeaders.USER_ROLES);

        if (userId != null && !userId.isBlank()) {
            List<SimpleGrantedAuthority> authorities = roles == null || roles.isBlank()
                    ? List.of()
                    : Arrays.stream(roles.split(","))
                        .map(String::trim)
                        .filter(r -> !r.isEmpty())
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

            AuthenticatedUser principal = new AuthenticatedUser(Long.parseLong(userId), email, authorities);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }
}
