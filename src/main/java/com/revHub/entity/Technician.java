package com.revHub.entity;

import com.revHub.entity.enums.TechnicianStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "technician")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Technician extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "technician_id")
    private Long technicianId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "technician_name", length = 255, nullable = false)
    private String technicianName;

    @Column(name = "speciality", length = 255)
    private String speciality;

    @Column(name = "technician_contact", length = 50)
    private String technicianContact;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private TechnicianStatus status = TechnicianStatus.AVAILABLE;

    // mappedBy points to the field name inside the JobCard entity
    @ManyToMany(mappedBy = "technicians", fetch = FetchType.LAZY)
    private List<JobCard> assignedJobCards = new ArrayList<>();
}