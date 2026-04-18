package com.virtualdoctor.virtual_doctor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500)
    private String diagnosis;

    @Column
    private String severity;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}