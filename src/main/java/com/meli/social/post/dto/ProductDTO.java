package com.meli.social.post.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.meli.social.post.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank
    @Size(max = 40)
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$")
    private String productName;

    @NotBlank
    @Size(max = 40)
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$")
    private String type;

    @NotBlank
    @Size(max = 40)
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$")
    private String brand;

    @NotBlank
    @Size(max = 15)
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$")
    private String color;

    @Size(max = 15)
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$")
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
