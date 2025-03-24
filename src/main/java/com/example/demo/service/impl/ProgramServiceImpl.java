package com.example.demo.service.impl;

import com.example.demo.DTO.ProgramDTO;
import com.example.demo.DTO.SessionDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.module.Program;
import com.example.demo.module.Session;
import com.example.demo.repository.ProgramRepository;
import com.example.demo.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepository;

    @Override
    public ProgramDTO createProgram(ProgramDTO programDTO) {
        Program program = Program.builder()
                .name(programDTO.getName())
                .description(programDTO.getDescription())
                .startDate(programDTO.getStartDate())
                .endDate(programDTO.getEndDate())
                .isActive(programDTO.isActive())
                .build();
        program = programRepository.save(program);
        return mapToDTO(program);
    }

    @Override
    public ProgramDTO getProgramById(Long id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + id));
        return mapToDTO(program);
    }

    @Override
    public List<ProgramDTO> getAllPrograms() {
        return programRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProgramDTO updateProgram(Long id, ProgramDTO programDTO) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + id));
        program.setName(programDTO.getName());
        program.setDescription(programDTO.getDescription());
        program.setStartDate(programDTO.getStartDate());
        program.setEndDate(programDTO.getEndDate());
        program.setActive(programDTO.isActive());
        program = programRepository.save(program);
        return mapToDTO(program);
    }

    @Override
    public void deleteProgram(Long id) {
        if (!programRepository.existsById(id)) {
            throw new ResourceNotFoundException("Program not found with id: " + id);
        }
        programRepository.deleteById(id);
    }

    private ProgramDTO mapToDTO(Program program) {
        return ProgramDTO.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .startDate(program.getStartDate())
                .endDate(program.getEndDate())
                .isActive(program.isActive())
                .build();
    }
}
