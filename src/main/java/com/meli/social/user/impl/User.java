package com.meli.social.user.impl;

import com.meli.social.model.Post;
import com.meli.social.model.PostLike;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name", nullable = false, length = 15)
    private String userName;

    @Column(name = "followers_count")
    private Integer followersCount = 0;

    // Relacionamento: usuários que EU sigo
    @ManyToMany
    @JoinTable(
            name = "user_follows",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    private Set<User> following = new HashSet<>();

    // Relacionamento: usuários que ME seguem
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    // Posts criados pelo usuário
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();

    // Posts que o usuário curtiu
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLike> likedPosts = new HashSet<>();
}