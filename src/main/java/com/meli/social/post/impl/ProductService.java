package com.meli.social.post.impl;

import com.meli.social.exception.ProductNotFoundException;
import com.meli.social.post.dto.ProductDTO;
import com.meli.social.post.inter.IProductRepository;
import com.meli.social.post.inter.IProductService;
import com.meli.social.post.model.Product;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService implements IProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final IProductRepository productRepository;

    @Override
    public ProductDTO getProductById(Integer productId) {
        logger.info("Fetching product productId={}", productId);
        if (productId == null) {
            logger.warn("Invalid productId (null)");
            throw new IllegalArgumentException("ProductId não pode ser nulo");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado: " + productId));

        return ProductDTO.fromEntity(product);
    }
}
