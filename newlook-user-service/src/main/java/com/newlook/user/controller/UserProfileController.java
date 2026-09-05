package com.newlook.user.controller;

import com.newlook.user.dto.AddRoleRequest;
import com.newlook.user.dto.UpdateUserProfileRequest;
import com.newlook.user.dto.UserProfileResponse;
import com.newlook.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Public-facing endpoints, only reachable through the API Gateway.
 * The gateway authenticates the JWT and forwards the resolved user id
 * as the X-User-Id header — this service trusts the gateway boundary
 * and never validates the JWT itself.
 */
@RestController
@RequestMapping("/user/users")
@RequiredArgsConstructor
@Tag(name = "User Profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "Get the authenticated user's own profile")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
           ) {
        UUID userId= UUID.fromString("f302702a-8024-4d8f-b213-da847db9004d");
        return ResponseEntity.ok(userProfileService.getByUserId(userId));
    }

    @Operation(summary = "Update the authenticated user's own profile")
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    @Operation(summary = "Add a role to the authenticated user, e.g. become a barber")
    @PostMapping("/me/roles")
    public ResponseEntity<UserProfileResponse> addRole(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody AddRoleRequest request) {
        return ResponseEntity.ok(userProfileService.addRole(userId, request));
    }

    @Operation(summary = "Fetch a profile by userId — used by other services behind the gateway")
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(userProfileService.getByUserId(userId));
    }
}
