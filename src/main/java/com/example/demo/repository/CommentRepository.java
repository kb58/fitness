package com.example.demo.repository;

import com.example.demo.module.Comment;
import com.example.demo.module.Discussion;
import com.example.demo.module.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find comments by discussion
    List<Comment> findByDiscussionOrderByCreatedAtAsc(Discussion discussion);

    // Find top-level comments (no parent) for a discussion with pagination
    Page<Comment> findByDiscussionAndParentIsNullOrderByCreatedAtAsc(Discussion discussion, Pageable pageable);

    // Find replies to a comment
    List<Comment> findByParentOrderByCreatedAtAsc(Comment parent);

    // Find comments by author
    List<Comment> findByAuthor(User author);

    // Find comments liked by a user
    @Query("SELECT c FROM Comment c JOIN c.likes l WHERE l = :user")
    List<Comment> findCommentsLikedByUser(@Param("user") User user);

    // Count comments for a discussion
    long countByDiscussion(Discussion discussion);

    // Check if a user has liked a comment
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Comment c JOIN c.likes l " +
            "WHERE c.id = :commentId AND l.id = :userId")
    boolean hasUserLikedComment(@Param("commentId") Long commentId, @Param("userId") Long userId);
}