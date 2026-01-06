package com.meli.social.post.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.meli.social.post.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDTO {

    @JsonAlias("productId")
    private Integer productId;

    @JsonAlias("productName")
    private String productName;

    @JsonAlias("type")
    private String type;

    @JsonAlias("brand")
    private String brand;

    @JsonAlias("color")
    private String color;

    @JsonAlias("notes")
    private String notes;

    public Product toEntity() {
        return new Product(
                this.productId,
                this.productName,
                this.type,
                this.brand,
                this.color,
                this.notes
        );
    }

    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductDTO(
                product.getProductId(),
                product.getProductName(),
                product.getType(),
                product.getBrand(),
                product.getColor(),
                product.getNotes()
        );
    }
}
