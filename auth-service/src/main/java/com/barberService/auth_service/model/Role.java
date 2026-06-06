package com.barberService.auth_service.model;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;  // e.g. ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR
}
