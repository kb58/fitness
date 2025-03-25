package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDTO {
    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String content;

    @NotNull(message = "Discussion ID is required")
    private Long discussionId;

    private Long parentId; // Optional, for replies to existing comments
}