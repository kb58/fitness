package com.example.demo.DTO;

import com.example.demo.entity.GoalStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class GoalRequestDTO {
    private String title;
    private String description;
    private Double targetValue;
    private String unit;
    private LocalDate startDate;
    private LocalDate endDate;
}


