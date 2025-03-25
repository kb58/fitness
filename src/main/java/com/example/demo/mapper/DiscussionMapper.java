package com.example.demo.mapper;

import com.example.demo.DTO.CommentDTO;
import com.example.demo.DTO.DiscussionDTO;
import com.example.demo.module.Comment;
import com.example.demo.module.Discussion;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.DiscussionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Discussion entity and DiscussionDTO, and Comment entity and CommentDTO
 */
@Component
public class DiscussionMapper {

    private final CommentRepository commentRepository;
    private final DiscussionRepository discussionRepository;

    @Autowired
    public DiscussionMapper(CommentRepository commentRepository, DiscussionRepository discussionRepository) {
        this.commentRepository = commentRepository;
        this.discussionRepository = discussionRepository;
    }

    /**
     * Convert Discussion entity to DiscussionDTO
     *
     * @param discussion The Discussion entity to convert
     * @param currentUserId The ID of the current user, used to check like status
     * @return The converted DiscussionDTO
     */
    public DiscussionDTO toDTO(Discussion discussion, Long currentUserId) {
        if (discussion == null) {
            return null;
        }

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

    /**
     * Convert Comment entity to CommentDTO
     *
     * @param comment The Comment entity to convert
     * @param currentUserId The ID of the current user, used to check like status
     * @return The converted CommentDTO
     */
    public CommentDTO toCommentDTO(Comment comment, Long currentUserId) {
        if (comment == null) {
            return null;
        }

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

    /**
     * Convert Comment entity to CommentDTO including all replies
     *
     * @param comment The Comment entity to convert
     * @param currentUserId The ID of the current user, used to check like status
     * @return The converted CommentDTO with replies
     */
    public CommentDTO toCommentDTOWithReplies(Comment comment, Long currentUserId) {
        CommentDTO dto = toCommentDTO(comment, currentUserId);

        if (dto != null) {
            // Recursively add replies
            List<Comment> replies = commentRepository.findByParentOrderByCreatedAtAsc(comment);
            List<CommentDTO> replyDTOs = replies.stream()
                    .map(reply -> toCommentDTOWithReplies(reply, currentUserId))
                    .collect(Collectors.toList());

            dto.setReplies(replyDTOs);
        }

        return dto;
    }

    /**
     * Convert a list of comments to DTOs with replies
     *
     * @param comments The list of Comment entities to convert
     * @param currentUserId The ID of the current user
     * @return A list of converted CommentDTOs with replies
     */
    public List<CommentDTO> toCommentDTOListWithReplies(List<Comment> comments, Long currentUserId) {
        if (comments == null) {
            return new ArrayList<>();
        }

        return comments.stream()
                .map(comment -> toCommentDTOWithReplies(comment, currentUserId))
                .collect(Collectors.toList());
    }
}