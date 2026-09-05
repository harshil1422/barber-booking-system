package com.barberService.auth_service.dto;

import com.barberService.auth_service.enums.UserRole;
import com.barberService.auth_service.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UserServiceDtos {

    public record   CreateUserProfileRequest(

            UUID userId,
            String name,
            String phone,
            String email,
            UserRole initialRole

    ){}

    public record UserProfileResponse(
            UUID id,
            UUID userId,
            String name,
            String phone,
            String email,
            String avatarUrl,
            UserStatus status,
            String preferredLanguage,
            Integer totalBookings,
            Instant lastBookingAt,
            List<UserRole> roles,
            boolean hasShop,
            UUID shopId,
            Instant createdAt
    ) {}
}
