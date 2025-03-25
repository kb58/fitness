package com.example.demo.mapper;

import com.example.demo.DTO.CommunityDTO;
import com.example.demo.module.Community;
import com.example.demo.module.User;
import com.example.demo.repository.DiscussionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper class to convert between Community entity and CommunityDTO
 */
@Component
public class CommunityMapper {

    private final DiscussionRepository discussionRepository;

    @Autowired
    public CommunityMapper(DiscussionRepository discussionRepository) {
        this.discussionRepository = discussionRepository;
    }

    /**
     * Convert Community entity to CommunityDTO
     *
     * @param community The Community entity to convert
     * @param currentUserId The ID of the current user, used to check membership status
     * @return The converted CommunityDTO
     */
    public CommunityDTO toDTO(Community community, Long currentUserId) {
        if (community == null) {
            return null;
        }

        CommunityDTO dto = new CommunityDTO();
        dto.setId(community.getId());
        dto.setName(community.getName());
        dto.setDescription(community.getDescription());
        dto.setCreatedAt(community.getCreatedAt());
        dto.setUpdatedAt(community.getUpdatedAt());
        dto.setCreatorId(community.getCreator().getId());
        dto.setCreatorUsername(community.getCreator().getUsername());

        // Set member IDs
        dto.setMemberIds(community.getMembers().stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        dto.setMemberCount(community.getMembers().size());

        // Set discussion count
        dto.setDiscussionCount((int) discussionRepository.findByCommunity(community, Pageable.unpaged()).getTotalElements());

        dto.setImageUrl(community.getImageUrl());
        dto.setPrivate(community.isPrivate());

        // Check if current user is a member
        dto.setMember(community.getMembers().stream()
                .anyMatch(member -> member.getId().equals(currentUserId)));

        return dto;
    }
}