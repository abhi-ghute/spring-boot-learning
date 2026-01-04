package com.hospital.management.entity;

import com.hospital.management.entity.type.BloodGroupType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(
        name = "patient",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_patient_name_birthdate",columnNames = {"name","birth_date"})
        },
        indexes = {
                @Index(name = "idx_patient_birth_date",columnList = "birth_date")
        }
)
public final class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",length = 40, nullable = false)
    private String name;

    @Column(unique = true,nullable = false)
    private LocalDate birthDate;

    private String email;

    private String gender;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    private BloodGroupType bloodGroup;
}
