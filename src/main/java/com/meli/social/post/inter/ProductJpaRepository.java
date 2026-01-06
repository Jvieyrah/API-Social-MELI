package com.meli.social.post.inter;

import com.meli.social.post.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Integer> {
}
