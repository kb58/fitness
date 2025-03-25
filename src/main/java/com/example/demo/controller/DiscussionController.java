package com.example.demo.controller;

import com.example.demo.DTO.CommentCreateDTO;
import com.example.demo.DTO.CommentDTO;
import com.example.demo.DTO.DiscussionCreateDTO;
import com.example.demo.DTO.DiscussionDTO;
import com.example.demo.module.User;
import com.example.demo.service.DiscussionService;
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

import java.util.List;

@RestController
@RequestMapping("/api/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;

    @Autowired
    public DiscussionController(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscussionDTO> getDiscussionById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        DiscussionDTO discussionDTO = discussionService.getDiscussionById(id, currentUser.getId());
        return ResponseEntity.ok(discussionDTO);
    }

    @PostMapping
    public ResponseEntity<DiscussionDTO> createDiscussion(
            @Valid @RequestBody DiscussionCreateDTO discussionDTO,
            @AuthenticationPrincipal User currentUser) {
        DiscussionDTO createdDiscussion = discussionService.createDiscussion(discussionDTO, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDiscussion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscussionDTO> updateDiscussion(
            @PathVariable Long id,
            @Valid @RequestBody DiscussionCreateDTO discussionDTO,
            @AuthenticationPrincipal User currentUser) {
        DiscussionDTO updatedDiscussion = discussionService.updateDiscussion(id, discussionDTO, currentUser.getId());
        return ResponseEntity.ok(updatedDiscussion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscussion(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        discussionService.deleteDiscussion(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<Page<DiscussionDTO>> getDiscussionsByCommunity(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @AuthenticationPrincipal User currentUser) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<DiscussionDTO> discussions = discussionService.getDiscussionsByCommunity(communityId, pageable, currentUser.getId());
        return ResponseEntity.ok(discussions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<DiscussionDTO>> getDiscussionsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DiscussionDTO> discussions = discussionService.getDiscussionsByUser(userId, pageable, currentUser.getId());
        return ResponseEntity.ok(discussions);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DiscussionDTO>> searchDiscussions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DiscussionDTO> discussions = discussionService.searchDiscussions(query, pageable, currentUser.getId());
        return ResponseEntity.ok(discussions);
    }

    @GetMapping("/trending")
    public ResponseEntity<Page<DiscussionDTO>> getTrendingDiscussions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DiscussionDTO> discussions = discussionService.getTrendingDiscussions(pageable, currentUser.getId());
        return ResponseEntity.ok(discussions);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeDiscussion(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        discussionService.likeDiscussion(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<Void> unlikeDiscussion(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        discussionService.unlikeDiscussion(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    // Comments endpoints
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        List<CommentDTO> comments = discussionService.getCommentsByDiscussion(id, currentUser.getId());
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentDTO> createComment(
            @Valid @RequestBody CommentCreateDTO commentDTO,
            @AuthenticationPrincipal User currentUser) {
        CommentDTO createdComment = discussionService.createComment(commentDTO, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateDTO commentDTO,
            @AuthenticationPrincipal User currentUser) {
        CommentDTO updatedComment = discussionService.updateComment(id, commentDTO, currentUser.getId());
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        discussionService.deleteComment(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{id}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        discussionService.likeComment(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments/{id}/unlike")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        discussionService.unlikeComment(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}