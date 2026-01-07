package com.meli.social.post.impl;

import com.meli.social.post.inter.IPostRepository;
import com.meli.social.post.inter.PostJpaRepository;
import com.meli.social.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository  implements IPostRepository {

    private final PostJpaRepository postJpaRepository;

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public List<Post> findPostsByUserIdInAndDateBetween(List<Integer> userIds, LocalDate startDate, LocalDate endDate, Sort sort) {
        return postJpaRepository.findByUser_UserIdInAndDateBetween(userIds, startDate, endDate, sort);
    }

}
