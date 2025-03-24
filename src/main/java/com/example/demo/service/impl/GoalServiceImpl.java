package com.example.demo.service.impl;

import com.example.demo.DTO.GoalRequestDTO;
import com.example.demo.DTO.GoalResponseDTO;
import com.example.demo.module.Goal;
import com.example.demo.entity.GoalStatus;
import com.example.demo.module.User;
import com.example.demo.repository.GoalRepository;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.GoalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepo userRepository;

    @Override
    public GoalResponseDTO createGoal(Long userId, GoalRequestDTO goalRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle(goalRequestDTO.getTitle());
        goal.setDescription(goalRequestDTO.getDescription());
        goal.setTargetValue(goalRequestDTO.getTargetValue());
        goal.setUnit(goalRequestDTO.getUnit());
        goal.setStartDate(goalRequestDTO.getStartDate() != null ? goalRequestDTO.getStartDate() : LocalDate.now());
        goal.setEndDate(goalRequestDTO.getEndDate());
        goal.setStatus(GoalStatus.IN_PROGRESS);

        goal = goalRepository.save(goal);
        return mapToDTO(goal);
    }

    @Override
    public GoalResponseDTO updateGoal(Long goalId, Long userId, GoalRequestDTO goalRequestDTO) {
        Goal goal = findGoalByIdAndUser(goalId, userId);

        goal.setTitle(goalRequestDTO.getTitle());
        goal.setDescription(goalRequestDTO.getDescription());
        goal.setTargetValue(goalRequestDTO.getTargetValue());
        goal.setUnit(goalRequestDTO.getUnit());
        goal.setStartDate(goalRequestDTO.getStartDate());
        goal.setEndDate(goalRequestDTO.getEndDate());

        return mapToDTO(goalRepository.save(goal));
    }

    @Override
    public List<GoalResponseDTO> getAllGoals(Long userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        return goals.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public GoalResponseDTO getGoalById(Long goalId, Long userId) {
        Goal goal = findGoalByIdAndUser(goalId, userId);
        return mapToDTO(goal);
    }

    @Override
    public void deleteGoal(Long goalId, Long userId) {
        Goal goal = findGoalByIdAndUser(goalId, userId);
        goalRepository.delete(goal);
    }

    @Override
    public GoalResponseDTO updateGoalProgress(Long goalId, Long userId, Double progressValue) {
        Goal goal = findGoalByIdAndUser(goalId, userId);
        goal.setCurrentValue(goal.getCurrentValue() + progressValue);

        // Check progress
        if (goal.getCurrentValue() >= goal.getTargetValue()) {
            goal.setStatus(GoalStatus.COMPLETED);
        } else if (goal.getEndDate() != null && LocalDate.now().isAfter(goal.getEndDate())) {
            goal.setStatus(GoalStatus.FAILED);
        }

        return mapToDTO(goalRepository.save(goal));
    }

    private Goal findGoalByIdAndUser(Long goalId, Long userId) {
        return goalRepository.findById(goalId)
                .filter(goal -> goal.getUser().getId().equals(userId))
                .orElseThrow(() -> new EntityNotFoundException("Goal not found or access denied"));
    }

    private GoalResponseDTO mapToDTO(Goal goal) {
        GoalResponseDTO dto = new GoalResponseDTO();
        dto.setId(goal.getId());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setTargetValue(goal.getTargetValue());
        dto.setCurrentValue(goal.getCurrentValue());
        dto.setUnit(goal.getUnit());
        dto.setStatus(goal.getStatus());
        dto.setStartDate(goal.getStartDate());
        dto.setEndDate(goal.getEndDate());
        return dto;
    }
}
