package com.example.demo.DTO;

import com.example.demo.entity.GoalStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class GoalResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Double targetValue;
    private Double currentValue;
    private String unit;
    private GoalStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}