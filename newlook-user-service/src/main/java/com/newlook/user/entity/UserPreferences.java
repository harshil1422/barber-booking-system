package com.newlook.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "user_preferences",
        schema = "user_db",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_preferences_user",
                columnNames = "user_profile_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "userProfile")
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private UserProfile userProfile;

    @Column(name = "notify_sms", nullable = false)
    @Builder.Default
    private boolean notifySms = true;

    @Column(name = "notify_email", nullable = false)
    @Builder.Default
    private boolean notifyEmail = true;

    @Column(name = "preferred_radius_km")
    @Builder.Default
    private Double preferredRadiusKm = 2.0;
}
