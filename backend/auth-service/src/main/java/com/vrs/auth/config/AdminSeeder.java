package com.vrs.auth.config;

import com.vrs.auth.entity.Role;
import com.vrs.auth.entity.User;
import com.vrs.auth.repository.RoleRepository;
import com.vrs.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    @Value("${app.admin.seed-email}")
    private String adminEmail;
    @Value("${app.admin.seed-password}")
    private String adminPassword;
    @Value("${app.admin.seed-name}")
    private String adminName;

    @Override
    public void run(String... args) {
        if (userRepo.existsByEmail(adminEmail)) {
            return;
        }
        Role admin = roleRepo.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN missing"));
        Role user = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER missing"));

        Set<Role> roles = new HashSet<>();
        roles.add(admin);
        roles.add(user);

        User u = User.builder()
                .email(adminEmail.toLowerCase())
                .passwordHash(encoder.encode(adminPassword))
                .fullName(adminName)
                .phone("0000000000")
                .enabled(true)
                .roles(roles)
                .build();
        userRepo.save(u);
        log.info("Seeded admin user: {}", adminEmail);
    }
}
