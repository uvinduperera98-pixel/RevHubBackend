package com.revHub.integration.vehiclesdb.entity;

import com.revHub.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "vehicle_make",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vehicle_make_normalized",
                        columnNames = "normalized_name"
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleMake extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "normalized_name", nullable = false, length = 150)
    private String normalizedName;

    @Column(length = 150)
    private String slug;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(nullable = false, length = 50)
    private String source = "VEHICLESDB";
}
