package com.newlook.user.dto;

import com.newlook.user.enums.UserRole;
import com.newlook.user.enums.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
