package com.meli.social.post.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
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