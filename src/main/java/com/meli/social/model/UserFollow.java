package com.meli.social.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meli.social.user.impl.User;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_follows")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"follower", "followed"})
@EqualsAndHashCode(of = "id")
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    @JsonIgnore
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    @JsonIgnore
    private User followed;

    @Column(name = "followed_at")
    private LocalDateTime followedAt;

    @PrePersist
    protected void onCreate() {
        followedAt = LocalDateTime.now();
    }

    public UserFollow(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
    }
}