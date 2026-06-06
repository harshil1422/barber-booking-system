package com.barberService.auth_service.controller;

import com.barberService.auth_service.model.User;
import com.barberService.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    /** Returns the currently authenticated user's profile. */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
                "id",       user.getId(),
                "username", user.getUsername(),
                "email",    user.getEmail(),
                "roles",    user.getAuthorities()
        ));
    }

    /** Admin-only: lock or unlock a user account. */
    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setLocked(
            @PathVariable UUID id,
            @RequestParam boolean locked) {

        userRepository.findById(id).ifPresent(u -> {
            u.setLocked(locked);
            userRepository.save(u);
        });
        return ResponseEntity.noContent().build();
    }

    /** Admin-only: list all users. */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(
                userRepository.findAll().stream().map(u -> Map.of(
                        "id",       u.getId(),
                        "username", u.getUsername(),
                        "email",    u.getEmail(),
                        "enabled",  u.isEnabled(),
                        "locked",   u.isLocked()
                )).toList()
        );
    }
}
