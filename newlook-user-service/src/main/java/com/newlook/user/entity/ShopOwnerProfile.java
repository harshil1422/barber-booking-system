package com.newlook.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Marks a user as a shop owner/barber and holds the cross-service pointer
 * to the actual Shop aggregate, which is owned by shop-service.
 */
@Entity
@Table(
        name = "shop_owner_profiles",
        schema = "user_db",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_shop_owner_profiles_user",
                columnNames = "user_profile_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "userProfile")
public class ShopOwnerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private UserProfile userProfile;

    /** Cross-service reference — the actual Shop record lives in shop-service */
    @Column(name = "shop_id")
    private UUID shopId;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean verified = false;
}
