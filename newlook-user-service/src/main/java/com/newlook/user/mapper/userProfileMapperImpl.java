package com.newlook.user.mapper;

import com.newlook.user.dto.UserProfileResponse;
import com.newlook.user.entity.UserProfile;
import com.newlook.user.enums.UserRole;
import com.newlook.user.enums.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-30T00:44:46+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Amazon.com Inc.)"
)
@Component
public class userProfileMapperImpl implements UserProfileMapper {

    @Override
    public UserProfileResponse toResponse(UserProfile entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        UUID userId = null;
        String name = null;
        String phone = null;
        String email = null;
        String avatarUrl = null;
        UserStatus status = null;
        String preferredLanguage = null;
        Integer totalBookings = null;
        Instant lastBookingAt = null;
        Instant createdAt = null;

        id = entity.getId();
        userId = entity.getUserId();
        name = entity.getName();
        phone = entity.getPhone();
        email = entity.getEmail();
        avatarUrl = entity.getAvatarUrl();
        status = entity.getStatus();
        preferredLanguage = entity.getPreferredLanguage();
        totalBookings = entity.getTotalBookings();
        lastBookingAt = entity.getLastBookingAt();
        createdAt = entity.getCreatedAt();

        List<UserRole> roles = mapRoles(entity);
        boolean hasShop = entity.hasShop();
        UUID shopId = entity.hasShop() ? entity.getShopOwnerProfile().getShopId() : null;

        UserProfileResponse userProfileResponse = new UserProfileResponse( id, userId, name, phone, email, avatarUrl, status, preferredLanguage, totalBookings, lastBookingAt, roles, hasShop, shopId, createdAt );

        return userProfileResponse;
    }
}
