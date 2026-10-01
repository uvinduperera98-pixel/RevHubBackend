package com.revHub.entity;

import com.revHub.entity.enums.MeasuringUnitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "item_history",
        indexes = {
                @Index(name = "idx_item_history_item_id", columnList = "item_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long itemHistoryId;

    @Column(name = "item_id", nullable = false)
    private Long itemId; // Reference to original Item ID

    @Column(name = "item_name", length = 255)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "measuring_unit_type", length = 100)
    private MeasuringUnitType measuringUnitType;

    @Column(name = "balance_qty")
    private double balanceQty;

    @Column(name = "supplier_price")
    private double supplierPrice;

    @Column(name = "selling_price")
    private double sellingPrice;

    @Column(name = "action_type", length = 30)
    private String actionType; // e.g., "UPDATE", "DELETE"
}
