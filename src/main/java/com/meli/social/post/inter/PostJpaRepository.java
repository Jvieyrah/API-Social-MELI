package com.meli.social.post.inter;

import com.meli.social.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import java.util.List;

public interface PostJpaRepository  extends JpaRepository<Post, Integer> {

    List<Post> findByUser_UserIdInAndDateBetween(List<Integer> userIds, LocalDate startDate, LocalDate endDate, Sort sort);

    List<Post> findByUser_UserIdInAndDateBetween(List<Integer> userIds, LocalDate startDate, LocalDate endDate, Pageable pageable);

    long countByUser_UserIdAndHasPromoTrue(Integer userId);

    List<Post> findByUser_UserIdAndHasPromoTrue(Integer userId);

    List<Post> findByUser_UserIdAndHasPromoTrue(Integer userId, Pageable pageable);
}
