package com.newlook.user.entity;

import com.newlook.user.entity.UserProfile;
import com.newlook.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "user_roles",
        schema = "user_db",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_roles_profile_role",
                columnNames = {"user_id", "role"}
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
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            nullable = false
    )
    private UserProfile userProfile;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "role",
            nullable = false,
            columnDefinition = "user_db.user_role"
    )
    private UserRole role;
}