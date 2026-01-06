package com.meli.social.post.inter;

import com.meli.social.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository  extends JpaRepository<Post, Integer> {

}
