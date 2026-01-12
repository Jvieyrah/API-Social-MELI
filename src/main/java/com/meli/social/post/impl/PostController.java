package com.meli.social.post.impl;

import com.meli.social.post.dto.*;
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

    @PostMapping("/promo-pub")
    public ResponseEntity<Void> publishPromo(@Valid @RequestBody PostPromoDTO postPromoDTO) {
        postService.createPost(postPromoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<FollowedPostsDTO> getFeed(
            @PathVariable Integer userId,
            @RequestParam(required = false) String order) {
        return ResponseEntity.ok(postService.getFollowedPosts(userId, order));
    }

    @GetMapping("/promo-pub/count")
    public ResponseEntity<PromoProductsCountDTO> getPromoProductsCount(
            @RequestParam Integer userId) {
        return ResponseEntity.ok(postService.getPromoProductsCount(userId));
    }

    @GetMapping("/promo-pub/list")
    public ResponseEntity<PromoProducsListDTO> getPromoProductsList(
            @RequestParam Integer userId) {
        return ResponseEntity.ok(postService.getPromoProductsList(userId));
    }

    @PostMapping("/{postId}/like/{userId}")
    public ResponseEntity<Void> likePost(
            @PathVariable Integer postId,
            @PathVariable Integer userId
    ) {
        postService.likePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/unlike/{userId}")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Integer postId,
            @PathVariable Integer userId
    ) {
        postService.unlikePost(postId, userId);
        return ResponseEntity.ok().build();
    }

}
