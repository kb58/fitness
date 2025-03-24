package com.example.demo.mapper;

import com.example.demo.DTO.ProgramDTO;
import com.example.demo.module.Program;

public class ProgramMapper {

    public static ProgramDTO toDTO(Program program) {
        return ProgramDTO.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .startDate(program.getStartDate())
                .endDate(program.getEndDate())
                .isActive(program.isActive())
                .build();
    }

    public static Program toEntity(ProgramDTO dto) {
        return Program.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(dto.isActive())
                .build();
    }
}
