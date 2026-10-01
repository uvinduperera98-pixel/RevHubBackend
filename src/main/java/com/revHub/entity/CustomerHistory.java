package com.revHub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_history")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long customerHistoryId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId; // Reference to original Customer ID

    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Column(name = "customer_address", length = 255)
    private String customerAddress;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "driving_license_number", length = 255)
    private String drivingLicenseNumber;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "action_type", length = 30)
    private String actionType; // e.g., "UPDATE", "DELETE"
}
