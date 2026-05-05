package com.vrs.gateway.filter;

import com.vrs.common.security.AuthHeaders;
import com.vrs.common.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * Validates JWTs (where required) and forwards user identity headers to downstream services.
 * Public routes (login, register, vehicle browsing) skip validation.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/auth/health",
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui"
    );

    private static final Set<String> PUBLIC_GET_PREFIXES = Set.of(
            "/vehicles",
            "/vehicles/search",
            "/vehicles/uploads"
    );

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod() == null ? "GET" : request.getMethod().name();

        if (isPublic(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        try {
            Jws<Claims> jws = jwtUtil.parse(token);
            String type = jws.getPayload().get("type", String.class);
            if (!"access".equals(type)) {
                return unauthorized(exchange, "Token type must be 'access'");
            }
            Long userId = Long.valueOf(jws.getPayload().getSubject());
            String email = jws.getPayload().get("email", String.class);
            Object rolesObj = jws.getPayload().get("roles");
            String roles = rolesObj instanceof List<?> list
                    ? list.stream().map(Object::toString).reduce((a, b) -> a + "," + b).orElse("")
                    : "";

            ServerHttpRequest mutated = request.mutate()
                    .header(AuthHeaders.USER_ID, String.valueOf(userId))
                    .header(AuthHeaders.USER_EMAIL, email == null ? "" : email)
                    .header(AuthHeaders.USER_ROLES, roles)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (Exception ex) {
            log.warn("JWT validation failed: {}", ex.getMessage());
            return unauthorized(exchange, "Invalid or expired token");
        }
    }

    private boolean isPublic(String path, String method) {
        for (String p : PUBLIC_PATH_PREFIXES) {
            if (path.startsWith(p)) return true;
        }
        if ("GET".equalsIgnoreCase(method)) {
            for (String p : PUBLIC_GET_PREFIXES) {
                if (path.equals(p) || path.startsWith(p + "/")) return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        ServerHttpResponse resp = exchange.getResponse();
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"status\":401,\"message\":\"" + msg + "\"}";
        DataBuffer buf = resp.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Mono.just(buf));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
