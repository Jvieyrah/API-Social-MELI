package com.meli.social.post.impl;

import com.meli.social.post.inter.IPostRepository;
import com.meli.social.post.inter.PostJpaRepository;
import com.meli.social.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository  implements IPostRepository {

    private final PostJpaRepository postJpaRepository;

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public Optional<Post> findById(Integer postId) {
        return postJpaRepository.findById(postId);
    }

    @Override
    public List<Post> findPostsByUserIdInAndDateBetween(List<Integer> userIds, LocalDate startDate, LocalDate endDate, Sort sort) {
        return postJpaRepository.findByUser_UserIdInAndDateBetween(userIds, startDate, endDate, sort);
    }

    @Override
    public List<Post> findPostsByUserIdInAndDateBetween(List<Integer> userIds, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return postJpaRepository.findByUser_UserIdInAndDateBetween(userIds, startDate, endDate, pageable);
    }

    @Override
    public long countPromoPostsByUserId(Integer userId) {
        return postJpaRepository.countByUser_UserIdAndHasPromoTrue(userId);
    }

    @Override
    public List<Post> findPromoPostsByUserId(Integer userId) {
        return postJpaRepository.findByUser_UserIdAndHasPromoTrue(userId);
    }

    @Override
    public List<Post> findPromoPostsByUserId(Integer userId, Pageable pageable) {
        return postJpaRepository.findByUser_UserIdAndHasPromoTrue(userId, pageable);
    }

}
