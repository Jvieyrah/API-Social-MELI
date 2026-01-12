package com.meli.social.post.inter;

import com.meli.social.post.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeJpaRepository extends JpaRepository<PostLike, Long> {

    boolean existsByUser_UserIdAndPost_PostId(Integer userId, Integer postId);

    long deleteByUser_UserIdAndPost_PostId(Integer userId, Integer postId);
}
