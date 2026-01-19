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
public class ProductDTO {

    @JsonAlias({"productId", "product_id"})
    private Integer productId;

    @JsonAlias({"productName", "product_name"})
    @NotBlank
    @Size(max = 40, message = "Nome do produto deve conter no máximo 40 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Nome do produto deve conter apenas letras e números")
    private String productName;

    @NotBlank
    @Size(max = 15, message = "Tipo do produto deve conter no máximo 15 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Tipo do produto deve conter apenas letras e números")
    private String type;

    @NotBlank
    @Size(max = 25, message = "Marca do produto deve conter no máximo 25 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Marca do produto deve conter apenas letras e números")
    private String brand;

    @NotBlank
    @Size(max = 15, message = "Cor do produto deve conter no máximo 15 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Cor do produto deve conter apenas letras e números")
    private String color;

    @Size(max = 80, message = "Notas do produto deve conter no máximo 80 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Notas do produto deve conter apenas letras e números")
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
