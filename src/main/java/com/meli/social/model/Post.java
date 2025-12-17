package com.meli.social.model;

import com.meli.social.user.impl.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Embedded
    private Product product;

    @Column(name = "category")
    private Integer category;

    @Column(name = "price")
    private Double price;

    @Column(name = "has_promo")
    private Boolean hasPromo = false;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    // Relacionamento com os likes
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLike> likes = new HashSet<>();
}