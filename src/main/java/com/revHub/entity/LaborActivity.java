package com.revHub.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "labor_activity")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LaborActivity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "labor_activity_id")
    private Long laborActivityId;

    @Column(name = "activity_name", length = 255, nullable = false)
    private String activityName; // e.g., "Wheel Alignment", "Brake Pad Replacement Labor", "Body Painting per Panel"

    @Column(name = "hourly_rate")
    private double hourlyRate = 0.0; // Standard hourly charge if calculated by time

    @Column(name = "flat_rate_charge")
    private double flatRateCharge = 0.0; // Standard flat price fixed for this job type

    @Column(name = "estimated_duration_hours")
    private double estimatedDurationHours = 0.0; // Standard diagnostic time allocation (e.g., 1.5 hours)

    @Column(name = "active", nullable = false)
    private Boolean active = true; // For soft-deleting/deactivating activities without breaking old invoices

    // mappedBy points to the variable name in the JobCard entity
    @ManyToMany(mappedBy = "laborActivities", fetch = FetchType.LAZY)
    private List<JobCard> jobCards = new ArrayList<>();

    // --- MANY-TO-MANY RELATIONSHIP SETUP ---
    @ManyToMany(mappedBy = "laborActivities")
    private List<Item> items = new ArrayList<>();


}
