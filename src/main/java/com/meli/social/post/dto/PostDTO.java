package com.meli.social.post.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.meli.social.post.model.Post;
import com.meli.social.user.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostDTO {

    @JsonAlias({"userId", "user_id"})
    private Integer userId;

    @JsonAlias({"date"})
    private String date;

    @Valid
    @NotNull
    @JsonAlias({"product"})
    private ProductDTO product;

    @NotNull
    @JsonAlias({"category"})
    private Integer category;

    @NotNull
    @DecimalMax("10000000")
    @JsonAlias({"price"})
    private Double price;

    public Post toEntity(User user, LocalDate date) {
        Post post = new Post();
        post.setUser(user);
        post.setDate(date);
        post.setProduct(this.product != null ? this.product.toEntity() : null);
        post.setCategory(this.category);
        post.setPrice(this.price);
        return post;
    }

    public static PostDTO fromEntity(Post post) {
        if (post == null) {
            return null;
        }

        return new PostDTO(
                post.getUser() != null ? post.getUser().getUserId() : null,
                post.getDate() != null ? post.getDate().toString() : null,
                ProductDTO.fromEntity(post.getProduct()),
                post.getCategory(),
                post.getPrice()
        );
    }
}
