package com.meli.social.post.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meli.social.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "post"})
@EqualsAndHashCode(of = "id")
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    @Column(name = "liked_at")
    private LocalDateTime likedAt;

    @PrePersist
    protected void onCreate() {
        likedAt = LocalDateTime.now();
    }
}