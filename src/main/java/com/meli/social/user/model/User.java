package com.meli.social.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meli.social.post.model.Post;
import com.meli.social.post.model.PostLike;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"following", "followers", "posts", "likedPosts"})
@EqualsAndHashCode(of = "userId")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name", unique = true, nullable = false, length = 15)
    @NotNull
    @Size(max = 15)
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$")
    private String userName;

    @Column(name = "followers_count")
    private Integer followersCount = 0;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY )
    @JsonIgnore
    private Set<UserFollow> following = new HashSet<>();

    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY )
    @JsonIgnore
    private Set<UserFollow> followers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<PostLike> likedPosts = new HashSet<>();

    public User(String userName) {
        this.userName = userName;
        this.followersCount = 0;
        this.followers = new HashSet<>();
        this.following = new HashSet<>();
    }

    public void follow(User userToFollow) {
        UserFollow userFollow = new UserFollow(this, userToFollow);
        this.following.add(userFollow);
        userToFollow.getFollowers().add(userFollow);
        userToFollow.incrementFollowersCount();
    }

    public void unfollow(User userToUnfollow) {
        this.following.removeIf(uf -> uf.getFollowed().equals(userToUnfollow));
        userToUnfollow.getFollowers().removeIf(uf -> uf.getFollower().equals(this));
        userToUnfollow.decrementFollowersCount();
    }

    public void incrementFollowersCount() {
        this.followersCount = (this.followersCount == null ? 0 : this.followersCount) + 1;
    }

    public void decrementFollowersCount() {
        this.followersCount = Math.max(0, (this.followersCount == null ? 0 : this.followersCount) - 1);
    }
}