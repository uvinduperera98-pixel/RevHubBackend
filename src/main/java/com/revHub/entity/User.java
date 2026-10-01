package com.revHub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, exclude = {"roles"})
@ToString(exclude = {"roles"}) // Prevent infinite loop in logs
@Entity
@Table(name = "user_account")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 100, unique = true, nullable = false)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "speciality", length = 255)
    private String speciality;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "must_change_password")
    private Boolean mustChangePassword;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @BatchSize(size = 25) // Tells Hibernate to batch-fetch roles safely across paginated elements
    private Set<Role> roles = new HashSet<>();

    // Helper methods to keep bidirectional association in sync
    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }
}
