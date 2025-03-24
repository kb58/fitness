package com.example.demo.controller;

import com.example.demo.DTO.ProgramDTO;
import com.example.demo.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    // Only Admin and Trainer can create a program
    @PostMapping
    @PreAuthorize("hasAnyAuthority('admin:create', 'trainer:create')")
    public ResponseEntity<ProgramDTO> createProgram(@RequestBody ProgramDTO programDTO) {
        return ResponseEntity.ok(programService.createProgram(programDTO));
    }

    // Everyone can view programs
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin:read', 'trainer:read', 'user')")
    public ResponseEntity<ProgramDTO> getProgramById(@PathVariable Long id) {
        return ResponseEntity.ok(programService.getProgramById(id));
    }

    // Everyone can view all programs
    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin:read', 'trainer:read', 'user')")
    public ResponseEntity<List<ProgramDTO>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    // Only Admin and Trainer can update
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin:update', 'trainer:update')")
    public ResponseEntity<ProgramDTO> updateProgram(@PathVariable Long id, @RequestBody ProgramDTO programDTO) {
        return ResponseEntity.ok(programService.updateProgram(id, programDTO));
    }

    // Only Admin can delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<String> deleteProgram(@PathVariable Long id) {
        programService.deleteProgram(id);
        return ResponseEntity.ok("Program deleted successfully");
    }
}
