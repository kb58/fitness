package com.example.demo.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "discussions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discussion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "discussion_likes",
            joinColumns = @JoinColumn(name = "discussion_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> likes = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setDiscussion(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setDiscussion(null);
    }

    public void addLike(User user) {
        likes.add(user);
    }

    public void removeLike(User user) {
        likes.remove(user);
    }
}