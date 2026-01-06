package com.meli.social.post.impl;

import com.meli.social.post.dto.PostDTO;
import com.meli.social.post.inter.IProductRepository;
import com.meli.social.post.inter.IPostRepository;
import com.meli.social.post.inter.IPostService;
import com.meli.social.post.model.Post;
import com.meli.social.post.model.Product;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService implements IPostService {
    private final IPostRepository postRepository;
    private final IProductRepository productRepository;
    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public Boolean createPost(PostDTO newPost) {
        if (newPost == null) {
            throw new IllegalArgumentException("Post não pode ser nulo");
        }
        if (newPost.getUserId() == null) {
            throw new IllegalArgumentException("UserId não pode ser nulo");
        }

        User user = userRepository.findById(newPost.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + newPost.getUserId()));

        Post post = newPost.toEntity(user, resolveDate(newPost.getDate()));

        post.setProduct(resolveProduct(post.getProduct()));

        postRepository.save(post);
        return true;
    }

    private Product resolveProduct(Product product) {
        if (product == null) {
            return null;
        }
        if (product.getProductId() == null) {
            throw new IllegalArgumentException("ProductId não pode ser nulo");
        }

        return productRepository.findById(product.getProductId())
                .orElseGet(() -> productRepository.save(product));
    }

    private LocalDate resolveDate(String date) {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        if (date == null || date.trim().isEmpty()) {
            return LocalDate.now(zoneId);
        }

        String trimmed = date.trim();
        try {
            return LocalDate.parse(trimmed);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Data inválida: " + date);
        }
    }

}
