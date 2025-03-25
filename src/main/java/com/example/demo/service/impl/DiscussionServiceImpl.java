package com.example.demo.service.impl;

import com.example.demo.DTO.CommentCreateDTO;
import com.example.demo.DTO.CommentDTO;
import com.example.demo.DTO.DiscussionCreateDTO;
import com.example.demo.DTO.DiscussionDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.module.Comment;
import com.example.demo.module.Community;
import com.example.demo.module.Discussion;
import com.example.demo.module.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.DiscussionRepository;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.CommunityService;
import com.example.demo.service.DiscussionService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscussionServiceImpl implements DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final CommunityRepository communityRepository;
    private final UserRepo userRepository;
    private final CommentRepository commentRepository;
    private final CommunityService communityService;

    @Autowired
    public DiscussionServiceImpl(
            DiscussionRepository discussionRepository,
            CommunityRepository communityRepository,
            UserRepo userRepository,
            CommentRepository commentRepository,
            CommunityService communityService) {
        this.discussionRepository = discussionRepository;
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.communityService = communityService;
    }

    // Discussion operations
    @Override
    public DiscussionDTO getDiscussionById(Long id, Long currentUserId) {
        Discussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found with id: " + id));

        // Check if the discussion is in a private community and user has access
        if (discussion.getCommunity().isPrivate() &&
                !communityService.isMember(discussion.getCommunity().getId(), currentUserId)) {
            throw new AccessDeniedException("You do not have access to discussions in this private community");
        }

        return convertToDiscussionDTO(discussion, currentUserId);
    }

    @Override
    @Transactional
    public DiscussionDTO createDiscussion(DiscussionCreateDTO discussionDTO, Long authorId) {
        Community community = communityRepository.findById(discussionDTO.getCommunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + discussionDTO.getCommunityId()));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));

        // Check if the user is a member of the community
        if (!communityService.isMember(community.getId(), authorId)) {
            throw new AccessDeniedException("You must be a member of the community to create a discussion");
        }

        Discussion discussion = new Discussion();
        discussion.setTitle(discussionDTO.getTitle());
        discussion.setContent(discussionDTO.getContent());
        discussion.setAuthor(author);
        discussion.setCommunity(community);
        discussion.setCreatedAt(LocalDateTime.now());

        Discussion savedDiscussion = discussionRepository.save(discussion);
        return convertToDiscussionDTO(savedDiscussion, authorId);
    }

    @Override
    @Transactional
    public DiscussionDTO updateDiscussion(Long id, DiscussionCreateDTO discussionDTO, Long currentUserId) {
        Discussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found with id: " + id));

        // Check if the user is the author
        if (!discussion.getAuthor().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the author can update the discussion");
        }

        // Check if community has changed and validate
        if (!discussion.getCommunity().getId().equals(discussionDTO.getCommunityId())) {
            Community newCommunity = communityRepository.findById(discussionDTO.getCommunityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + discussionDTO.getCommunityId()));

            // Check if the user is a member of the new community
            if (!communityService.isMember(newCommunity.getId(), currentUserId)) {
                throw new AccessDeniedException("You must be a member of the community to move the discussion there");
            }

            discussion.setCommunity(newCommunity);
        }

        discussion.setTitle(discussionDTO.getTitle());
        discussion.setContent(discussionDTO.getContent());
        discussion.setUpdatedAt(LocalDateTime.now());

        Discussion updatedDiscussion = discussionRepository.save(discussion);
        return convertToDiscussionDTO(updatedDiscussion, currentUserId);
    }

    @Override
    @Transactional
    public void deleteDiscussion(Long id, Long currentUserId) {
        Discussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found with id: " + id));

        // Check if the user is the author or a community admin
        if (!discussion.getAuthor().getId().equals(currentUserId) &&
                !discussion.getCommunity().getCreator().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the author or community creator can delete the discussion");
        }

        discussionRepository.delete(discussion);
    }

    @Override
    public Page<DiscussionDTO> getDiscussionsByCommunity(Long communityId, Pageable pageable, Long currentUserId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + communityId));

        // Check if the community is private and the user has access
        if (community.isPrivate() && !communityService.isMember(communityId, currentUserId)) {
            throw new AccessDeniedException("You do not have access to discussions in this private community");
        }

        Page<Discussion> discussions = discussionRepository.findByCommunity(community, pageable);
        return discussions.map(discussion -> convertToDiscussionDTO(discussion, currentUserId));
    }

    @Override
    public Page<DiscussionDTO> getDiscussionsByUser(Long userId, Pageable pageable, Long currentUserId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Get discussions by author (using a repository method)
        List<Discussion> authorDiscussions = discussionRepository.findByAuthor(author);

        // Filter out discussions from private communities that the current user doesn't have access to
        List<DiscussionDTO> filteredDiscussions = authorDiscussions.stream()
                .filter(discussion -> !discussion.getCommunity().isPrivate() ||
                        communityService.isMember(discussion.getCommunity().getId(), currentUserId))
                .map(discussion -> convertToDiscussionDTO(discussion, currentUserId))
                .collect(Collectors.toList());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredDiscussions.size());
        List<DiscussionDTO> paginatedDiscussions = new ArrayList<>();

        if (start <= end) {
            paginatedDiscussions = filteredDiscussions.subList(start, end);
        }

        return new PageImpl<>(paginatedDiscussions, pageable, filteredDiscussions.size());
    }

    @Override
    public Page<DiscussionDTO> searchDiscussions(String query, Pageable pageable, Long currentUserId) {
        Page<Discussion> discussions = discussionRepository.searchDiscussions(query, pageable);

        // Convert to list and filter
        List<DiscussionDTO> filteredDiscussions = discussions.getContent().stream()
                .filter(discussion -> !discussion.getCommunity().isPrivate() ||
                        communityService.isMember(discussion.getCommunity().getId(), currentUserId))
                .map(discussion -> convertToDiscussionDTO(discussion, currentUserId))
                .collect(Collectors.toList());

        return new PageImpl<>(filteredDiscussions, pageable, filteredDiscussions.size());
    }

    @Override
    public Page<DiscussionDTO> getTrendingDiscussions(Pageable pageable, Long currentUserId) {
        Page<Discussion> discussions = discussionRepository.findMostActiveDiscussions(pageable);

        // Convert to list and filter
        List<DiscussionDTO> filteredDiscussions = discussions.getContent().stream()
                .filter(discussion -> !discussion.getCommunity().isPrivate() ||
                        communityService.isMember(discussion.getCommunity().getId(), currentUserId))
                .map(discussion -> convertToDiscussionDTO(discussion, currentUserId))
                .collect(Collectors.toList());

        return new PageImpl<>(filteredDiscussions, pageable, filteredDiscussions.size());
    }

    // Rest of your methods remain unchanged...

    @Override
    @Transactional
    public void likeDiscussion(Long id, Long userId) {
        Discussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user has access to the discussion
        if (discussion.getCommunity().isPrivate() &&
                !communityService.isMember(discussion.getCommunity().getId(), userId)) {
            throw new AccessDeniedException("You do not have access to this discussion");
        }

        // Check if already liked
        if (discussionRepository.hasUserLikedDiscussion(id, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has already liked this discussion");
        }

        discussion.addLike(user);
        discussionRepository.save(discussion);
    }

    @Override
    @Transactional
    public void unlikeDiscussion(Long id, Long userId) {
        Discussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user has access to the discussion
        if (discussion.getCommunity().isPrivate() &&
                !communityService.isMember(discussion.getCommunity().getId(), userId)) {
            throw new AccessDeniedException("You do not have access to this discussion");
        }

        // Check if liked
        if (!discussionRepository.hasUserLikedDiscussion(id, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not liked this discussion");
        }

        discussion.removeLike(user);
        discussionRepository.save(discussion);
    }

    @Override
    public boolean hasLikedDiscussion(Long id, Long userId) {
        return discussionRepository.hasUserLikedDiscussion(id, userId);
    }

    // Comment operations
    @Override
    public List<CommentDTO> getCommentsByDiscussion(Long discussionId, Long currentUserId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found with id: " + discussionId));

        // Check if user has access to the discussion
        if (discussion.getCommunity().isPrivate() &&
                !communityService.isMember(discussion.getCommunity().getId(), currentUserId)) {
            throw new AccessDeniedException("You do not have access to this discussion");
        }

        // Get top-level comments
        List<Comment> topLevelComments = commentRepository.findByDiscussionAndParentIsNullOrderByCreatedAtAsc(discussion, Pageable.unpaged()).getContent();

        // Convert to DTOs with replies
        return topLevelComments.stream()
                .map(comment -> convertToCommentDTOWithReplies(comment, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDTO createComment(CommentCreateDTO commentDTO, Long authorId) {
        Discussion discussion = discussionRepository.findById(commentDTO.getDiscussionId())
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found with id: " + commentDTO.getDiscussionId()));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));

        // Check if user has access to the discussion
        if (discussion.getCommunity().isPrivate() &&
                !communityService.isMember(discussion.getCommunity().getId(), authorId)) {
            throw new AccessDeniedException("You do not have access to this discussion");
        }

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setAuthor(author);
        comment.setDiscussion(discussion);
        comment.setCreatedAt(LocalDateTime.now());

        // Check if this is a reply to another comment
        if (commentDTO.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id: " + commentDTO.getParentId()));

            // Ensure parent comment belongs to the same discussion
            if (!parentComment.getDiscussion().getId().equals(discussion.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent comment does not belong to the specified discussion");
            }

            comment.setParent(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        return convertToCommentDTO(savedComment, authorId);
    }

    @Override
    @Transactional
    public CommentDTO updateComment(Long id, CommentCreateDTO commentDTO, Long currentUserId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Check if the user is the author
        if (!comment.getAuthor().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the author can update the comment");
        }

        comment.setContent(commentDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return convertToCommentDTO(updatedComment, currentUserId);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long currentUserId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Check if the user is the author or a community admin
        if (!comment.getAuthor().getId().equals(currentUserId) &&
                !comment.getDiscussion().getCommunity().getCreator().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the author or community creator can delete the comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void likeComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user has access to the comment's discussion
        if (comment.getDiscussion().getCommunity().isPrivate() &&
                !communityService.isMember(comment.getDiscussion().getCommunity().getId(), userId)) {
            throw new AccessDeniedException("You do not have access to this comment");
        }

        // Check if already liked
        if (commentRepository.hasUserLikedComment(id, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has already liked this comment");
        }

        comment.addLike(user);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void unlikeComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user has access to the comment's discussion
        if (comment.getDiscussion().getCommunity().isPrivate() &&
                !communityService.isMember(comment.getDiscussion().getCommunity().getId(), userId)) {
            throw new AccessDeniedException("You do not have access to this comment");
        }

        // Check if liked
        if (!commentRepository.hasUserLikedComment(id, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not liked this comment");
        }

        comment.removeLike(user);
        commentRepository.save(comment);
    }

    @Override
    public boolean hasLikedComment(Long id, Long userId) {
        return commentRepository.hasUserLikedComment(id, userId);
    }

    // Helper methods
    private DiscussionDTO convertToDiscussionDTO(Discussion discussion, Long currentUserId) {
        DiscussionDTO dto = new DiscussionDTO();
        dto.setId(discussion.getId());
        dto.setTitle(discussion.getTitle());
        dto.setContent(discussion.getContent());
        dto.setAuthorId(discussion.getAuthor().getId());
        dto.setAuthorUsername(discussion.getAuthor().getUsername());
        dto.setCommunityId(discussion.getCommunity().getId());
        dto.setCommunityName(discussion.getCommunity().getName());
        dto.setCreatedAt(discussion.getCreatedAt());
        dto.setUpdatedAt(discussion.getUpdatedAt());
        dto.setCommentCount((int) commentRepository.countByDiscussion(discussion));
        dto.setLikeCount(discussion.getLikes().size());
        dto.setUserHasLiked(discussionRepository.hasUserLikedDiscussion(discussion.getId(), currentUserId));

        return dto;
    }

    private CommentDTO convertToCommentDTO(Comment comment, Long currentUserId) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setAuthorUsername(comment.getAuthor().getUsername());
        dto.setDiscussionId(comment.getDiscussion().getId());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        if (comment.getParent() != null) {
            dto.setParentId(comment.getParent().getId());
        }

        dto.setLikeCount(comment.getLikes().size());
        dto.setUserHasLiked(commentRepository.hasUserLikedComment(comment.getId(), currentUserId));
        dto.setReplies(new ArrayList<>()); // Empty list for direct conversion

        return dto;
    }

    private CommentDTO convertToCommentDTOWithReplies(Comment comment, Long currentUserId) {
        CommentDTO dto = convertToCommentDTO(comment, currentUserId);

        // Recursively add replies
        List<Comment> replies = commentRepository.findByParentOrderByCreatedAtAsc(comment);
        List<CommentDTO> replyDTOs = replies.stream()
                .map(reply -> convertToCommentDTOWithReplies(reply, currentUserId))
                .collect(Collectors.toList());

        dto.setReplies(replyDTOs);
        return dto;
    }
}