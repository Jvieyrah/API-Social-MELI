package com.meli.social.post.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.meli.social.user.dto.UserSimpleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Manter metodos Lombok para herança
@JsonPropertyOrder({"userId", "userName", "posts"})
public class PromoProducsListDTO extends UserSimpleDTO {
    private List<PostPromoDTO> posts;

    public PromoProducsListDTO(Integer userId, String userName, List<PostPromoDTO> posts) {
        super(userId, userName);
        this.posts = posts;
    }
}
