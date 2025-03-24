package com.example.demo.controller;

import com.example.demo.DTO.GoalRequestDTO;
import com.example.demo.DTO.GoalResponseDTO;
import com.example.demo.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    // ✅ Create a new goal - Only User can create a goal
    @PostMapping
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<GoalResponseDTO> createGoal(@RequestBody GoalRequestDTO goalRequestDTO,
                                                      @RequestParam Long userId) {
        return ResponseEntity.ok(goalService.createGoal(userId, goalRequestDTO));
    }

    // ✅ Update an existing goal - Only User can update their goals
    @PutMapping("/{goalId}")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<GoalResponseDTO> updateGoal(@PathVariable Long goalId,
                                                      @RequestParam Long userId,
                                                      @RequestBody GoalRequestDTO goalRequestDTO) {
        return ResponseEntity.ok(goalService.updateGoal(goalId, userId, goalRequestDTO));
    }

    // ✅ View all goals of a user
    @GetMapping
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<List<GoalResponseDTO>> getAllGoals(@RequestParam Long userId) {
        return ResponseEntity.ok(goalService.getAllGoals(userId));
    }

    // ✅ View a specific goal by ID
    @GetMapping("/{goalId}")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<GoalResponseDTO> getGoalById(@PathVariable Long goalId,
                                                       @RequestParam Long userId) {
        return ResponseEntity.ok(goalService.getGoalById(goalId, userId));
    }

    // ✅ Delete a goal by ID - Only User can delete their own goals
    @DeleteMapping("/{goalId}")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<String> deleteGoal(@PathVariable Long goalId,
                                             @RequestParam Long userId) {
        goalService.deleteGoal(goalId, userId);
        return ResponseEntity.ok("Goal deleted successfully");
    }

    // ✅ Update goal progress
    @PatchMapping("/{goalId}/progress")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<GoalResponseDTO> updateGoalProgress(@PathVariable Long goalId,
                                                              @RequestParam Long userId,
                                                              @RequestParam Double progressValue) {
        return ResponseEntity.ok(goalService.updateGoalProgress(goalId, userId, progressValue));
    }
}
