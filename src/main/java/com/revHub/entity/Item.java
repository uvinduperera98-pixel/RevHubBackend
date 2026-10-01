package com.revHub.entity;

import com.revHub.entity.enums.MeasuringUnitType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Item extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false, updatable = false)
    private Long itemId;

    @Column(name = "item_name", length = 255)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "measuring_unit_type", length = 100, nullable = false)
    private MeasuringUnitType measuringUnitType;

    @Column(name = "balance_qty")
    private double balanceQty;

    @Column(name = "supplier_price")
    private double supplierPrice;

    @Column(name = "selling_price")
    private double sellingPrice;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "labor_items_mapping",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "labor_activity_id")
    )
    @JsonIgnore
    private List<LaborActivity> laborActivities = new ArrayList<>();
}
