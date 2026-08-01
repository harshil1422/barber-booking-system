package com.newlook.user.service;

import com.newlook.user.dto.AddRoleRequest;
import com.newlook.user.dto.CreateUserProfileRequest;
import com.newlook.user.dto.UpdateUserProfileRequest;
import com.newlook.user.dto.UserProfileResponse;

import java.util.UUID;

public interface UserProfileService {

    /** Called internally by auth-service right after signup. Idempotent on userId. */
    UserProfileResponse createProfile(CreateUserProfileRequest request);

    UserProfileResponse getByUserId(UUID userId);

    UserProfileResponse updateProfile(UUID userId, UpdateUserProfileRequest request);

    UserProfileResponse addRole(UUID userId, AddRoleRequest request);

    /** Called by booking-service via internal REST after a booking completes. */
    void recordBooking(UUID userId);
}
