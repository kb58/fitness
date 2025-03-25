package com.example.demo.controller;

import com.example.demo.DTO.CommunityCreateDTO;
import com.example.demo.DTO.CommunityDTO;
import com.example.demo.module.User;
import com.example.demo.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {

    private final CommunityService communityService;

    @Autowired
    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDTO> getCommunityById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        CommunityDTO communityDTO = communityService.getCommunityById(id, currentUser.getId());
        return ResponseEntity.ok(communityDTO);
    }

    @PostMapping
    public ResponseEntity<CommunityDTO> createCommunity(
            @Valid @RequestBody CommunityCreateDTO communityDTO,
            @AuthenticationPrincipal User currentUser) {
        CommunityDTO createdCommunity = communityService.createCommunity(communityDTO, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCommunity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityDTO> updateCommunity(
            @PathVariable Long id,
            @Valid @RequestBody CommunityCreateDTO communityDTO,
            @AuthenticationPrincipal User currentUser) {
        CommunityDTO updatedCommunity = communityService.updateCommunity(id, communityDTO, currentUser.getId());
        return ResponseEntity.ok(updatedCommunity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        communityService.deleteCommunity(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<CommunityDTO>> getAllPublicCommunities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @AuthenticationPrincipal User currentUser) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<CommunityDTO> communities = communityService.getAllPublicCommunities(pageable, currentUser.getId());
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CommunityDTO>> searchCommunities(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityDTO> communities = communityService.searchCommunities(query, pageable, currentUser.getId());
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommunityDTO>> getUserCommunities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityDTO> communities = communityService.getUserCommunities(userId, pageable, currentUser.getId());
        return ResponseEntity.ok(communities);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinCommunity(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        communityService.joinCommunity(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveCommunity(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        communityService.leaveCommunity(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/member-status")
    public ResponseEntity<Boolean> checkMemberStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        boolean isMember = communityService.isMember(id, currentUser.getId());
        return ResponseEntity.ok(isMember);
    }
}