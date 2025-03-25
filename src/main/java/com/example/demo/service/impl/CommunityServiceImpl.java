package com.example.demo.service.impl;

import com.example.demo.DTO.CommunityCreateDTO;
import com.example.demo.DTO.CommunityDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.module.Community;
import com.example.demo.module.User;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.DiscussionRepository;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepo userRepository;
    private final DiscussionRepository discussionRepository;

    @Autowired
    public CommunityServiceImpl(
            CommunityRepository communityRepository,
            UserRepo userRepository,
            DiscussionRepository discussionRepository) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
        this.discussionRepository = discussionRepository;
    }

    @Override
    public CommunityDTO getCommunityById(Long id, Long currentUserId) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + id));

        // If the community is private, check if the user is a member
        if (community.isPrivate() && !isMember(id, currentUserId)) {
            throw new AccessDeniedException("You do not have access to this private community");
        }

        return convertToCommunityDTO(community, currentUserId);
    }

    @Override
    @Transactional
    public CommunityDTO createCommunity(CommunityCreateDTO communityDTO, Long creatorId) {
        // Check if a community with this name already exists
        if (communityRepository.findByNameIgnoreCase(communityDTO.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A community with this name already exists");
        }

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + creatorId));

        Community community = new Community();
        community.setName(communityDTO.getName());
        community.setDescription(communityDTO.getDescription());
        community.setCreator(creator);
        community.setImageUrl(communityDTO.getImageUrl());
        community.setPrivate(communityDTO.isPrivate());
        community.setCreatedAt(LocalDateTime.now());

        // Add creator as a member
        community.addMember(creator);

        Community savedCommunity = communityRepository.save(community);
        return convertToCommunityDTO(savedCommunity, creatorId);
    }

    @Override
    @Transactional
    public CommunityDTO updateCommunity(Long id, CommunityCreateDTO communityDTO, Long currentUserId) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + id));

        // Check if user is the creator
        if (!community.getCreator().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the community creator can update it");
        }

        // Check if new name already exists for other communities
        if (!community.getName().equalsIgnoreCase(communityDTO.getName()) &&
                communityRepository.findByNameIgnoreCase(communityDTO.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A community with this name already exists");
        }

        community.setName(communityDTO.getName());
        community.setDescription(communityDTO.getDescription());
        community.setImageUrl(communityDTO.getImageUrl());
        community.setPrivate(communityDTO.isPrivate());
        community.setUpdatedAt(LocalDateTime.now());

        Community updatedCommunity = communityRepository.save(community);
        return convertToCommunityDTO(updatedCommunity, currentUserId);
    }

    @Override
    @Transactional
    public void deleteCommunity(Long id, Long currentUserId) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + id));

        // Check if user is the creator
        if (!community.getCreator().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the community creator can delete it");
        }

        communityRepository.delete(community);
    }

    @Override
    public Page<CommunityDTO> getAllPublicCommunities(Pageable pageable, Long currentUserId) {
        Page<Community> communities = communityRepository.findByIsPrivateFalse(pageable);
        return communities.map(community -> convertToCommunityDTO(community, currentUserId));
    }

    @Override
    public Page<CommunityDTO> searchCommunities(String searchTerm, Pageable pageable, Long currentUserId) {
        Page<Community> communities = communityRepository.searchCommunities(searchTerm, pageable);

        // Create a list of filtered DTOs
        List<CommunityDTO> filteredDTOs = communities.getContent().stream()
                .filter(community -> !community.isPrivate() || isMember(community.getId(), currentUserId))
                .map(community -> convertToCommunityDTO(community, currentUserId))
                .collect(Collectors.toList());

        // Return a PageImpl with the filtered DTOs
        return new PageImpl<>(
                filteredDTOs,
                pageable,
                filteredDTOs.size()
        );
    }

    @Override
    public Page<CommunityDTO> getUserCommunities(Long userId, Pageable pageable, Long currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Get all communities where the user is a member
        List<Community> userCommunities = communityRepository.findCommunitiesByMember(user);

        // Convert to DTOs and filter out private ones if needed
        List<CommunityDTO> communityDTOs = userCommunities.stream()
                .filter(community -> !community.isPrivate() || userId.equals(currentUserId) || isMember(community.getId(), currentUserId))
                .map(community -> convertToCommunityDTO(community, currentUserId))
                .collect(Collectors.toList());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), communityDTOs.size());

        List<CommunityDTO> paginatedDTOs = communityDTOs;
        if (start <= end) {
            paginatedDTOs = communityDTOs.subList(start, end);
        }

        return new PageImpl<>(
                paginatedDTOs,
                pageable,
                communityDTOs.size()
        );
    }

    @Override
    @Transactional
    public void joinCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + communityId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if already a member
        if (isMember(communityId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a member of this community");
        }

        community.addMember(user);
        communityRepository.save(community);
    }

    @Override
    @Transactional
    public void leaveCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + communityId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if a member
        if (!isMember(communityId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a member of this community");
        }

        // Don't allow the creator to leave
        if (community.getCreator().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The creator cannot leave the community. Transfer ownership or delete the community instead.");
        }

        community.removeMember(user);
        communityRepository.save(community);
    }

    @Override
    public boolean isMember(Long communityId, Long userId) {
        return communityRepository.isMember(communityId, userId);
    }

    // Helper method to convert Community entity to CommunityDTO
    private CommunityDTO convertToCommunityDTO(Community community, Long currentUserId) {
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
                .collect(java.util.stream.Collectors.toSet()));
        dto.setMemberCount(community.getMembers().size());

        // Set discussion count
        dto.setDiscussionCount((int) discussionRepository.findByCommunity(community, Pageable.unpaged()).getTotalElements());

        dto.setImageUrl(community.getImageUrl());
        dto.setPrivate(community.isPrivate());
        dto.setMember(isMember(community.getId(), currentUserId));

        return dto;
    }
}