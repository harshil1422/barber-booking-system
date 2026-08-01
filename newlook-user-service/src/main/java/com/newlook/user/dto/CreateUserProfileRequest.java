package com.newlook.user.dto;

import com.newlook.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

/**
 * Sent by auth-service -> POST /api/v1/internal/users right after a user
 * successfully registers/verifies their phone number.
 */
public record CreateUserProfileRequest(

        @NotNull
        UUID userId,

        @NotBlank
        String name,

        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "phone must be 10-15 digits, optional leading +")
        String phone,

        @Email
        String email,

        @NotNull
        UserRole initialRole
) {}
