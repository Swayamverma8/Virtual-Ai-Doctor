package com.virtualdoctor.virtual_doctor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private Integer age;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column
    private String allergies;

    @Column(name = "medical_history")
    private String medicalHistory;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}