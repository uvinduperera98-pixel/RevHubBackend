package com.revHub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_account_history",
        indexes = {
                @Index(name = "idx_user_account_history_user_id", columnList = "user_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userHistoryId;

    @Column(name = "user_id", nullable = false)
    private Long userId; // Reference to original User Account ID

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "speciality", length = 255)
    private String speciality;

    @Column(name = "active")
    private boolean active;

    @Column(name = "must_change_password")
    private Boolean mustChangePassword;

    @Column(name = "action_type", length = 30)
    private String actionType; // e.g., "UPDATE", "DELETE"
}
