package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long creatorId;
    private String creatorUsername;
    private Set<Long> memberIds;
    private int memberCount;
    private int discussionCount;
    private String imageUrl;
    private boolean isPrivate;
    private boolean member; // Whether the current user is a member
}