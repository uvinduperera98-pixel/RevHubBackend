package com.revHub.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoice")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobCard jobCard;

    @Column(unique = true,name = "invoice_number")
    private String invoiceNumber; // Business Key (e.g., "INV-001")

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

    // Added column for remarks/instructions
    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceDetail> invoiceDetails;
}
