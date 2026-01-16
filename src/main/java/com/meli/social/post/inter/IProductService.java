package com.meli.social.post.inter;

import com.meli.social.post.dto.ProductDTO;

public interface IProductService {

    ProductDTO getProductById(Integer productId);
}
