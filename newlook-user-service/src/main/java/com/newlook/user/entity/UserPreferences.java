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
                columnNames = "user_id"
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
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            nullable = false,
            unique = true
    )
    private UserProfile userProfile;

    @Column(name = "sms_notifications", nullable = false)
    @Builder.Default
    private boolean smsNotifications = true;

    @Column(name = "push_notifications", nullable = false)
    @Builder.Default
    private boolean pushNotifications = true;

    @Column(name = "booking_reminders", nullable = false)
    @Builder.Default
    private boolean bookingReminders = true;

    @Column(name = "marketing_notifications", nullable = false)
    @Builder.Default
    private boolean marketingNotifications = false;

    @Column(name = "default_radius_km", nullable = false)
    @Builder.Default
    private Integer defaultRadiusKm = 5;
}