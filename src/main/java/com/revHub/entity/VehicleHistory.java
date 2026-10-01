package com.revHub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "vehicle_history",
        indexes = {
                @Index(name = "idx_vehicle_history_vehicle_id", columnList = "vehicle_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long vehicleHistoryId;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId; // Reference to original Vehicle ID

    @Column(name = "customer_id", nullable = false)
    private Long customerId; // Reference to owner Customer ID

    @Column(name = "vehicle_reg_no", length = 100)
    private String vehicleRegNo;

    @Column(name = "vehicle_vin_no", length = 100)
    private String vehicleVinNo;

    @Column(name = "vehicle_make", length = 100)
    private String vehicleMake;

    @Column(name = "vehicle_model", length = 100)
    private String vehicleModel;

    @Column(name = "vehicle_year")
    private int vehicleYear;

    @Column(name = "colour")
    private String colour;

    @Column(name = "other_specs")
    private String otherSpecs;

    @Column(name = "action_type", length = 30)
    private String actionType; // e.g., "UPDATE", "DELETE"
}
