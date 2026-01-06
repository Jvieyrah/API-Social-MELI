package com.meli.social.post.inter;

import com.meli.social.post.model.Product;

import java.util.Optional;

public interface IProductRepository {

    Optional<Product> findById(Integer id);

    Product save(Product product);
}
