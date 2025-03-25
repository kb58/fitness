package com.example.demo.service;

import com.example.demo.DTO.CommentCreateDTO;
import com.example.demo.DTO.CommentDTO;
import com.example.demo.DTO.DiscussionCreateDTO;
import com.example.demo.DTO.DiscussionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiscussionService {

    // Discussion operations
    DiscussionDTO getDiscussionById(Long id, Long currentUserId);

    DiscussionDTO createDiscussion(DiscussionCreateDTO discussionDTO, Long authorId);

    DiscussionDTO updateDiscussion(Long id, DiscussionCreateDTO discussionDTO, Long currentUserId);

    void deleteDiscussion(Long id, Long currentUserId);

    Page<DiscussionDTO> getDiscussionsByCommunity(Long communityId, Pageable pageable, Long currentUserId);

    Page<DiscussionDTO> getDiscussionsByUser(Long userId, Pageable pageable, Long currentUserId);

    Page<DiscussionDTO> searchDiscussions(String query, Pageable pageable, Long currentUserId);

    Page<DiscussionDTO> getTrendingDiscussions(Pageable pageable, Long currentUserId);

    void likeDiscussion(Long id, Long userId);

    void unlikeDiscussion(Long id, Long userId);

    boolean hasLikedDiscussion(Long id, Long userId);

    // Comment operations
    List<CommentDTO> getCommentsByDiscussion(Long discussionId, Long currentUserId);

    CommentDTO createComment(CommentCreateDTO commentDTO, Long authorId);

    CommentDTO updateComment(Long id, CommentCreateDTO commentDTO, Long currentUserId);

    void deleteComment(Long id, Long currentUserId);

    void likeComment(Long id, Long userId);

    void unlikeComment(Long id, Long userId);

    boolean hasLikedComment(Long id, Long userId);
}