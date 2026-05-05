package com.vrs.auth.service;

import com.vrs.auth.dto.*;
import com.vrs.auth.entity.Role;
import com.vrs.auth.entity.User;
import com.vrs.auth.repository.RoleRepository;
import com.vrs.auth.repository.UserRepository;
import com.vrs.common.exception.ApiException;
import com.vrs.common.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw ApiException.conflict("Email already registered");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> ApiException.conflict("Default role missing; check seed data"));

        User user = User.builder()
                .email(req.email().toLowerCase())
                .passwordHash(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .phone(req.phone())
                .enabled(true)
                .roles(Set.of(userRole))
                .build();
        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email().toLowerCase())
                .orElseThrow(() -> ApiException.unauthorized("Invalid credentials"));

        if (!user.isEnabled()) {
            throw ApiException.forbidden("Account is disabled");
        }
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("Invalid credentials");
        }
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshRequest req) {
        try {
            Jws<Claims> jws = jwtUtil.parse(req.refreshToken());
            String type = jws.getPayload().get("type", String.class);
            if (!"refresh".equals(type)) {
                throw ApiException.unauthorized("Invalid refresh token");
            }
            Long userId = Long.valueOf(jws.getPayload().getSubject());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> ApiException.unauthorized("User not found"));
            return buildAuthResponse(user);
        } catch (Exception e) {
            throw ApiException.unauthorized("Invalid or expired refresh token");
        }
    }

    @Transactional(readOnly = true)
    public UserDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));
        return toUserDto(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        String access = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refresh = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
        return new AuthResponse(access, refresh, user.getId(), user.getEmail(), user.getFullName(), roles);
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRoles().stream().map(Role::getName).toList(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}
