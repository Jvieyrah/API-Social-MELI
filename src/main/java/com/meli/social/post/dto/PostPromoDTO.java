package com.meli.social.post.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.meli.social.post.model.Post;
import com.meli.social.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Manter metodos Lombok para herança
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostPromoDTO extends PostDTO {

    @JsonAlias({"hasPromo", "has_promo"})
    private Boolean hasPromo;

    @JsonAlias({"discount"})
    private Double discount;

    @Override
    public Post toEntity(User user, LocalDate date) {
        Post post = super.toEntity(user, date);
        post.setHasPromo(Boolean.TRUE.equals(this.hasPromo));
        post.setDiscount(this.discount);
        return post;
    }

    public static PostPromoDTO fromEntity(Post post) {
        if (post == null) {
            return null;
        }

        PostPromoDTO dto = new PostPromoDTO();
        dto.setUserId(post.getUser() != null ? post.getUser().getUserId() : null);
        dto.setDate(post.getDate() != null ? post.getDate().toString() : null);
        dto.setProduct(ProductDTO.fromEntity(post.getProduct()));
        dto.setCategory(post.getCategory());
        dto.setPrice(post.getPrice());
        dto.setHasPromo(post.getHasPromo());
        dto.setDiscount(post.getDiscount());
        return dto;
    }


}
