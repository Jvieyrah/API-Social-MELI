package com.meli.social.post.impl;

import com.meli.social.post.dto.PostDTO;
import com.meli.social.post.inter.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;

    @PostMapping("/publish")
    public ResponseEntity<Void> publish(@RequestBody PostDTO postDTO) {
        postService.createPost(postDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
