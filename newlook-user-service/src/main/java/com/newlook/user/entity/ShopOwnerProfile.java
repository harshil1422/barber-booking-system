package com.newlook.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "shop_owner_profiles",
        schema = "user_db",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_shop_owner_profiles_user",
                columnNames = "user_id"
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
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            nullable = false,
            unique = true
    )
    private UserProfile userProfile;

    /** Cross-service reference — the actual Shop record lives in shop-service */
    @Column(name = "shop_id")
    private UUID shopId;

    @Column(name = "kyc_verified", nullable = false)
    @Builder.Default
    private boolean verified = false;
}