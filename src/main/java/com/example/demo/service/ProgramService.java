package com.example.demo.service;

import com.example.demo.DTO.ProgramDTO;

import java.util.List;

public interface ProgramService {
    ProgramDTO createProgram(ProgramDTO programDTO);
    ProgramDTO getProgramById(Long id);
    List<ProgramDTO> getAllPrograms();
    ProgramDTO updateProgram(Long id, ProgramDTO programDTO);
    void deleteProgram(Long id);
}