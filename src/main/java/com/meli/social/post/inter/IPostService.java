package com.meli.social.post.inter;

import com.meli.social.post.dto.FollowedPostsDTO;
import com.meli.social.post.dto.PostDTO;
import com.meli.social.post.dto.PromoProducsListDTO;
import com.meli.social.post.dto.PromoProductsCountDTO;

public interface IPostService {
    Boolean createPost(PostDTO newPost);

    FollowedPostsDTO getFollowedPosts(Integer userId, String sort);

    PromoProductsCountDTO getPromoProductsCount(Integer userId);

    PromoProducsListDTO getPromoProductsList(Integer userId);

    void likePost(Integer postId, Integer userId);

    void unlikePost(Integer postId, Integer userId);
}
