package com.meli.social.post.inter;

import com.meli.social.post.dto.FollowedPostsDTO;
import com.meli.social.post.dto.PostDTO;
import com.meli.social.post.model.Post;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public interface IPostService {
    Boolean createPost(PostDTO newPost);

    FollowedPostsDTO getFollowedPosts(Integer userId, String sort);


}
