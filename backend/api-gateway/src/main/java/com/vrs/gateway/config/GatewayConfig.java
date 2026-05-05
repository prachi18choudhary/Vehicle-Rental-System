package com.vrs.gateway.config;

import com.vrs.common.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class GatewayConfig {

    @Bean
    public JwtUtil jwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-ttl-ms}") long accessTtl,
            @Value("${app.jwt.refresh-token-ttl-ms}") long refreshTtl,
            @Value("${app.jwt.issuer}") String issuer
    ) {
        return new JwtUtil(secret, accessTtl, refreshTtl, issuer);
    }

    @Bean
    public CorsWebFilter corsWebFilter(@Value("${app.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration cors = new CorsConfiguration();
        for (String origin : allowedOrigins.split(",")) {
            cors.addAllowedOrigin(origin.trim());
        }
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.setAllowCredentials(true);
        cors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", cors);
        return new CorsWebFilter(source);
    }
}
