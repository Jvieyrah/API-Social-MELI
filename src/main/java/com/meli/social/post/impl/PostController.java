package com.meli.social.post.impl;

import com.meli.social.post.dto.FollowedPostsDTO;
import com.meli.social.post.dto.PostDTO;
import com.meli.social.post.inter.IPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;

    @PostMapping("/publish")
    public ResponseEntity<Void> publish(@Valid @RequestBody PostDTO postDTO) {
        postService.createPost(postDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<FollowedPostsDTO> getFeed(
            @PathVariable Integer userId,
            @RequestParam(required = false) String order) {
        return ResponseEntity.ok(postService.getFollowedPosts(userId, order));
    }

}
