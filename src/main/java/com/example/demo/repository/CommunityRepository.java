package com.example.demo.repository;

import com.example.demo.module.Community;
import com.example.demo.module.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    // Find communities by name containing the search term
    Page<Community> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Find all public communities
    Page<Community> findByIsPrivateFalse(Pageable pageable);

    // Find communities created by a specific user
    List<Community> findByCreator(User creator);

    // Find communities that a user is a member of
    @Query("SELECT c FROM Community c JOIN c.members m WHERE m = :user")
    List<Community> findCommunitiesByMember(@Param("user") User user);

    // Find a community by its exact name
    Optional<Community> findByNameIgnoreCase(String name);

    // Search communities by name or description
    @Query("SELECT c FROM Community c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Community> searchCommunities(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Check if a user is a member of a specific community
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Community c JOIN c.members m " +
            "WHERE c.id = :communityId AND m.id = :userId")
    boolean isMember(@Param("communityId") Long communityId, @Param("userId") Long userId);
}