package com.newlook.user.service;

import com.newlook.user.dto.AddRoleRequest;
import com.newlook.user.dto.CreateUserProfileRequest;
import com.newlook.user.dto.UpdateUserProfileRequest;
import com.newlook.user.dto.UserProfileResponse;
import com.newlook.user.entity.UserPreferences;
import com.newlook.user.entity.UserProfile;
import com.newlook.user.exception.DuplicateUserException;
import com.newlook.user.exception.UserProfileNotFoundException;
import com.newlook.user.mapper.UserProfileMapper;
import com.newlook.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository repository;
    private final UserProfileMapper mapper;

    @Override
    @Transactional
    public UserProfileResponse createProfile(CreateUserProfileRequest request) {
        // Idempotent: auth-service may retry this call safely.
        if (repository.existsByUserId(request.userId())) {
            log.info("Profile already exists for userId={}, returning existing", request.userId());
            return mapper.toResponse(repository.findByUserId(request.userId()).orElseThrow());
        }

        if (repository.existsByPhone(request.phone())) {
            throw new DuplicateUserException("A profile with phone " + request.phone() + " already exists");
        }

        UserProfile profile = UserProfile.builder()
                .userId(request.userId())
                .name(request.name())
                .phone(request.phone())
                .email(request.email())
                .build();

        profile.addRole(request.initialRole());

        UserPreferences preferences = UserPreferences.builder()
                .userProfile(profile)
                .build();
        profile.setPreferences(preferences);

        UserProfile saved = repository.save(profile);
        log.info("Created user profile id={} for userId={}", saved.getId(), saved.getUserId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getByUserId(UUID userId) {
        return mapper.toResponse(findOrThrow(userId));
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateUserProfileRequest request) {
        UserProfile profile = findOrThrow(userId);

        if (StringUtils.hasText(request.name())) {
            profile.setName(request.name());
        }
        if (StringUtils.hasText(request.email())) {
            profile.setEmail(request.email());
        }
        if (StringUtils.hasText(request.avatarUrl())) {
            profile.setAvatarUrl(request.avatarUrl());
        }
        if (StringUtils.hasText(request.preferredLanguage())) {
            profile.setPreferredLanguage(request.preferredLanguage());
        }

        return mapper.toResponse(repository.save(profile));
    }

    @Override
    @Transactional
    public UserProfileResponse addRole(UUID userId, AddRoleRequest request) {
        UserProfile profile = findOrThrow(userId);
        profile.addRole(request.role());
        return mapper.toResponse(repository.save(profile));
    }

    @Override
    @Transactional
    public void recordBooking(UUID userId) {
        UserProfile profile = findOrThrow(userId);
        profile.recordBooking();
        repository.save(profile);
    }

    private UserProfile findOrThrow(UUID userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(userId));
    }
}
