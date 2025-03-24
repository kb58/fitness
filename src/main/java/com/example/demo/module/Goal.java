package com.example.demo.module;

import com.example.demo.entity.GoalStatus;
import com.example.demo.module.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private Double targetValue; // e.g., Target weight, time, distance, etc.

    @Column(nullable = false)
    private Double currentValue = 0.0;

    @Column(nullable = false)
    private String unit; // kg, km, hours, etc.

    @Enumerated(EnumType.STRING)
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    private LocalDate startDate;
    private LocalDate endDate;
}
