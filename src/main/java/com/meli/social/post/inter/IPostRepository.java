package com.meli.social.post.inter;

import com.meli.social.post.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPostRepository {

    Post save(Post post);

    Optional<Post> findById(Integer postId);

    List<Post> findPostsByUserIdInAndDateBetween(List<Integer> userIds, LocalDate startDate, LocalDate endDate, Sort sort);

    List<Post> findPostsByUserIdInAndDateBetween(List<Integer> userIds, LocalDate startDate, LocalDate endDate, Pageable pageable);

    long countPromoPostsByUserId(Integer userId);

    List<Post> findPromoPostsByUserId(Integer userId);

    List<Post> findPromoPostsByUserId(Integer userId, Pageable pageable);

}
