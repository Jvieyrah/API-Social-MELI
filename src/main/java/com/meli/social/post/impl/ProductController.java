package com.meli.social.post.impl;

import com.meli.social.post.inter.IProductService;
import com.meli.social.post.dto.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Operações relacionadas a produtos")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final IProductService productService;

    @GetMapping("/{productId}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID do produto", example = "5")
            @PathVariable Integer productId
    ) {
        logger.info("Request to get product productId={}", productId);
        return ResponseEntity.ok(productService.getProductById(productId));
    }
}
