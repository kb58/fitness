package com.example.demo.repository;

import com.example.demo.module.Community;
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
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    // Find discussions by their community
    Page<Discussion> findByCommunity(Community community, Pageable pageable);

    // Find discussions by author
    List<Discussion> findByAuthor(User author);

    // Search discussions by title or content
    @Query("SELECT d FROM Discussion d WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Discussion> searchDiscussions(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Find discussions liked by a user
    @Query("SELECT d FROM Discussion d JOIN d.likes l WHERE l = :user")
    Page<Discussion> findDiscussionsLikedByUser(@Param("user") User user, Pageable pageable);

    // Find most active discussions (with most comments)
    @Query("SELECT d FROM Discussion d LEFT JOIN d.comments c GROUP BY d ORDER BY COUNT(c) DESC")
    Page<Discussion> findMostActiveDiscussions(Pageable pageable);

    // Find recent discussions
    Page<Discussion> findByOrderByCreatedAtDesc(Pageable pageable);

    // Find discussions in user's communities
    @Query("SELECT d FROM Discussion d JOIN d.community c JOIN c.members m WHERE m = :user")
    Page<Discussion> findDiscussionsInUserCommunities(@Param("user") User user, Pageable pageable);

    // Check if a user has liked a discussion
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Discussion d JOIN d.likes l " +
            "WHERE d.id = :discussionId AND l.id = :userId")
    boolean hasUserLikedDiscussion(@Param("discussionId") Long discussionId, @Param("userId") Long userId);
}