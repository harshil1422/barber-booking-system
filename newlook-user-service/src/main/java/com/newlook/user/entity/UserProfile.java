package com.newlook.user.entity;

import com.newlook.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core user profile entity.
 *
 * user_id is the cross-service identity key shared with auth_db.users.
 * No FK constraint — the Auth service owns user creation; this service
 * is notified via the API Gateway's X-User-Id header or internal REST call.
 */
@Entity
@Table(
        name = "user_profiles",
        schema = "user_db",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_profiles_user_id", columnNames = "user_id"),
                @UniqueConstraint(name = "uk_user_profiles_phone",   columnNames = "phone"),
                @UniqueConstraint(name = "uk_user_profiles_email",   columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_user_profiles_phone",   columnList = "phone"),
                @Index(name = "idx_user_profiles_status",  columnList = "status"),
                @Index(name = "idx_user_profiles_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"roles", "shopOwnerProfile", "preferences"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    /** Cross-service identifier — matches auth_db.users.id */
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "preferred_language", length = 10, nullable = false)
    @Builder.Default
    private String preferredLanguage = "en";

    @Column(name = "total_bookings", nullable = false)
    @Builder.Default
    private Integer totalBookings = 0;

    @Column(name = "last_booking_at")
    private Instant lastBookingAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ── Relationships ──────────────────────────────────────────

    @OneToMany(
            mappedBy = "userProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER    // roles always needed — small list (max 3 enums)
    )
    @Builder.Default
    private List<UserRoleEntity> roles = new ArrayList<>();

    @OneToOne(
            mappedBy = "userProfile",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private ShopOwnerProfile shopOwnerProfile;

    @OneToOne(
            mappedBy = "userProfile",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private UserPreferences preferences;

    // ── Domain helpers ─────────────────────────────────────────

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }

    public boolean isBarber() {
        return roles.stream()
                .anyMatch(r -> com.newlook.user.enums.UserRole.BARBER.equals(r.getRole()));
    }

    public boolean hasShop() {
        return shopOwnerProfile != null && shopOwnerProfile.getShopId() != null;
    }

    /** Add a role if not already present — idempotent */
    public void addRole(com.newlook.user.enums.UserRole role) {
        boolean alreadyHas = roles.stream().anyMatch(r -> role.equals(r.getRole()));
        if (!alreadyHas) {
            UserRoleEntity roleEntity = UserRoleEntity.builder()
                    .userProfile(this)
                    .role(role)
                    .build();
            roles.add(roleEntity);
        }
    }

    /** Increment booking counter — called by internal event handler */
    public void recordBooking() {
        this.totalBookings++;
        this.lastBookingAt = Instant.now();
    }
}