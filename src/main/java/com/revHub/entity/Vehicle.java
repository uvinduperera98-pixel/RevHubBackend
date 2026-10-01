package com.revHub.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "vehicle",
        indexes = {
                @Index(name = "idx_vehicle_reg_no", columnList = "vehicle_reg_no")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vehicle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    // Stored uniquely in exactly one place to prevent data redundancy
    @Column(name = "vehicle_reg_no", length = 100, nullable = false)
    private String vehicleRegNo;

    @Column(name = "vehicle_vin_no", length = 100, nullable = false)
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

    // 1. Change "vehicle" to "vehicles" to match the field name in Customer class
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties("vehicles")
    private Customer customer;

    // 2. Add this to stop JobCard from serializing this vehicle over and over again
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("vehicle")
    private List<JobCard> jobCards;
}
