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
@ToString(exclude = {"posts", "likedPosts"})
@EqualsAndHashCode(of = "userId")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name", unique = true, nullable = false, length = 15)
    @NotNull
    @Size(max = 15, message = "Nome do usuário deve conter no máximo 15 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Nome do usuário deve conter apenas letras e números")
    private String userName;

    @Column(name = "followers_count")
    private Integer followersCount = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<PostLike> likedPosts = new HashSet<>();

    public User(String userName) {
        this.userName = userName;
        this.followersCount = 0;
    }

    public void incrementFollowersCount() {
        this.followersCount = (this.followersCount == null ? 0 : this.followersCount) + 1;
    }

    public void decrementFollowersCount() {
        this.followersCount = Math.max(0, (this.followersCount == null ? 0 : this.followersCount) - 1);
    }
}