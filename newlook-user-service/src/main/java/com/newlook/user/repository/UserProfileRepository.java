package com.newlook.user.repository;

import com.newlook.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserId(UUID userId);
    Optional<UserProfile> findByPhone(String phone);
    boolean existsByUserId(UUID userId);
    boolean existsByPhone(String phone);
}
