package com.meli.social.post.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meli.social.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data
@ToString(exclude = {"user", "likes"})
@EqualsAndHashCode(of = "postId")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "category")
    @NotNull
    private Integer category;

    @Column(name = "price",  nullable = false)
    @NotNull
    @DecimalMax("10000000")
    private Double price;

    @Column(name = "has_promo")
    private Boolean hasPromo = false;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<PostLike> likes = new HashSet<>();
}