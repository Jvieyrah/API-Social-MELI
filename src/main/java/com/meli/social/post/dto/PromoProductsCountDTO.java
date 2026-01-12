package com.meli.social.post.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.meli.social.user.dto.UserSimpleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Manter metodos Lombok para herança
@JsonPropertyOrder({"userId", "userName", "promoProductsCount"})
public class PromoProductsCountDTO  extends UserSimpleDTO {

    private Long promoProductsCount;

    public PromoProductsCountDTO(Integer userId, String userName, Long promoProductsCount) {
        super(userId, userName);
        this.promoProductsCount = promoProductsCount;
    }
}
