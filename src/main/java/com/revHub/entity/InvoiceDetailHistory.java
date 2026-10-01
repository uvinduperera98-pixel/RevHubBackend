package com.revHub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_detail_history")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceDetailHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long invoiceDetailHistoryId;

    @Column(name = "invoice_detail_id", nullable = false)
    private Long invoiceDetailId; // Reference to original InvoiceDetail ID

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId; // Reference to parent Invoice ID

    @Column(name = "type", length = 50, nullable = false)
    private String type; // "LABOR" or "PART"

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "labor_activity_id")
    private Long laborActivityId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "qty", nullable = false)
    private int qty;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "total", nullable = false)
    private double total;

    @Column(name = "action_type", length = 30)
    private String actionType; // e.g., "UPDATE", "DELETE_LINE"
}
