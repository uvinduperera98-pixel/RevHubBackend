package com.revHub.entity;

import com.revHub.entity.enums.MeasuringUnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "invoice_detail")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_detail_id")
    private Long invoiceDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "type", length = 50, nullable = false)
    private String type; // "LABOR" or "PART"

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "labor_activity_id")
    private Long laborActivityId; // Grouping identifier for both labor and items / parts

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "qty", nullable = false)
    private int qty;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false, length = 50)
    private MeasuringUnitType unitType; // Saves unit type (e.g., UNIT, KILO_GRAM)

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "total", nullable = false)
    private double total;
}
