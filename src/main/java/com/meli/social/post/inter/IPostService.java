package com.meli.social.post.inter;

import com.meli.social.post.dto.PostDTO;

public interface IPostService {
    Boolean createPost(PostDTO newPost);
}
