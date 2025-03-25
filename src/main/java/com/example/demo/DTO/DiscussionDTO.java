package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionDTO {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorUsername;
    private Long communityId;
    private String communityName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int commentCount;
    private int likeCount;
    private boolean userHasLiked; // Whether the current user has liked this discussion
}