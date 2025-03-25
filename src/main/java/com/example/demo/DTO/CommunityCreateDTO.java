package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCreateDTO {
    @NotBlank(message = "Community name is required")
    @Size(min = 3, max = 100, message = "Community name must be between 3 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private String imageUrl;

    private boolean isPrivate;
}