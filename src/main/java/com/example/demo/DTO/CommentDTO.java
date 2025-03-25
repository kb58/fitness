package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private Long authorId;
    private String authorUsername;
    private Long discussionId;
    private Long parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private boolean userHasLiked;
    private List<CommentDTO> replies;
}