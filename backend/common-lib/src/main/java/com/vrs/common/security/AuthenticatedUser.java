package com.vrs.common.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public record AuthenticatedUser(Long userId, String email, List<SimpleGrantedAuthority> authorities) {
}
