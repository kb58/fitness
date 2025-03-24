package com.example.demo.service.impl;

import com.example.demo.DTO.ProgramDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProgramMapper;
import com.example.demo.module.Program;
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
        Program program = ProgramMapper.toEntity(programDTO);
        program = programRepository.save(program);
        return ProgramMapper.toDTO(program);
    }

    @Override
    public ProgramDTO getProgramById(Long id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + id));
        return ProgramMapper.toDTO(program);
    }

    @Override
    public List<ProgramDTO> getAllPrograms() {
        return programRepository.findAll()
                .stream()
                .map(ProgramMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProgramDTO updateProgram(Long id, ProgramDTO programDTO) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + id));

        program.setName(programDTO.getName());
        program.setDescription(programDTO.getDescription());
        program.setStartDate(programDTO.getStartDate());
        program.setEndDate(programDTO.getEndDate());
        program.setActive(programDTO.isActive());

        program = programRepository.save(program);
        return ProgramMapper.toDTO(program);
    }

    @Override
    public void deleteProgram(Long id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + id));

        programRepository.delete(program);
    }
}
