package com.example.demo.service;

import com.example.demo.DTO.CommunityCreateDTO;
import com.example.demo.DTO.CommunityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommunityService {

    // Get community by ID
    CommunityDTO getCommunityById(Long id, Long currentUserId);

    // Create a new community
    CommunityDTO createCommunity(CommunityCreateDTO communityDTO, Long creatorId);

    // Update a community
    CommunityDTO updateCommunity(Long id, CommunityCreateDTO communityDTO, Long currentUserId);

    // Delete a community
    void deleteCommunity(Long id, Long currentUserId);

    // Get all public communities with pagination
    Page<CommunityDTO> getAllPublicCommunities(Pageable pageable, Long currentUserId);

    // Search communities by name or description
    Page<CommunityDTO> searchCommunities(String searchTerm, Pageable pageable, Long currentUserId);

    // Get communities a user is a member of
    Page<CommunityDTO> getUserCommunities(Long userId, Pageable pageable, Long currentUserId);

    // Join a community
    void joinCommunity(Long communityId, Long userId);

    // Leave a community
    void leaveCommunity(Long communityId, Long userId);

    // Check if user is a member of a community
    boolean isMember(Long communityId, Long userId);
}