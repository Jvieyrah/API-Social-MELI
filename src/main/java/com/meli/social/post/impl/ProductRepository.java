package com.meli.social.post.impl;

import com.meli.social.post.inter.IProductRepository;
import com.meli.social.post.inter.ProductJpaRepository;
import com.meli.social.post.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepository implements IProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Optional<Product> findById(Integer id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }
}
