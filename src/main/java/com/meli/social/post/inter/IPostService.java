package com.meli.social.post.inter;

import com.meli.social.post.dto.FollowedPostsDTO;
import com.meli.social.post.dto.PostDTO;
import com.meli.social.post.dto.PromoProducsListDTO;
import com.meli.social.post.dto.PromoProductsCountDTO;

public interface IPostService {
    Boolean createPost(PostDTO newPost);

    FollowedPostsDTO getFollowedPosts(Integer userId, String sort);

    FollowedPostsDTO getFollowedPosts(Integer userId, String sort, int page, int size);

    PromoProductsCountDTO getPromoProductsCount(Integer userId);

    PromoProducsListDTO getPromoProductsList(Integer userId);

    PromoProducsListDTO getPromoProductsList(Integer userId, int page, int size);

    void likePost(Integer postId, Integer userId);

    void unlikePost(Integer postId, Integer userId);
}
