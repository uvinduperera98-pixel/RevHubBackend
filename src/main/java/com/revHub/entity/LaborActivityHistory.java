package com.revHub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "labor_activity_history",
        indexes = {
                @Index(name = "idx_labor_activity_history_labor_activity_id", columnList = "labor_activity_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LaborActivityHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long laborActivityHistoryId;

    @Column(name = "labor_activity_id", nullable = false)
    private Long laborActivityId; // Reference to original LaborActivity ID

    @Column(name = "activity_name", length = 255)
    private String activityName;

    @Column(name = "hourly_rate")
    private double hourlyRate;

    @Column(name = "flat_rate_charge")
    private double flatRateCharge;

    @Column(name = "estimated_duration_hours")
    private double estimatedDurationHours;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "action_type", length = 30)
    private String actionType; // e.g., "UPDATE", "DELETE"
}
