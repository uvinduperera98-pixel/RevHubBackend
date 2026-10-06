package com.revHub.integration.vehiclesdb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "vehicle_model",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vehicle_model_make_name",
                        columnNames = {"make_id", "normalized_name"}
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "make_id", nullable = false)
    private VehicleMake make;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "normalized_name", nullable = false, length = 150)
    private String normalizedName;

    @Column(length = 150)
    private String slug;

    @Column(length = 50)
    private String kind;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(nullable = false, length = 50)
    private String source = "VEHICLESDB";
}
