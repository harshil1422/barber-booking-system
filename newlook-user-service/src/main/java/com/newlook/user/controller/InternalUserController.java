package com.newlook.user.controller;

import com.newlook.user.dto.CreateUserProfileRequest;
import com.newlook.user.dto.UserProfileResponse;
import com.newlook.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Service-to-service only. Never routed through the public API Gateway —
 * restrict at the network/gateway layer (internal-only ingress, or the
 * InternalApiKeyFilter's X-Internal-Api-Key check) so external clients
 * can't reach it directly.
 */
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Tag(name = "Internal - User Provisioning")
public class InternalUserController {

    private final UserProfileService userProfileService;

    @Operation(summary = "Called by auth-service right after a new account is created/verified")
    @PostMapping
    public ResponseEntity<UserProfileResponse> createProfile(
            @Valid @RequestBody CreateUserProfileRequest request) {
        UserProfileResponse response = userProfileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Called by booking-service after a booking is completed, to update stats")
    @PostMapping("/{userId}/booking-completed")
    public ResponseEntity<Void> recordBooking(@PathVariable UUID userId) {
        userProfileService.recordBooking(userId);
        return ResponseEntity.noContent().build();
    }
}
