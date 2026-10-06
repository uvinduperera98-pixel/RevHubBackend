package com.revHub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_card_history")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobCardHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long jobHistoryid;

    @Column(name = "job_id", nullable = false)
    private Long jobId; // Reference to the original JobCard ID

    @Column(name = "job_card_number", length = 50)
    private String jobCardNumber;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId; // Storing the foreign key ID avoids heavy object mapping in history

    @Column(name = "estimated_completion_time")
    private LocalDateTime estimatedCompletionTime;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "customer_complaint_text", columnDefinition = "TEXT")
    private String customerComplaintText;

    @Column(name = "current_mileage")
    private Double currentMileage;

    @Column(name = "action_type", length = 30)
    private String actionType;
}
