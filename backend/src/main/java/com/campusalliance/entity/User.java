package com.campusalliance.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * A registered user — student, faculty member, or admin.
 *
 * Role is stored as a single @Enumerated column (not a join table) because
 * each user has exactly one role. If we ever needed multiple roles per user,
 * we'd refactor to a Set<Role> with @ElementCollection.
 *
 * Table is named "users" because "user" is a reserved word in PostgreSQL
 * and would require quoting in every raw query.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt hash — never stored in plain text

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING) // Store "STUDENT" not 0 — readable in DB queries
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
