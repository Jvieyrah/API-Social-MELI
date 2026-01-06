package com.meli.social.post.impl;

import com.meli.social.post.inter.IPostRepository;
import com.meli.social.post.inter.PostJpaRepository;
import com.meli.social.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepository  implements IPostRepository {

    private final PostJpaRepository postJpaRepository;

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

}
