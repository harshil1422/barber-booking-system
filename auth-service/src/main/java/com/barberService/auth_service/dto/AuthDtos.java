package com.barberService.auth_service.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// ─── Login ───────────────────────────────────────────────────────────────────

public class AuthDtos {

    public record LoginRequest(
            @NotBlank(message = "Username or email is required")
            String username,

            @NotBlank(message = "Password is required")
            String password
    ) {}

    // ─── Register ────────────────────────────────────────────────────────────

    public record RegisterRequest(
            @NotBlank(message = "Username is required")
            @Size(min = 3, max = 50, message = "Username must be 3–50 characters")
            String username,

            @NotBlank(message = "Email is required")
            @Email(message = "Must be a valid email")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 8, message = "Password must be at least 8 characters")
            String password
    ) {}

    // ─── Token response ──────────────────────────────────────────────────────

    public record TokenResponse(
            String accessToken,
            String refreshToken   // only populated internally; never sent in response body
    ) {}

    // ─── Error response ──────────────────────────────────────────────────────

    public record ErrorResponse(
            int status,
            String error,
            String message,
            String path,
            String timestamp
    ) {}
}
