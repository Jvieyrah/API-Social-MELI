package com.meli.social.post.inter;

import com.meli.social.post.model.Post;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public interface IPostRepository {

    Post save(Post post);

    List<Post> findPostsByUserIdInAndDateBetween(List<Integer> userIds, LocalDate startDate, LocalDate endDate, Sort sort);

    long countPromoPostsByUserId(Integer userId);

    List<Post> findPromoPostsByUserId(Integer userId);

}
