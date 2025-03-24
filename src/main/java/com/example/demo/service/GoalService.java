package com.example.demo.service;

import com.example.demo.DTO.GoalRequestDTO;
import com.example.demo.DTO.GoalResponseDTO;
import java.util.List;

public interface GoalService {
    GoalResponseDTO createGoal(Long userId, GoalRequestDTO goalRequestDTO);
    GoalResponseDTO updateGoal(Long goalId, Long userId, GoalRequestDTO goalRequestDTO);
    List<GoalResponseDTO> getAllGoals(Long userId);
    GoalResponseDTO getGoalById(Long goalId, Long userId);
    void deleteGoal(Long goalId, Long userId);
    GoalResponseDTO updateGoalProgress(Long goalId, Long userId, Double progressValue);
}
