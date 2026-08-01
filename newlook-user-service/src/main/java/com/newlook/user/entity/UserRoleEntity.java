package com.newlook.user.entity;

import com.newlook.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "user_roles",
        schema = "user_db",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_roles_profile_role",
                columnNames = {"user_profile_id", "role"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "userProfile")
public class UserRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;
}
