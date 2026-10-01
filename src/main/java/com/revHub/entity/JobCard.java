package com.revHub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "job_card",
        indexes = {
                @Index(name = "idx_job_card_status_date", columnList = "status, created_date"),
                @Index(name = "idx_job_card_vehicle_id", columnList = "vehicle_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Column(nullable = true, unique = true, length = 50)
    private String jobCardNumber; // Business Key (e.g., "JC-001")

    // FIX: Changed List<Vehicle> to Vehicle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnoreProperties({"jobCards", "customer"})
    private Vehicle vehicle;

    @Column(name = "estimated_completion_time")
    private LocalDateTime estimatedCompletionTime;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "customer_complaint_text")
    private String customerComplaintText;

    @Column(name = "current_mileage")
    private Double currentMileage;

    // --- MANY-TO-MANY RELATIONSHIP SETUP ---
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "job_card_technician_mapping", // Name of the underlying join table
            joinColumns = @JoinColumn(name = "job_id"), // FK pointing to JobCard
            inverseJoinColumns = @JoinColumn(name = "technician_id"), // FK pointing to Technician
            indexes = {
                    @Index(name = "idx_job_card_tech_job_id", columnList = "job_id"),
                    @Index(name = "idx_job_card_tech_tech_id", columnList = "technician_id")
            }
    )
    @JsonIgnoreProperties("jobCards")
    private List<Technician> technicians = new ArrayList<>();

    // --- MANY-TO-MANY RELATIONSHIP SETUP ---
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "job_card_labor_activity_mapping", // Name of the underlying join table
            joinColumns = @JoinColumn(name = "job_id"), // FK pointing to JobCard
            inverseJoinColumns = @JoinColumn(name = "labor_activity_id") // FK pointing to LaborActivity
    )
    @JsonIgnoreProperties("jobCards")
    private List<LaborActivity> laborActivities = new ArrayList<>();
}
