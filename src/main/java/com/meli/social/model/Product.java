package com.meli.social.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "product_name", nullable = false, length = 40)
    private String productName;

    @Column(name = "type", length = 15)
    private String type;

    @Column(name = "brand", length = 25)
    private String brand;

    @Column(name = "color", length = 15)
    private String color;

    @Column(name = "notes", length = 80)
    private String notes;
}