package com.revHub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_history")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long invoiceHistoryId;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId; // Reference to original Invoice ID

    @Column(name = "job_id", nullable = false)
    private Long jobId; // Reference to associated JobCard ID

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "additional_fees", nullable = false)
    private double additionalFees;

    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(name = "grand_total", nullable = false)
    private double grandTotal;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "action_type", length = 30)
    private String actionType; // e.g., "UPDATE", "VOID", "CANCEL"
}
